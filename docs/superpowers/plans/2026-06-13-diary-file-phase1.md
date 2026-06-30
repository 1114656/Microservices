# Diary File Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a first-stage `diary-file` backend module for authenticated file upload, automatic category detection, S3/MinIO storage, metadata persistence, and preview URLs.

**Architecture:** `diary-file` is a Maven module loaded by `diary-server` for now, with service boundaries that can later become a standalone service. It owns file metadata and S3 object keys, while S3/MinIO owns file bytes. Uploads are backend-mediated in phase 1; frontend direct presigned upload is reserved for phase 2.

**Tech Stack:** Spring Boot 3, MyBatis Plus, AWS SDK S3, MySQL/H2-compatible schema, existing `diary-common`, `diary-spring-boot-starter-web`, `diary-spring-boot-starter-mybatis`, and `diary-spring-boot-starter-test`.

---

### Task 1: Module And Maven Wiring

**Files:**
- Modify: `diary-backend/pom.xml`
- Modify: `diary-backend/diary-server/pom.xml`
- Create: `diary-backend/diary-module-file/pom.xml`

- [ ] Add `diary-module-file` to the root Maven modules.
- [ ] Add `diary-module-file` as a dependency of `diary-server`.
- [ ] Create `diary-module-file/pom.xml` with dependencies on common, web, security, validation, mybatis, AWS S3, and test starter.
- [ ] Run `mvn -pl diary-module-file -am test` and expect compilation to include the new module.

### Task 2: File Category Detection TDD

**Files:**
- Create: `diary-backend/diary-module-file/src/test/java/com/xiaoyang/diary/module/file/service/FileCategoryDetectorTest.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/enums/FileCategoryEnum.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/service/FileCategoryDetector.java`

- [ ] Write failing tests for image, audio, video, text, document, archive, and other categories.
- [ ] Run `mvn -pl diary-module-file -Dtest=FileCategoryDetectorTest test`; expect failure because detector does not exist.
- [ ] Implement `FileCategoryEnum` and `FileCategoryDetector` using content type first and extension fallback.
- [ ] Re-run the test and expect pass.

### Task 3: S3 Storage Client Adapter TDD

**Files:**
- Create: `diary-backend/diary-module-file/src/test/java/com/xiaoyang/diary/module/file/service/storage/S3FileStorageServiceTest.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/framework/config/FileStorageProperties.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/framework/config/FileStorageConfiguration.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/service/storage/FileStorageService.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/service/storage/S3FileStorageService.java`

- [ ] Write tests with a mocked `S3Client` and `S3Presigner` verifying `putObject` receives bucket, object key, content type, and content length.
- [ ] Verify `createPreviewUrl` returns a URL from the presigner.
- [ ] Implement configuration properties under `diary.file.s3`.
- [ ] Implement the storage service against AWS SDK S3.

### Task 4: File Metadata Model

**Files:**
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/dal/dataobject/FileObjectDO.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/dal/mysql/FileObjectMapper.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/enums/FileObjectStatusEnum.java`
- Create: `diary-backend/diary-module-file/src/main/resources/mapper/file/FileObjectMapper.xml`
- Modify: `diary-backend/sql/mysql/diary-slim.sql`

- [ ] Add `file_object` schema with bucket, object key, original name, extension, content type, category, size, sha256, owner user id, business type, business id, visibility, preview supported, status, and audit columns.
- [ ] Add MyBatis DO and mapper.
- [ ] Keep business linkage optional through `businessType` and `businessId`.

### Task 5: Upload Service And Controller TDD

**Files:**
- Create: `diary-backend/diary-module-file/src/test/java/com/xiaoyang/diary/module/file/service/FileObjectServiceImplTest.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/controller/admin/FileObjectController.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/controller/admin/vo/FileUploadReqVO.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/controller/admin/vo/FileObjectRespVO.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/controller/admin/vo/FilePreviewUrlRespVO.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/service/FileObjectService.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/service/FileObjectServiceImpl.java`
- Create: `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/enums/ErrorCodeConstants.java`

- [ ] Write tests for empty upload rejection, allowed audio category detection, S3 object key generation, metadata persistence, and preview URL lookup.
- [ ] Implement `POST /file/upload`, `GET /file/{id}`, `GET /file/{id}/preview-url`, `GET /file/page`, and `DELETE /file/{id}`.
- [ ] Do not implement a download endpoint in phase 1.
- [ ] Use short-lived preview URLs and return inline-capable S3 URLs for image/audio/video/text previews.

### Task 6: Application Configuration

**Files:**
- Modify: `diary-backend/diary-server/src/main/resources/application.yaml`
- Modify: `diary-backend/diary-server/src/main/resources/application-local.yaml`
- Modify: `diary-backend/diary-server/src/main/resources/application-dev.yaml`

- [ ] Add multipart limits.
- [ ] Add `diary.file.s3` configuration for bucket, region, endpoint, access key, secret key, path style access, and preview URL expiry.
- [ ] Use MinIO-friendly local defaults.

### Task 7: Verification

**Commands:**
- `mvn -pl diary-module-file -am test`
- `mvn -pl diary-server -am package -DskipTests`

- [ ] Confirm all `diary-module-file` tests pass.
- [ ] Confirm `diary-server` packages with the new module included.
- [ ] Report any skipped runtime MinIO verification separately if MinIO is not running locally.
