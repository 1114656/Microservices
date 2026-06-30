# Diary Slim Admin Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove all requested management/features and leave only the minimal admin shell: login, user, role, menu permissions, profile, home.

**Architecture:** Remove exposed frontend pages/API modules first, then remove backend controllers/services/data objects for deleted domains while preserving authentication and permission dependencies. Generate a new slim MySQL initialization script containing only tables and seed data needed by the minimal shell.

**Tech Stack:** Spring Boot 3.5, Maven, MyBatis Plus, Vue 3, Vite, TypeScript, Element Plus, MySQL SQL scripts.

---

### Task 1: Frontend Pruning

**Files:**
- Delete feature folders under `diary-vue/src/views/infra`
- Delete feature folders under `diary-vue/src/views/system` except `menu`, `role`, `user`
- Delete API folders under `diary-vue/src/api/infra`
- Delete API folders under `diary-vue/src/api/system` except `menu`, `permission`, `role`, `user`
- Modify login/profile code if it references social login, tenant, dept, post, dict, file upload, or deleted APIs

- [x] Remove deleted feature routes/pages and matching API modules.
- [x] Remove social-login/tenant/captcha optional UI paths from login if they depend on deleted backend APIs.
- [x] Keep `Home`, `Login`, `Profile`, `system/menu`, `system/role`, `system/user`, and layout components compiling.
- [x] Run `pnpm exec vue-tsc --noEmit` and fix compile errors.

### Task 2: Backend Pruning

**Files:**
- Modify/delete Java packages in `diary-backend/diary-module-system/src/main/java/com/xiaoyang/diary/module/system`
- Modify/delete Java packages in `diary-backend/diary-module-infra/src/main/java/com/xiaoyang/diary/module/infra`
- Modify Maven dependencies if deleted code makes dependencies unused or broken

- [x] Delete controllers and services for tenant, dept, post, dict, notify, notice, logger/audit, OAuth2 management, social login, area, codegen, datasource config, form/API tooling, websocket, file, job, config, monitor.
- [x] Preserve minimal auth/token flow required by frontend login and route loading.
- [x] Preserve user/role/menu permission APIs and profile APIs.
- [x] Run Maven compile and fix missing references.

### Task 3: Slim SQL

**Files:**
- Create `diary-backend/sql/mysql/diary-slim.sql`

- [x] Parse current `diary.sql`/`diary-all.sql` table definitions and seed data.
- [x] Keep only minimal tables for user, role, menu, role-menu, user-role, token/session, and necessary framework metadata.
- [x] Remove all tables and seed data for requested deleted domains.
- [x] Seed only minimal menus for System, User, Role, Menu, and required permissions.

### Task 4: Verification

- [x] Run frontend type check/build.
- [x] Run backend Maven compile.
- [x] Inspect generated SQL for deleted table names and removed menu entries.
- [x] Report exact commands and any residual risks.
