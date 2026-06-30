# Entry Status Visibility Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add unified draft/published status and owner/login/public visibility to diary and blog page queries.

**Architecture:** Keep lifecycle status and audience visibility as separate integer columns. Controllers expose anonymous `/page` endpoints with `@PermitAll`, and services accept nullable `loginUserId` for public list filtering.

**Tech Stack:** Java 17, Spring Boot, Spring Security method annotations, MyBatis Plus, JUnit 5, Mockito.

---

### Task 1: Write Failing Service Tests

**Files:**
- Modify: `diary-backend/diary-module-diary/src/test/java/com/xiaoyang/diary/module/diary/service/DiaryServiceImplTest.java`
- Modify: `diary-backend/diary-module-blog/src/test/java/com/xiaoyang/diary/module/blog/service/BlogArticleServiceImplTest.java`

- [ ] Add tests that assert default created entries use `status = 1` and `visibility = 1`.
- [ ] Add tests that assert page queries pass nullable `loginUserId` into mapper methods for anonymous public access.
- [ ] Run targeted tests and confirm failures are caused by missing status/visibility support.

### Task 2: Implement Model And Query Support

**Files:**
- Modify diary DO, VO, mapper, service, controller.
- Modify blog DO, VO, mapper, service, controller.
- Modify `diary-backend/sql/mysql/diary-slim.sql`.

- [ ] Add fields to DOs and VOs.
- [ ] Default status and visibility in create/update.
- [ ] Change mapper page filtering to apply public/owner visibility rules.
- [ ] Add `@PermitAll` to `/diary/page` and `/blog/page`.
- [ ] Convert response fields.

### Task 3: Verify

**Commands:**
- `mvn -pl diary-module-diary -am "-Dtest=DiaryContentBlockServiceTest,DiaryServiceImplTest,DiaryControllerPermissionTest" "-Dsurefire.failIfNoSpecifiedTests=false" test`
- `mvn -pl diary-module-blog -am -Dtest=BlogArticleServiceImplTest "-Dsurefire.failIfNoSpecifiedTests=false" test`
- `mvn -pl diary-server -am package -DskipTests`

