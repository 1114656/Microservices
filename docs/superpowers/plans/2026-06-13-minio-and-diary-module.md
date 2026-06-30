# MinIO And Diary Module Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a manual MinIO connectivity test and a diary module with CRUD plus ordered text/file content blocks.

**Architecture:** The file module keeps owning S3/MinIO storage and file metadata. The diary module stores diary entries in `diary_entry`, stores ordered content as JSON blocks, and stores extracted file references in `diary_file` for lookup and cleanup. Diary save/update validates referenced file IDs through `FileObjectService`.

**Tech Stack:** Spring Boot, MyBatis Plus, JUnit 5, Mockito, AWS SDK S3-compatible MinIO client, Jackson.

---

### Task 1: MinIO Connectivity Test

**Files:**
- Create: `diary-backend/diary-module-file/src/test/java/com/xiaoyang/diary/module/file/service/storage/MinioConnectionIT.java`

- [ ] **Step 1: Write a manual integration test**

Create a JUnit test that reads `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, and optional `MINIO_BUCKET`, checks bucket existence, and creates it if missing.

- [ ] **Step 2: Verify compile/run command**

Run: `mvn -pl diary-module-file -am -Dtest=MinioConnectionIT -Dsurefire.failIfNoSpecifiedTests=false test`

Expected without env: test skips. Expected with env: test passes and bucket exists.

### Task 2: Diary Maven Module

**Files:**
- Modify: `diary-backend/pom.xml`
- Modify: `diary-backend/diary-server/pom.xml`
- Create: `diary-backend/diary-module-diary/pom.xml`

- [ ] **Step 1: Wire module**

Add `diary-module-diary` after `diary-module-file`, and add `diary-server` dependency on it.

- [ ] **Step 2: Verify module participates in reactor**

Run: `mvn -pl diary-module-diary -am -DskipTests package`

Expected: module compiles.

### Task 3: Diary Content Block Tests

**Files:**
- Create: `diary-backend/diary-module-diary/src/test/java/com/xiaoyang/diary/module/diary/service/DiaryContentBlockServiceTest.java`

- [ ] **Step 1: Write failing tests**

Test that mixed text/file blocks serialize in the same order and file IDs are extracted with sort values.

- [ ] **Step 2: Implement minimal content block service**

Create `DiaryContentBlockService` using Jackson.

- [ ] **Step 3: Verify**

Run: `mvn -pl diary-module-diary -am -Dtest=DiaryContentBlockServiceTest -Dsurefire.failIfNoSpecifiedTests=false test`

Expected: tests pass.

### Task 4: Diary Data Model, Service, Controller

**Files:**
- Create diary DOs, mappers, VO classes, service, controller under `diary-module-diary`
- Modify: `diary-backend/sql/mysql/diary-slim.sql`

- [ ] **Step 1: Write failing service tests**

Test create diary stores JSON content, inserts ordered file references, and validates file ownership through `FileObjectService`.

- [ ] **Step 2: Implement DOs, mapper, SQL, service, controller**

Add `POST /diary/create`, `PUT /diary/update`, `DELETE /diary/delete`, `GET /diary/get`, `GET /diary/page`.

- [ ] **Step 3: Verify**

Run diary module unit tests and server package.
