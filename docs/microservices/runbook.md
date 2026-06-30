# Diary Microservices Runbook

## Build

From `diary-backend`:

```powershell
mvn "-Dmaven.repo.local=..\.m2\repository" -pl diary-gateway,diary-module-system,diary-module-file,diary-module-diary,diary-module-blog -am package -DskipTests
```

## Start

From repo root:

```powershell
docker compose -f infra/docker-compose.yml up -d --build
```

Expected local ports:

- Gateway: `48080`
- System: `48081`
- File: `48082`
- Diary: `48083`
- Blog: `48084`
- Nacos: `8848`
- Sentinel dashboard: `8080`
- RocketMQ nameserver: `9876`
- MinIO API: `9000`
- MinIO console: `9001`

## Nacos Config

Seed config files live in `infra/nacos/configs`.

Data IDs:

- `diary-common.yaml`
- `diary-gateway.yaml`
- `diary-system-service.yaml`
- `diary-file-service.yaml`
- `diary-diary-service.yaml`
- `diary-blog-service.yaml`

Use group `DIARY_GROUP`.

## Smoke Test

After Compose is healthy and seed data exists for `admin/admin123`, run:

```powershell
.\infra\smoke\smoke.ps1 -GatewayBaseUrl http://localhost:48080 -Username admin -Password admin123
```

The script verifies:

- Gateway health
- Login through `/admin-api/system/auth/login`
- File upload through `/admin-api/file/upload`
- Diary list through `/admin-api/diary/page`
- Blog list through `/admin-api/blog/page`

## Sentinel Drill

Rules are tracked under:

- `diary-backend/diary-module-diary/src/main/resources/sentinel`
- `diary-backend/diary-module-blog/src/main/resources/sentinel`
- `infra/sentinel/README.md`

Use Sentinel dashboard at `http://localhost:8080` after services are running.

## Current Local Limitation

This workstation does not have Docker installed, so `docker compose config`, `docker compose up`, and the smoke script cannot be executed here yet. Run the Start and Smoke Test steps on a machine with Docker Desktop or Docker Engine installed.
