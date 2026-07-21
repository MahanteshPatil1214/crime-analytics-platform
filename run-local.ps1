param(
    [string[]]$Services = @("discovery", "gateway", "incident", "person", "graph", "search", "analytics", "conversational-ai", "financial", "report", "notification", "etl"),
    [switch]$SkipBuild,
    [switch]$SkipInfra
)

$ErrorActionPreference = "Continue"
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$LogDir = Join-Path $ProjectRoot "logs"

if (-not (Test-Path $LogDir)) { New-Item -ItemType Directory -Path $LogDir -Force | Out-Null }

$ServiceConfig = @{
    "discovery"        = @{ Port = 8761; Heap = "128m"; InitHeap = "64m";  Depends = @();               Dir = "discovery-service" }
    "gateway"          = @{ Port = 8080; Heap = "128m"; InitHeap = "64m";  Depends = @("discovery");     Dir = "gateway-service" }
    "incident"         = @{ Port = 8082; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "incident-service" }
    "person"           = @{ Port = 8083; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "person-service" }
    "graph"            = @{ Port = 8084; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "graph-service" }
    "search"           = @{ Port = 8085; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "search-service" }
    "analytics"        = @{ Port = 8086; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "analytics-service" }
    "conversational-ai" = @{ Port = 8087; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "conversational-ai-service" }
    "financial"        = @{ Port = 8088; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "financial-service" }
    "report"           = @{ Port = 8089; Heap = "128m"; InitHeap = "64m";  Depends = @("discovery","gateway"); Dir = "report-service" }
    "notification"     = @{ Port = 8090; Heap = "128m"; InitHeap = "64m";  Depends = @("discovery","gateway"); Dir = "notification-service" }
    "etl"              = @{ Port = 8091; Heap = "256m"; InitHeap = "128m"; Depends = @("discovery","gateway"); Dir = "etl-service" }
}

function Write-Banner {
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host " Crime Analytics Platform - Local Dev Launcher" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host " Total RAM: ~15 GB | Max concurrent services: 12" -ForegroundColor Cyan
    Write-Host " Estimated memory usage: ~4 GB for all services" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
}

function Write-Step {
    param([string]$Message)
    Write-Host "" -ForegroundColor Yellow
    Write-Host ">> $Message" -ForegroundColor Yellow
}

function Wait-ForPort {
    param([int]$Port, [int]$TimeoutSeconds = 60)
    $elapsed = 0
    while ($elapsed -lt $TimeoutSeconds) {
        $connection = Test-NetConnection -ComputerName localhost -Port $Port -WarningAction SilentlyContinue -InformationLevel Quiet 2>$null
        if ($connection) { return $true }
        Start-Sleep -Seconds 2
        $elapsed += 2
        Write-Host "." -NoNewline -ForegroundColor Gray
    }
    Write-Host ""
    return $false
}

function Start-Service {
    param(
        [string]$Name,
        [string]$Heap,
        [string]$InitHeap,
        [string]$Dir
    )
    $servicePom = Join-Path (Join-Path $ProjectRoot $Dir) "pom.xml"
    $logFile = Join-Path $LogDir "$Name.log"

    Write-Host "  Starting $Name (heap: $Heap, init: $InitHeap)..." -ForegroundColor Green

    $process = Start-Process -FilePath "mvn" -ArgumentList @(
        "spring-boot:run",
        "-f", $servicePom
    ) -NoNewWindow -RedirectStandardOutput $logFile -RedirectStandardError "${logFile}.err" -PassThru

    return $process
}

# Main
Write-Banner

# Step 0: Build
if (-not $SkipBuild) {
    Write-Step "[0/3] Building all modules (skip with -SkipBuild)..."
    Push-Location $ProjectRoot
    mvn clean install -DskipTests -q
    if ($LASTEXITCODE -ne 0) { Write-Host "Build failed! Run with -SkipBuild to use existing JARs." -ForegroundColor Red; exit 1 }
    Pop-Location
}

# Step 1: Start infrastructure
if (-not $SkipInfra) {
    Write-Step "[1/3] Starting infrastructure (Docker)..."
    Push-Location $ProjectRoot
    docker-compose up -d --remove-orphans 2>&1 | Out-Null
    Pop-Location
    Write-Host "  Docker containers starting..."
}

# Step 2: Start services
Write-Step "[2/3] Starting core services..."
$startedPorts = @{}

foreach ($svcName in $Services) {
    $cfg = $ServiceConfig[$svcName]
    if (-not $cfg) { Write-Host "  Unknown service: $svcName. Skipping." -ForegroundColor Red; continue }

    # Check dependencies
    foreach ($dep in $cfg.Depends) {
        $depPort = $ServiceConfig[$dep].Port
        $depDir = $ServiceConfig[$dep].Dir
        if (-not $startedPorts.ContainsKey($dep)) {
            Write-Host "  Dependency $dep not started yet. Starting first..." -ForegroundColor Yellow
            Start-Service -Name $dep -Heap $ServiceConfig[$dep].Heap -InitHeap $ServiceConfig[$dep].InitHeap -Dir $depDir
            Write-Host "  Waiting for $dep on port $depPort..." -NoNewline
            if (Wait-ForPort -Port $depPort -TimeoutSeconds 90) {
                Write-Host " READY" -ForegroundColor Green
                $startedPorts[$dep] = $true
            } else {
                Write-Host " TIMEOUT" -ForegroundColor Red
                Write-Host "  $dep failed to start. Check logs in $LogDir." -ForegroundColor Red
            }
        }
    }

    # Wait for dependencies
    $allDepsReady = $true
    foreach ($dep in $cfg.Depends) {
        if (-not $startedPorts.ContainsKey($dep)) { $allDepsReady = $false; break }
    }
    if (-not $allDepsReady) {
        Write-Host "  Skipping $svcName - dependencies not ready." -ForegroundColor Red
        continue
    }

    # Start the service
    Start-Service -Name $svcName -Heap $cfg.Heap -InitHeap $cfg.InitHeap -Dir $cfg.Dir

    # Wait for it to be ready
    Write-Host "  Waiting for $svcName on port $($cfg.Port)..." -NoNewline
    if (Wait-ForPort -Port $cfg.Port -TimeoutSeconds 120) {
        Write-Host " READY" -ForegroundColor Green
        $startedPorts[$svcName] = $true
    } else {
        Write-Host " TIMEOUT" -ForegroundColor Red
        Write-Host "  $svcName may not have started. Check logs in $LogDir." -ForegroundColor Red
    }
}

Write-Step "[3/3] Startup complete!"
Write-Host "  Eureka:  http://localhost:8761" -ForegroundColor Cyan
Write-Host "  Gateway: http://localhost:8080" -ForegroundColor Cyan
Write-Host "  Logs:    $LogDir" -ForegroundColor Cyan
Write-Host ""
Write-Host "To stop all services, use: Get-Process -Name java | Stop-Process" -ForegroundColor Yellow
