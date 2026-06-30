const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')

const root = path.resolve(__dirname, '..')
const repoRoot = path.resolve(root, '..')
const read = (file) => fs.readFileSync(path.resolve(repoRoot, file), 'utf8')
const exists = (file) => fs.existsSync(path.resolve(repoRoot, file))

for (const file of [
  'diary-vue/src/api/site/config/index.ts',
  'diary-vue/src/views/site/Home.vue',
  'diary-vue/src/views/site/diary/index.vue',
  'diary-vue/src/views/site/blog/index.vue',
  'diary-vue/src/views/site/settings/index.vue',
  'diary-vue/src/assets/imgs/site/gardenia-hero.png',
  'diary-backend/diary-module-system/src/main/java/com/xiaoyang/diary/module/system/controller/admin/site/SiteConfigController.java',
  'diary-backend/diary-module-system/src/main/java/com/xiaoyang/diary/module/system/service/site/SiteConfigService.java',
  'diary-backend/sql/mysql/2026-06-14-site-config.sql'
]) {
  assert.ok(exists(file), `${file} is missing`)
}

const siteApi = read('diary-vue/src/api/site/config/index.ts')
assert.match(siteApi, /DEFAULT_HOME_PAGE_TYPE\s*=\s*'diary'/)
assert.match(siteApi, /getPublicSiteConfig/)
assert.match(siteApi, /updateSiteConfig/)

const router = read('diary-vue/src/router/modules/remaining.ts')
assert.match(router, /path:\s*'\/'/)
assert.match(router, /@\/views\/site\/Home.vue/)
assert.match(router, /path:\s*'\/diary'/)
assert.match(router, /@\/views\/site\/diary\/index.vue/)
assert.match(router, /path:\s*'\/blog'/)
assert.match(router, /@\/views\/site\/blog\/index.vue/)
assert.match(router, /path:\s*'\/index'/)

const permission = read('diary-vue/src/permission.ts')
assert.match(permission, /'\/'/)
assert.match(permission, /'\/diary'/)
assert.match(permission, /'\/blog'/)
assert.match(permission, /next\(\{ path: '\/index' \}\)/)

const routerIndex = read('diary-vue/src/router/index.ts')
assert.match(routerIndex, /'SiteDiary'/)
assert.match(routerIndex, /'SiteBlog'/)

const home = read('diary-vue/src/views/site/Home.vue')
assert.match(home, /router\.replace/)
assert.match(home, /homePageType === 'blog' \? '\/blog' : '\/diary'/)
assert.doesNotMatch(home, /DiaryHome/)
assert.doesNotMatch(home, /BlogHome/)
assert.match(home, /site-loading/)

const diaryPage = read('diary-vue/src/views/site/diary/index.vue')
assert.match(diaryPage, /gardenia-hero\.png/)
assert.match(diaryPage, /栀子花/)
assert.match(diaryPage, /花影/)
assert.match(diaryPage, /diary-featured/)

const blogPage = read('diary-vue/src/views/site/blog/index.vue')
assert.match(blogPage, /传统博客/)
assert.match(blogPage, /featured-article/)
assert.match(blogPage, /article-index/)
assert.doesNotMatch(blogPage, /gardenia-hero\.png/)

const sql = read('diary-backend/sql/mysql/2026-06-14-site-config.sql')
assert.match(sql, /home_page_type/)
assert.match(sql, /'diary'/)
assert.match(sql, /site\/settings\/index/)
assert.match(sql, /site:config:update/)

const controller = read(
  'diary-backend/diary-module-system/src/main/java/com/xiaoyang/diary/module/system/controller/admin/site/SiteConfigController.java'
)
assert.match(controller, /@PermitAll/)
assert.match(controller, /site:config:update/)

console.log('site home config structure tests passed')
