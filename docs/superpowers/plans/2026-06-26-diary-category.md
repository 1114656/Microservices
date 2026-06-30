# Diary Category Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add backend-managed diary categories, require diary entries to choose an existing category, and return per-category diary counts split by status.

**Architecture:** Add `diary_category` beside `diary_entry`, link entries with `category_id`, and expose `/diary/category/*` endpoints. Category responses include `totalCount`, `draftCount`, and `publishedCount` computed from diary entries.

**Tech Stack:** Spring Boot, MyBatis-Plus, JUnit/Mockito, Vue 3, Element Plus.

---

### Task 1: Backend Category Domain

**Files:**
- Create: `diary-backend/diary-module-diary/src/main/java/com/xiaoyang/diary/module/diary/dal/dataobject/DiaryCategoryDO.java`
- Create: `diary-backend/diary-module-diary/src/main/java/com/xiaoyang/diary/module/diary/dal/mysql/DiaryCategoryMapper.java`
- Create service, controller, and VO classes under the existing diary module.
- Modify: diary entry DO/VO/mapper/service/controller to carry `categoryId`.

- [ ] Write failing service tests for category status counts and delete protection.
- [ ] Write failing diary service test for category validation on create.
- [ ] Implement the minimal category classes and diary validation.
- [ ] Run diary module tests and keep them green.

### Task 2: Database Migration

**Files:**
- Create: `diary-backend/sql/mysql/2026-06-26-diary-category.sql`
- Modify: `diary-backend/sql/mysql/diary-slim.sql`

- [ ] Add `diary_category` table.
- [ ] Add `category_id` to `diary_entry`.
- [ ] Add indexes for owner/category/status.

### Task 3: Frontend Category Selection

**Files:**
- Create: `diary-vue/src/api/diary/category/index.ts`
- Modify: `diary-vue/src/api/diary/entry/index.ts`
- Modify: `diary-vue/src/views/diary/entry/DiaryEntryForm.vue`
- Modify: `diary-vue/src/views/diary/entry/index.vue`

- [ ] Fetch enabled categories for the diary form.
- [ ] Require category selection.
- [ ] Show category and status-split counts in the diary list filter options.
