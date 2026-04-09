# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A ticket ordering system (vetautet.com) built with Java 21, Spring Boot 3.3.5, following DDD (Domain-Driven Design) layered architecture. The system is designed for high concurrency (targeting 20,000 req/s) with multi-level caching and distributed locking.

## Build & Run

```bash
# Start infrastructure (MySQL 3316, Redis 6319, Prometheus, Grafana, ELK stack)
docker-compose -f environment/docker-compose-dev.yml up

# Build all modules
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run application (port 1122)
mvn spring-boot:run -pl xxxx-start
```

For lower-spec machines, use `environment/docker-compose-lite` variant and adjust `prometheus.yml` accordingly.

## Module Architecture (Dependency Flow)

```
xxxx-start → xxxx-controller → xxxx-application → xxxx-infrastructure → xxxx-domain
```

- **xxxx-domain**: Core business entities (`Ticket`, `TicketDetail`), domain services, and repository interfaces. No infrastructure dependencies — only JPA annotations for entity mapping.
- **xxxx-infrastructure**: Implements domain repository interfaces. Contains Redis caching (`RedisInfrasService`), Redisson distributed locking (`RedisDistributedLocker`), JPA persistence mappers, and Redis/Redisson configuration.
- **xxxx-application**: Orchestrates domain and infrastructure services. Contains DTOs, mappers, and the multi-level caching strategy (`TicketDetailCacheService`, `TicketDetailCacheServiceRefactor`) using local cache (Guava/Caffeine) + Redis + distributed locks.
- **xxxx-controller**: REST API layer with `ResultMessage<T>` response wrapper and `ResultUtil`/`ResultCode` enums.
- **xxxx-start**: Bootstrap module with `StartApplication` entry point and `application.yml` config. Contains MySQL connector and Prometheus metrics dependencies.

## Key Design Patterns

- **Multi-level caching**: Local cache (Guava/Caffeine) → Redis distributed cache → Database, with Redisson distributed locks to prevent cache stampede.
- **Resilience4j**: Circuit breaker (`checkRandom` instance) and rate limiter (`backendA`, `backendB` instances) configured in `application.yml`.
- **Virtual threads**: Enabled via `spring.threads.virtual.enabled: true`.
- **Monitoring**: Prometheus + Grafana for app/DB/Redis metrics; ELK stack (Elasticsearch + Logstash + Kibana) for log aggregation via `logstash-logback-encoder`.

## Infrastructure Ports

| Service       | Port  |
|---------------|-------|
| Application   | 1122  |
| MySQL         | 3316  |
| Redis         | 6319  |
| Prometheus    | 9090  |
| Grafana       | 3000  |
| Kibana        | 5601  |
| Elasticsearch | 9200  |

## Base Package

All Java code lives under `com.xxxx.ddd.*` with module-specific sub-packages (`.domain`, `.infrastructure`, `.application`, `.controller`).
