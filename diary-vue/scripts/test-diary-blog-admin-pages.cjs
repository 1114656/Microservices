const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')

const root = path.resolve(__dirname, '..')
const repoRoot = path.resolve(root, '..')

const read = (file) => fs.readFileSync(path.resolve(repoRoot, file), 'utf8')
const exists = (file) => fs.existsSync(path.resolve(repoRoot, file))

assert.ok(exists('diary-vue/src/api/diary/entry/index.ts'), 'diary entry api is missing')
assert.ok(exists('diary-vue/src/api/blog/article/index.ts'), 'blog article api is missing')
assert.ok(exists('diary-vue/src/api/file/index.ts'), 'file api is missing')
assert.ok(exists('diary-vue/src/views/diary/entry/index.vue'), 'diary entry page is missing')
assert.ok(exists('diary-vue/src/views/diary/entry/DiaryEntryForm.vue'), 'diary form is missing')
assert.ok(exists('diary-vue/src/views/blog/article/index.vue'), 'blog article page is missing')
assert.ok(exists('diary-vue/src/views/blog/article/BlogArticleForm.vue'), 'blog form is missing')
assert.ok(
  exists('diary-backend/sql/mysql/2026-06-13-diary-blog-menu.sql'),
  'menu sql is missing'
)

const diaryApi = read('diary-vue/src/api/diary/entry/index.ts')
for (const name of ['getDiaryPage', 'getDiary', 'createDiary', 'updateDiary', 'deleteDiary']) {
  assert.match(diaryApi, new RegExp(`export const ${name}\\b`), `missing diary api ${name}`)
}

const blogApi = read('diary-vue/src/api/blog/article/index.ts')
for (const name of [
  'getBlogArticlePage',
  'getBlogArticle',
  'createBlogArticle',
  'updateBlogArticle',
  'deleteBlogArticle'
]) {
  assert.match(blogApi, new RegExp(`export const ${name}\\b`), `missing blog api ${name}`)
}

const diaryPage = read('diary-vue/src/views/diary/entry/index.vue')
for (const permission of [
  'diary:entry:create',
  'diary:entry:update',
  'diary:entry:delete'
]) {
  assert.match(diaryPage, new RegExp(permission), `missing diary permission ${permission}`)
}

const blogPage = read('diary-vue/src/views/blog/article/index.vue')
for (const permission of [
  'blog:article:create',
  'blog:article:update',
  'blog:article:delete'
]) {
  assert.match(blogPage, new RegExp(permission), `missing blog permission ${permission}`)
}

const menuSql = read('diary-backend/sql/mysql/2026-06-13-diary-blog-menu.sql')
for (const text of [
  'diary/entry/index',
  'blog/article/index',
  'diary:entry:create',
  'blog:article:create'
]) {
  assert.match(menuSql, new RegExp(text), `missing menu sql text ${text}`)
}

const blogController = read(
  'diary-backend/diary-module-blog/src/main/java/com/xiaoyang/diary/module/blog/controller/admin/BlogController.java'
)
for (const permission of [
  "blog:article:create",
  "blog:article:update",
  "blog:article:delete"
]) {
  assert.match(blogController, new RegExp(permission), `missing backend permission ${permission}`)
}

console.log('diary/blog admin page structure tests passed')
