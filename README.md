# Crime Analytics Platform

A production-grade, microservices-based Crime Analytics Platform built with **Spring Boot 3.3.2**, **Java 21**, and **Spring Cloud**. Designed for law enforcement agencies to manage cases, track persons of interest, analyze criminal networks, monitor financial trails, and generate statutory reports.

> **Disclaimer:** This is a fictional demonstration project. All names, places, institutions, case numbers, and data used herein are entirely fictional and do not represent any real persons, agencies, or events. This project is built solely to demonstrate software architecture and engineering capabilities.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Services & Ports](#services--ports)
- [Infrastructure](#infrastructure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Quick Start](#quick-start)
  - [Running Specific Services](#running-specific-services)
  - [Stopping Services](#stopping-services)
- [API Reference](#api-reference)
  - [Incident Service](#incident-service-api)
  - [Person Service](#person-service-api)
  - [Graph Service](#graph-service-api)
  - [Search Service](#search-service-api)
  - [Financial Service](#financial-service-api)
  - [Report Service](#report-service-api)
  - [Conversational AI Service](#conversational-ai-service-api)
  - [Notification Service](#notification-service-api)
- [Database Schema](#database-schema)
- [Message Queue (Kafka)](#message-queue-kafka)
- [Monitoring & Observability](#monitoring--observability)
- [Security](#security)
- [CI/CD Pipeline](#cicd-pipeline)
- [Docker & Kubernetes](#docker--kubernetes)
- [Frontend](#frontend)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)

---

## Architecture Overview

```
                          +------------------+
                          |   React Frontend |
                          |   (Port 3000)    |
                          +--------+---------+
                                   |
                                   v
                          +--------+---------+
                          |   API Gateway    |
                          |   (Port 8080)    |
                          |  Rate Limiting   |
                          |  Circuit Breaker |
                          |  OAuth2 + JWT    |
                          +--------+---------+
                                   |
              +--------------------+--------------------+
              |          |         |         |          |
              v          v         v         v          v
     +--------+--+ +----+---+ +---+----+ +--+----+ +---+------+
     | Incident   | | Person | | Graph  | | Search| | Analytics|
     | (8082)     | | (8083) | | (8084) | | (8085)| | (8086)   |
     +-----------+ +--------+ +--------+ +-------+ +----------+
              |          |         |         |          |
              v          v         v         v          v
     +--------+--+ +----+---+ +---+----+ +--+----+ +---+------+
     | Financial  | | Report | | ChatAI | | Notif | | ETL      |
     | (8088)     | | (8089) | | (8087) | | (8090)| | (8091)   |
     +-----------+ +--------+ +--------+ +-------+ +----------+
              |          |         |         |          |
              +--------------------+--------------------+
                                   |
              +--------------------+--------------------+
              |          |         |         |          |
              v          v         v         v          v
     +--------+--+ +----+---+ +---+----+ +--+----+ +---+------+
     | PostgreSQL | | Redis  | | Neo4j  | | Kafka | | Elastic  |
     | + PostGIS  | | (6379) | | (7687) | |(9092) | | (9200)   |
     +-----------+ +--------+ +--------+ +-------+ +----------+
```

**Microservices Pattern:** Each service is independently deployable, communicates via REST (sync) and Kafka (async), and discovers peers through Netflix Eureka.

---

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 3.3.2 |
| **Cloud** | Spring Cloud | 2023.0.3 |
| **API Gateway** | Spring Cloud Gateway | (WebFlux) |
| **Service Discovery** | Netflix Eureka | (Spring Cloud) |
| **Resilience** | Resilience4j | 2.2.0 |
| **Database** | PostgreSQL + PostGIS | 16 |
| **Graph Database** | Neo4j | 5.x Community |
| **Search Engine** | Elasticsearch | 8.12.2 |
| **Message Broker** | Apache Kafka | 7.6.0 |
| **Cache** | Redis | 7 |
| **Auth** | Keycloak (OAuth2/OIDC) | 24.0 |
| **PDF Generation** | OpenPDF | 1.3.30 |
| **API Docs** | SpringDoc OpenAPI | 2.6.0 |
| **Monitoring** | Prometheus + Grafana | 2.53 / 11.1 |
| **Tracing** | Jaeger | 1.59 |
| **Logging** | Logback + Logstash Encoder | 7.4 |
| **Build** | Maven | 3.x |
| **Container** | Docker + Docker Compose | - |
| **Orchestration** | Kubernetes (manifests included) | - |
| **CI/CD** | GitHub Actions | - |
| **Frontend** | React 18 + TypeScript + Ant Design | - |

---

## Services & Ports

| Service | Port | Description | Database |
|---------|------|-------------|----------|
| **Discovery Service** | 8761 | Netflix Eureka server for service registration & discovery | None |
| **Gateway Service** | 8080 | API Gateway — routing, rate limiting, OAuth2, circuit breakers | Redis |
| **Incident Service** | 8082 | FIR/case management, evidence, lookups (25+ entities) | PostgreSQL |
| **Person Service** | 8083 | Suspect/victim/witness management with risk scoring | PostgreSQL + PostGIS |
| **Graph Service** | 8084 | Criminal network analysis, community detection, shortest path | PostgreSQL + Neo4j |
| **Search Service** | 8085 | Full-text search across cases, persons, financial data | PostgreSQL + Elasticsearch |
| **Analytics Service** | 8086 | Socio-demographic analytics, crime statistics | PostgreSQL + PostGIS |
| **Conversational AI** | 8087 | Chat interface for crime data queries | PostgreSQL |
| **Financial Service** | 8088 | Transaction monitoring, flagging, risk scoring | PostgreSQL |
| **Report Service** | 8089 | FIR PDF, incident reports, criminal profiles | None (PDF only) |
| **Notification Service** | 8090 | Real-time notifications via WebSocket + Kafka | Redis |
| **ETL Service** | 8091 | Nightly batch ETL — extract, transform, graph sync | PostgreSQL |

---

## Infrastructure

All infrastructure runs via Docker Compose:

| Container | Image | Port(s) | Purpose |
|-----------|-------|---------|---------|
| PostgreSQL + PostGIS | `postgis/postgis:16-3.4` | 5432 | Primary relational database |
| Neo4j | `neo4j:5-community` | 7474, 7687 | Graph database for network analysis |
| Elasticsearch | `elasticsearch:8.12.2` | 9200, 9300 | Full-text search engine |
| Redis | `redis:7-alpine` | 6379 | Caching, rate limiting, session store |
| Kafka | `confluentinc/cp-kafka:7.6.0` | 9092 | Async event streaming |
| Zookeeper | `confluentinc/cp-zookeeper:7.6.0` | 2181 | Kafka coordination |
| Keycloak | `quay.io/keycloak/keycloak:24.0` | 8081 | OAuth2/OIDC identity provider |
| Prometheus | `prom/prometheus:v2.53.0` | 9090 | Metrics collection |
| Grafana | `grafana/grafana:11.1.0` | 3001 | Metrics dashboards |
| OTel Collector | `otel/opentelemetry-collector-contrib:0.104.0` | 43170, 43180 | Telemetry collection |
| Jaeger | `jaegertracing/all-in-one:1.59` | 16686 | Distributed tracing UI |

**Named Volumes:** `postgres_data`, `neo4j_data`, `es_data`, `redis_data`, `prometheus_data`, `grafana_data`

---

## Getting Started

### Prerequisites

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| Java | JDK 21 | Eclipse Temurin 21 |
| Maven | 3.8+ | 3.9+ |
| Docker Desktop | 4.x | Latest |
| Docker Memory | 4 GB | 6 GB |
| RAM | 8 GB | 16 GB |
| Disk Space | 5 GB free | 10 GB free |
| OS | Windows/macOS/Linux | Windows 10/11 with WSL2 |

### Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/MahanteshPatil1214/crime-analytics-platform.git
cd crime-analytics-platform

# 2. Start everything (build + infra + all 12 services)
.\run-local.ps1
```

This single command will:
1. Build all 12 Java services with Maven
2. Start all Docker infrastructure containers
3. Launch services in dependency order (Discovery -> Gateway -> all others)
4. Wait for each service to be healthy before starting the next

**Estimated startup time:** 3-5 minutes for the full stack.

### Running Specific Services

For machines with limited memory, run services in batches:

```powershell
# Batch 1: Core + Case Management (5 services, ~1 GB RAM)
.\run-local.ps1 -SkipBuild -SkipInfra -Services discovery,gateway,incident,person,report

# Batch 2: Intelligence & Analytics (5 services, ~1 GB RAM)
.\run-local.ps1 -SkipBuild -SkipInfra -Services discovery,gateway,graph,search,analytics

# Batch 3: Financial + AI + Supporting (6 services, ~1 GB RAM)
.\run-local.ps1 -SkipBuild -SkipInfra -Services discovery,gateway,financial,conversational-ai,notification,etl
```

**Parameters:**
| Parameter | Description |
|-----------|-------------|
| `-SkipBuild` | Skip Maven build (use existing compiled JARs) |
| `-SkipInfra` | Skip Docker infrastructure (assume already running) |
| `-Services <list>` | Comma-separated list of services to start |

### Stopping Services

```powershell
# Stop all Java services
Get-Process -Name java | Stop-Process

# Stop Docker infrastructure
docker-compose down

# Stop and remove all data volumes
docker-compose down -v
```

---

## API Reference

All APIs are accessible through the Gateway at `http://localhost:8080` (requires JWT) or directly on each service port (no auth in local dev).

### Incident Service API
**Base URL:** `http://localhost:8082`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/cases/search` | Search cases with filters (district, status, crimeHead, date range) | - |
| `GET` | `/api/v1/cases/{id}` | Get full case detail (complainants, victims, accused, arrests) | - |
| `GET` | `/api/v1/cases/{id}/involvements` | List all persons involved in a case | - |
| `GET` | `/api/v1/cases/stats` | Case statistics (total, open, under investigation, closed) | - |
| `GET` | `/api/v1/cases/stats/districts` | Case counts by police station | - |
| `GET` | `/api/v1/cases/stats/crime-heads` | Case counts by crime type | - |
| `POST` | `/api/v1/cases/` | Create new FIR case | ADMIN/OFFICER |
| `PUT` | `/api/v1/cases/{id}` | Update case details | ADMIN/OFFICER |
| `PATCH` | `/api/v1/cases/{id}/status` | Change case status | ADMIN/OFFICER |
| `DELETE` | `/api/v1/cases/{id}` | Delete a case | ADMIN |
| `POST` | `/api/v1/cases/{caseId}/evidence/` | Upload evidence file (multipart) | ADMIN/OFFICER |
| `GET` | `/api/v1/cases/{caseId}/evidence/` | List evidence for a case | - |
| `GET` | `/api/v1/lookups/states` | Get all states | - |
| `GET` | `/api/v1/lookups/districts` | Get districts (filter by stateId) | - |
| `GET` | `/api/v1/lookups/units` | Get police stations (filter by districtId) | - |
| `GET` | `/api/v1/lookups/crime-heads` | Get all crime heads | - |
| `GET` | `/api/v1/lookups/courts` | Get all courts | - |
| `GET` | `/api/v1/lookups/employees` | Get all police employees | - |

### Person Service API
**Base URL:** `http://localhost:8083`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/persons/{id}` | Get person by UUID | - |
| `GET` | `/api/v1/persons/search` | Search persons (name, type, riskScore) | - |
| `GET` | `/api/v1/persons/offenders` | Get known offenders list | - |
| `GET` | `/api/v1/persons/stats/risk-distribution` | Risk score distribution | - |
| `POST` | `/api/v1/persons/` | Create person record | ADMIN/OFFICER |
| `PUT` | `/api/v1/persons/{id}` | Update person record | ADMIN/OFFICER |
| `DELETE` | `/api/v1/persons/{id}` | Delete person record | ADMIN/OFFICER |

### Graph Service API
**Base URL:** `http://localhost:8084`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/v1/graph/populate` | Populate Neo4j from relational data | ADMIN |
| `GET` | `/api/v1/graph/stats` | Node & relationship counts | - |
| `GET` | `/api/v1/graph/full` | Full graph (nodes + relationships, max 1000) | - |
| `GET` | `/api/v1/graph/person/{id}/network` | Person's N-hop network (1-6 hops) | - |
| `GET` | `/api/v1/graph/case/{crimeNo}/network` | Persons connected to a case | - |
| `GET` | `/api/v1/graph/search` | Search persons in graph | - |
| `GET` | `/api/v1/graph/communities` | Detect criminal communities | - |
| `GET` | `/api/v1/graph/path` | Shortest path between two persons | - |

### Search Service API
**Base URL:** `http://localhost:8085`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/v1/search/reindex` | Reindex all Elasticsearch documents | ADMIN |
| `GET` | `/api/v1/search/cases` | Search cases (query, district, status) | - |
| `GET` | `/api/v1/search/persons` | Search persons (query, type) | - |
| `GET` | `/api/v1/search/financial` | Search financial transactions | - |
| `GET` | `/api/v1/search/global` | Global search across all entities | - |
| `GET` | `/api/v1/search/autocomplete` | Autocomplete suggestions | - |
| `GET` | `/api/v1/search/stats` | Index document counts | - |

### Financial Service API
**Base URL:** `http://localhost:8088`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/financial/{id}` | Get transaction by UUID | - |
| `GET` | `/api/v1/financial/search` | Search transactions (account, type, flagged) | - |
| `GET` | `/api/v1/financial/flagged` | Get flagged transactions | - |
| `GET` | `/api/v1/financial/stats` | Transaction statistics | - |
| `GET` | `/api/v1/financial/case/{caseId}` | Transactions linked to a case | - |
| `POST` | `/api/v1/financial/` | Create transaction | ADMIN/OFFICER |
| `POST` | `/api/v1/financial/{id}/flag` | Flag transaction with reason | ADMIN/OFFICER |
| `PUT` | `/api/v1/financial/{id}` | Update transaction | ADMIN/OFFICER |
| `DELETE` | `/api/v1/financial/{id}` | Delete transaction | ADMIN/OFFICER |

### Report Service API
**Base URL:** `http://localhost:8089`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/reports/fir/{caseId}` | Generate FIR PDF report | - |
| `POST` | `/api/v1/reports/incident` | Generate incident report PDF | ADMIN/OFFICER |
| `POST` | `/api/v1/reports/criminal-profile` | Generate criminal profile PDF | ADMIN/OFFICER |

### Conversational AI Service API
**Base URL:** `http://localhost:8087`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/v1/chat/message` | Send chat message (sessionId + message) | - |
| `GET` | `/api/v1/chat/suggestions` | Get suggested prompts | - |
| `GET` | `/api/v1/chat/health` | Health check | - |

### Notification Service API
**Base URL:** `http://localhost:8090`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/notifications/` | Create notification | - |
| `GET` | `/api/notifications/user/{userId}` | Get user notifications | - |
| `GET` | `/api/notifications/user/{userId}/unread-count` | Unread count | - |
| `PUT` | `/api/notifications/{id}/read` | Mark as read | - |
| `GET` | `/api/notifications/health` | Health check | - |

---

## Database Schema

The platform uses **PostgreSQL with PostGIS** for geospatial capabilities. Key tables:

| Table | Purpose |
|-------|---------|
| `case_master` | FIR/case records with crime classification |
| `accused` | Accused persons linked to cases |
| `victim` | Victim records |
| `complainant_details` | Complainant information |
| `arrest_surrender` | Arrest/surrender records |
| `chargesheet_details` | Chargesheet filing details |
| `act_section_association` | Applicable legal sections per case |
| `persons` | All persons (suspects, victims, witnesses) with risk scores |
| `involvements` | Person-incident linking with roles |
| `financial_transactions` | Financial trail with flagging |
| `socio_demographics` | Census-level demographic data with geospatial boundaries |
| `audit_logs` | Tamper-evident audit trail with HMAC seals |
| `chat_history` | Conversational AI message history |
| `crime_head` / `crime_sub_head` | Crime classification hierarchy |
| `district` / `state` / `unit` | Administrative hierarchy |
| `employee` | Police personnel records |
| `court` | Court records |

**Geospatial:** Tables use PostGIS `GEOMETRY` types for boundaries (`POLYGON`) and locations (`POINT`), enabling spatial queries like "find all incidents within a beat boundary."

**Init scripts** are mounted into PostgreSQL at startup:
- `sql/init/01-schema.sql` — Table creation with PostGIS extensions
- `sql/init/02-create-user.sql` — Database user provisioning
- `sql/init/03-test-data.sql` — Sample test data
- `sql/init/04-ncrb-seed-data.sql` — NCRB reference data

---

## Message Queue (Kafka)

Apache Kafka handles async event-driven communication between services.

| Topic | Partitions | Replicas | Producers | Consumers |
|-------|-----------|----------|-----------|-----------|
| `crime.incident.created` | 12 | 3 | incident-service | graph-service, search-service, notification-service |
| `crime.incident.updated` | 12 | 3 | incident-service | search-service, notification-service |
| `crime.person.indexed` | 6 | 3 | person-service | graph-service, search-service |
| `crime.graph.sync` | 6 | 3 | graph-service | search-service |
| `crime.alert.notification` | 6 | 3 | financial-service, incident-service | notification-service |
| `crime.chat.events` | 6 | 3 | conversational-ai-service | notification-service |
| `crime.financial.alert` | 6 | 3 | financial-service | notification-service, incident-service |
| `crime.etl.completed` | 3 | 2 | etl-service | search-service, graph-service |

**Event Payload:** All events use the `DomainEvent<T>` envelope with `eventId`, `eventType`, `aggregateId`, `payload`, `metadata`, `occurredAt`, and `correlationId` for end-to-end tracing.

---

## Monitoring & Observability

### Access Points

| Tool | URL | Credentials |
|------|-----|-------------|
| **Eureka Dashboard** | http://localhost:8761 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3001 | admin / admin |
| **Jaeger** | http://localhost:16686 | - |

### Metrics

All 12 services expose Spring Boot Actuator metrics at `/actuator/prometheus`:

- **JVM metrics:** heap, GC, threads, classloading
- **HTTP metrics:** request count, latency (percentile histograms), error rates
- **Custom metrics:** Kafka producer/consumer lag, Neo4j query times, Elasticsearch index sizes
- **Health indicators:** database, Redis, Elasticsearch, Neo4j connectivity

### Structured Logging

Every service uses **JSON structured logging** via `logstash-logback-encoder`:

```json
{
  "@version": "1",
  "message": "Case created successfully",
  "logger": "gov.lawenforcement.incident.controller.CaseMasterController",
  "thread": "http-nio-8082-exec-1",
  "level": "INFO",
  "level_value": 20000,
  "APP_NAME": "incident-service",
  "application": "incident-service"
}
```

Logs are written to both console (for `docker-compose logs`) and rolling files under `logs/<service-name>.log`.

### Distributed Tracing

- **Request tracking:** Every request gets a unique `X-Request-Id` and `X-Correlation-Id` propagated through all services via the Gateway's `TrackingFilter`
- **Jaeger UI:** Visualize request flows across services

### Audit Trail

The `@Auditable` AOP annotation captures write operations with:
- User identity, role, IP address
- Entity type and ID
- Old/new value hashes
- **HMAC tamper seal** — each audit log entry includes a chained HMAC that detects if any record in the chain has been modified

---

## Security

### Authentication Flow

```
Frontend -> Keycloak (login) -> JWT Token
         -> API Gateway (validate JWT via issuer-uri)
         -> Service (trust Gateway's authentication)
```

- **OAuth2/OIDC:** Keycloak issues JWTs with roles (`ADMIN`, `OFFICER`, `VIEWER`)
- **Gateway enforcement:** All `/api/**` routes require a valid JWT. `/actuator/**` is open for monitoring.
- **Per-service RBAC:** Write endpoints (POST/PUT/DELETE) require `ADMIN` or `OFFICER` roles

### Security Features

| Feature | Implementation |
|---------|---------------|
| Rate Limiting | Redis-backed, 100 req/s per IP, 200 burst capacity |
| Circuit Breakers | Per-service with Resilience4j (50% failure threshold, 30s recovery) |
| CORS | Configurable origins, credentials support, 1h max-age |
| Input Validation | `@Valid` on all request bodies, Bean Validation annotations |
| SQL Injection | Parameterized queries via JPA/Hibernate |
| Cypher Injection | Parameterized Neo4j queries |
| Encryption | AES-256-GCM for sensitive fields |
| Pseudonymization | SHA-256 with salt for personally identifiable data |
| Audit Integrity | Chained HMAC tamper seals |

### Keycloak Configuration

The Keycloak realm is pre-configured via `keycloak/crime-platform-realm.json`:
- **Realm:** `crime-platform`
- **Issuer URI:** `http://localhost:8081/realms/crime-platform`
- **Client:** configured for the React frontend

---

## CI/CD Pipeline

GitHub Actions workflow at `.github/workflows/ci.yml`:

### Job 1: Build & Test

```yaml
Trigger: push to main/develop, PRs to main
Services: PostGIS, Neo4j, Elasticsearch, Redis
Steps: Checkout -> JDK 21 -> Init DB schema -> mvn clean verify -> Upload test results
```

### Job 2: Docker Build (main branch only)

- Builds Docker images for 9 services using the multi-stage `Dockerfile`
- Pushes to **GitHub Container Registry** (`ghcr.io`)
- Tags: `<commit-sha>` and `latest`
- Uses Docker BuildKit cache for fast rebuilds

### Job 3: Security Scan

- **Trivy** filesystem scan for CRITICAL and HIGH vulnerabilities
- Fails the pipeline on any finding

---

## Docker & Kubernetes

### Docker Build

Multi-stage `Dockerfile`:

```dockerfile
# Build stage — compile with JDK 21
FROM eclipse-temurin:21-jdk-alpine AS build
# ... Maven build ...

# Runtime stage — run with JRE 21
FROM eclipse-temurin:21-jre-alpine
HEALTHCHECK --interval=30s --timeout=5s CMD curl -sf http://localhost:8080/actuator/health
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build a single service:**
```bash
docker build --build-arg SERVICE_NAME=incident-service -t incident-service:latest .
```

### Kubernetes

Production manifests in `k8s/incident-service.yaml`:

- **Deployment:** 3 replicas, rolling update (zero-downtime)
- **Service:** ClusterIP on port 8082
- **HPA:** Auto-scales 3-12 pods based on 70% CPU utilization
- **Resources:** 512Mi-1Gi memory, 500m-1000m CPU
- **Health checks:** Liveness probe on `/actuator/health/liveness`, readiness on `/actuator/health/readiness`

---

## Frontend

A React-based SPA in `crime-analytics-ui/`:

| Technology | Purpose |
|-----------|---------|
| React 18 + TypeScript 5.6 | UI framework |
| Vite 5.4 | Build tool & dev server |
| Ant Design 5.20 | UI component library |
| Zustand + React Query | State management & server state |
| Axios | HTTP client |
| Leaflet | Map visualization |
| Cytoscape.js | Graph/network visualization |
| Recharts | Charts and analytics |
| STOMP over WebSocket | Real-time notifications |
| Keycloak.js | Authentication |
| i18next | Internationalization |
| Vitest | Unit testing |

**Start frontend:**
```bash
cd crime-analytics-ui
npm install
npm run dev    # http://localhost:3000
```

---

## Project Structure

```
crime-analytics-platform/
|
|-- pom.xml                          # Parent POM (modules, dependency management)
|-- docker-compose.yml               # All infrastructure containers
|-- Dockerfile                       # Multi-stage Docker build
|-- run-local.ps1                    # Windows PowerShell launcher script
|-- .env                             # Environment variables (DB passwords)
|-- .dockerignore                    # Docker build exclusions
|-- .github/workflows/ci.yml         # GitHub Actions CI/CD
|
|-- shared-models/                   # Shared Java library (all services depend on this)
|   |-- src/main/java/.../
|   |   |-- common/exception/        # GlobalExceptionHandler + custom exceptions
|   |   |-- common/entity/           # AuditLog entity
|   |   |-- common/audit/            # @Auditable annotation + AOP aspect
|   |   |-- common/crypto/           # AES-256-GCM encryption, HMAC tamper seals
|   |   |-- messaging/               # Kafka topic config + DomainEvent envelope
|   |   |-- dto/                     # Shared DTOs
|
|-- discovery-service/               # Netflix Eureka (port 8761)
|-- gateway-service/                 # Spring Cloud Gateway (port 8080)
|-- incident-service/                # Case/FIR management (port 8082)
|-- person-service/                  # Person tracking (port 8083)
|-- graph-service/                   # Neo4j network analysis (port 8084)
|-- search-service/                  # Elasticsearch full-text search (port 8085)
|-- analytics-service/               # Crime analytics (port 8086)
|-- conversational-ai-service/       # Chat interface (port 8087)
|-- financial-service/               # Transaction monitoring (port 8088)
|-- report-service/                  # PDF report generation (port 8089)
|-- notification-service/            # Real-time notifications (port 8090)
|-- etl-service/                     # Nightly batch ETL (port 8091)
|
|-- crime-analytics-ui/              # React frontend (port 3000)
|
|-- sql/init/                        # Database initialization scripts
|-- keycloak/                        # Keycloak realm configuration
|-- prometheus/                      # Prometheus scrape config
|-- grafana/                         # Grafana dashboards & provisioning
|-- otel-collector-config.yml        # OpenTelemetry collector config
|-- k8s/                             # Kubernetes deployment manifests
|-- logs/                            # Local service logs (generated)
```

---

## Troubleshooting

### Port Already in Use

```powershell
# Find and kill process on a port
netstat -ano | findstr :8080
Stop-Process -Id <PID> -Force
```

### Service Won't Start (OOM)

Reduce heap sizes in `run-local.ps1` or run fewer services simultaneously:
```powershell
.\run-local.ps1 -SkipBuild -SkipInfra -Services discovery,gateway,incident
```

### Discovery Service Fails to Start

Ensure no DataSource auto-configuration is triggered. The service excludes JPA/DataSource in `application.yml`:
```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

### Gateway Returns 401

All API routes require a valid JWT. Access actuator endpoints directly for local testing:
```bash
curl http://localhost:8082/actuator/health   # No auth required
```

### Docker Containers Won't Start

```powershell
# Check container status
docker-compose ps

# View logs
docker-compose logs crime-postgres
docker-compose logs crime-kafka

# Reset everything
docker-compose down -v
docker-compose up -d
```

### Full Cleanup

```powershell
# Kill all Java services
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force

# Stop all Docker containers and remove volumes
docker-compose down -v

# Clean Maven build cache
mvn clean
```

---

## License

This project is for educational and demonstration purposes.

---

**Built with Spring Boot 3.3.2 | Java 21 | Spring Cloud 2023.0.3 | Docker | Kubernetes**
