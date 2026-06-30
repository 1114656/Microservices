# Blog Module Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a blog module with authenticated CRUD and Markdown content that preserves frontend-authored code blocks.

**Architecture:** Blog posts live in a new `diary-module-blog` module and are loaded by `diary-server`. The frontend sends Markdown as plain text, including fenced code blocks; the backend stores it unchanged, validates ownership, optional cover file ownership, and returns the same Markdown for rendering.

**Tech Stack:** Spring Boot, MyBatis Plus, JUnit 5, Mockito, existing security and common framework.

---

### Task 1: Maven Wiring

**Files:**
- Modify: `diary-backend/pom.xml`
- Modify: `diary-backend/diary-server/pom.xml`
- Create: `diary-backend/diary-module-blog/pom.xml`

- [ ] Add the module and server dependency.
- [ ] Verify the module participates in the Maven reactor.

### Task 2: Blog Service TDD

**Files:**
- Create: `diary-backend/diary-module-blog/src/test/java/com/xiaoyang/diary/module/blog/service/BlogArticleServiceImplTest.java`
- Create production files under `diary-module-blog/src/main/java/com/xiaoyang/diary/module/blog`

- [ ] Write a failing test proving code fences are stored unchanged and cover files are validated through `FileObjectService`.
- [ ] Implement minimal service, DO, mapper, VO, and enum classes.
- [ ] Run the targeted test and make it pass.

### Task 3: REST API And SQL

**Files:**
- Create: `BlogController.java`
- Modify: `diary-backend/sql/mysql/diary-slim.sql`

- [ ] Add endpoints: `POST /blog/create`, `PUT /blog/update`, `DELETE /blog/delete`, `GET /blog/get`, `GET /blog/page`.
- [ ] Add `blog_article` table.
- [ ] Verify target tests and `diary-server` package.
