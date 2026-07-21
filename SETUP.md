# Crime Analytics Platform — Setup & Run Guide

---

## What You Need Installed

| Requirement | Version | Install |
|---|---|---|
| **Docker Desktop** | Latest | You have this |
| **Java JDK** | 21 | `winget install EclipseAdoptium.Temurin.21.JDK` |
| **Maven** | 3.9+ | `winget install Apache.Maven` or use `mvnw` wrapper |
| **Node.js** | 18+ | `winget install OpenJS.NodeJS.LTS` |
| **Git** | Latest | `winget install Git.Git` |

---

## Step 1: Start Infrastructure (Databases)

```bash
cd D:\crime\crime-analytics-platform
docker-compose up -d
```

This starts:

| Service | URL | What it is |
|---|---|---|
| PostgreSQL + PostGIS | `localhost:5432` | Relational DB with geospatial |
| Neo4j | `localhost:7474` (browser) / `localhost:7687` (bolt) | Graph database |
| Elasticsearch | `localhost:9200` | Search engine |
| Kafka | `localhost:9092` | Message broker |
| Redis | `localhost:6379` | Session cache |
| Keycloak | `localhost:8081` | Auth (admin/admin) |
| Zookeeper | `localhost:2181` | Kafka dependency |

**Verify all are running:**
```bash
docker-compose ps
```

**Wait for healthy status** (especially Postgres — ~10 seconds):
```bash
docker-compose logs postgres-primary | grep "ready to accept connections"
```

The SQL schema in `sql/init/01-schema.sql` runs **automatically** on first Postgres startup.

---

## Step 2: Build the Java Backend

```bash
cd D:\crime\crime-analytics-platform

# On Windows, use mvnw if available, or just mvn:
mvn clean install -DskipTests
```

This builds all 12 microservices (plus shared-models library).

**Expected build time:** ~3-5 minutes on first run.

If Maven is not installed, use the wrapper:
```bash
.\mvnw clean install -DskipTests
```

(If you don't have `mvnw`, download it: `mvn -N io.takari:maven:wrapper`)

---

## Step 3: Run the Microservices

**Quick way (recommended):** Use the provided startup script:

```powershell
# From the project root:
.\run-local.ps1
```

This launches all 12 services in dependency order with optimized JVM heap settings:
- Discovery & Gateway first (128m heap each)
- Data services next (256m heap each: incident, person, graph, search, analytics, financial, report, etl)
- LLM service last (512m heap: conversational-ai)

Each service has fixed heap limits baked into its `pom.xml` to prevent OOM crashes when running all services simultaneously. The script also supports:
- `-SkipBuild` — skip Maven build if already compiled
- `-SkipInfra` — skip Docker infrastructure startup
- `-Services` — specify a subset (e.g. `-Services discovery,gateway,incident`)

### Manual start (one-by-one)

Run each in a separate terminal:

```bash
# Terminal 1: Discovery (must start first)
cd discovery-service
mvn spring-boot:run

# Terminal 2: Gateway (after discovery is up)
cd gateway-service
mvn spring-boot:run
```

Wait for gateway to start, then start other services:

```bash
# Terminal 3
cd incident-service && mvn spring-boot:run

# Terminal 4
cd person-service && mvn spring-boot:run

# Terminal 5
cd graph-service && mvn spring-boot:run

# Terminal 6
cd conversational-ai-service && mvn spring-boot:run

# Terminal 7
cd search-service && mvn spring-boot:run

# Terminal 8
cd financial-service && mvn spring-boot:run

# Terminal 9
cd analytics-service && mvn spring-boot:run

# Terminal 10
cd report-service && mvn spring-boot:run

# Terminal 11
cd notification-service && mvn spring-boot:run

# Terminal 12
cd etl-service && mvn spring-boot:run
```

---

## Step 4: Run the Frontend

```bash
cd D:\crime\crime-analytics-platform\crime-analytics-ui
npm install
npm run dev
```

Opens at: **http://localhost:3000**

---

## Step 5: Verify Everything Works

### Check Eureka (service discovery):
Open: **http://localhost:8761**
- All services should appear under "Instances currently registered"

### Check Gateway:
```bash
curl http://localhost:8080/actuator/health
```

### Test the API (requires auth — for now, hit directly):
```bash
# Test incident service directly (bypass gateway auth)
curl http://localhost:8082/api/v1/incidents/search?district=Bangalore

# Test chat
curl -X POST http://localhost:8087/api/v1/chat/message \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-1","message":"Show crimes in Bangalore","isVoice":false}'
```

### Check Neo4j Browser:
Open: **http://localhost:7474**
- Login: `neo4j` / password from `.env` file (`NEO4J_PASSWORD`)

---

## API Keys & External Services

| Service | Needed For | How to Get |
|---|---|---|
| **Gemini API Key** | LLM chat responses (REQUIRED) | Get free key at aistudio.google.com, paste in `.env` as `GEMINI_API_KEY` |
| **Mapbox GL Token** | NOT needed — maps use free Leaflet + OpenStreetMap | N/A |
| **Whisper API** | Speech-to-text (optional) | OpenAI or self-hosted wav2vec2 |
| **Keycloak Realm** | Full RBAC auth (optional) | Import realm JSON into Keycloak |

**How to set your Gemini key:**
1. Go to https://aistudio.google.com/apikey
2. Create a free API key
3. Open `.env` file in the project root
4. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual key
5. Save the file

**Maps:** No API key needed — Leaflet + OpenStreetMap is completely free.

---

## Environment Variables Reference

All configurable in `.env` or `application.yml`:

| Variable | Default | Purpose |
|---|---|---|
| `DB_PASSWORD` | `CrimeSecure2024!` | PostgreSQL password |
| `NEO4J_PASSWORD` | Set in `.env` file | Neo4j password |
| `ES_PASSWORD` | `elasticCrime2024!` | Elasticsearch password |
| `REDIS_PASSWORD` | `redisCrime2024!` | Redis password |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://localhost:8761/eureka/` | Eureka URL |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/crime_analytics` | DB connection |
| `ENCRYPTION_FIELD_KEY` | (in .env) | AES-256 key for field encryption |
| `AUDIT_HMAC_SECRET` | (in .env) | HMAC key for audit chain |
| `GEMINI_API_KEY` | (in .env) | Google Gemini API key for chat |

---

## Ports Summary

| Port | Service |
|---|---|
| 3000 | React Frontend |
| 5432 | PostgreSQL |
| 6379 | Redis |
| 7474/7687 | Neo4j |
| 8080 | API Gateway |
| 8082 | Incident Service |
| 8083 | Person Service |
| 8084 | Graph Service |
| 8085 | Search Service |
| 8086 | Analytics Service |
| 8087 | Conversational AI |
| 8088 | Financial Service |
| 8089 | Report Service |
| 8081 | Keycloak |
| 8090 | Notification Service |
| 8091 | ETL Service |
| 8761 | Eureka Discovery |
| 9092 | Kafka |
| 9200 | Elasticsearch |

---

## Memory Notes

All 12 microservices running simultaneously can consume ~4 GB of heap. Each service's `pom.xml` has been configured with explicit heap limits (`-Xmx`/`-Xms`) via the `spring-boot-maven-plugin`. Infrastructure (Docker) containers add another ~4 GB. Ensure your system has at least 15 GB total RAM available.

### Per-service heap allocations:
| Heap | Services |
|------|----------|
| 128m max / 64m init | discovery, gateway, notification |
| 256m max / 128m init | incident, person, graph, search, analytics, financial, report, etl |
| 512m max / 256m init | conversational-ai |

If you still experience OOM, reduce the `conversational-ai-service` heap to 256m in its `pom.xml`.

---

## Troubleshooting

**Port already in use:**
```bash
netstat -ano | findstr :8080
taskkill /PID <process_id> /F
```

**Docker won't start:**
- Ensure Docker Desktop is running
- WSL2 backend enabled: `wsl --install`

**Services can't find each other:**
- Check Eureka at http://localhost:8761
- Ensure all services registered (takes ~30s after startup)

**Neo4j auth fails:**
```bash
docker-compose down -v
docker-compose up neo4j
```

**Schema already exists (Flyway error):**
```bash
# Reset database
docker-compose down -v
docker-compose up postgres-primary
```
