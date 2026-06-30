<template>
  <main class="blog-site">
    <header class="site-header">
      <div class="brand" @click="router.push('/blog')">
        <img alt="Diary" src="@/assets/imgs/logo.png" />
        <span>Blog</span>
      </div>
      <nav>
        <el-button text @click="router.push('/diary')">日记</el-button>
        <el-button v-if="hasToken" type="primary" @click="router.push('/index')">进入后台</el-button>
        <template v-else>
          <el-button text @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
        </template>
      </nav>
    </header>

    <section class="hero">
      <p class="eyebrow">Traditional Blog</p>
      <h1>传统博客</h1>
      <p>整理文章、代码和想法。少一点装饰，多一点稳定的阅读节奏。</p>
    </section>

    <section class="content">
      <div v-if="loading" class="site-loading">
        <span></span>
        <p>正在整理文章...</p>
      </div>
      <el-empty v-else-if="list.length === 0" description="暂无公开博客" />
      <template v-else>
        <article v-if="featuredArticle" class="featured-article">
          <time>{{ formatDate(featuredArticle.createTime) }}</time>
          <h2>{{ featuredArticle.title }}</h2>
          <p>
            {{
              featuredArticle.summary ||
              stripMarkdown(featuredArticle.contentMarkdown) ||
              '这篇博客还没有摘要。'
            }}
          </p>
        </article>

        <section class="article-index">
          <div class="index-heading">
            <p>Article Index</p>
            <h2>全部文章</h2>
          </div>
          <article v-for="item in restArticles" :key="item.id" class="article-row">
            <time>{{ formatDate(item.createTime) }}</time>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.summary || stripMarkdown(item.contentMarkdown) || '这篇博客还没有摘要。' }}</p>
            </div>
          </article>
        </section>
      </template>
    </section>
  </main>
</template>

<script lang="ts" setup>
import { getAccessToken } from '@/utils/auth'
import * as BlogApi from '@/api/blog/article'
import { formatDate as formatDateValue } from '@/utils/formatTime'

defineOptions({ name: 'SiteBlog' })

const router = useRouter()
const hasToken = computed(() => Boolean(getAccessToken()))
const loading = ref(true)
const list = ref<BlogApi.BlogArticleVO[]>([])
const featuredArticle = computed(() => list.value[0])
const restArticles = computed(() => list.value.slice(1))

const formatDate = (value?: Date | string | number) => formatDateValue(value, 'YYYY-MM-DD')

const stripMarkdown = (value?: string) => {
  return (value || '')
    .replace(/```[\s\S]*?```/g, '')
    .replace(/[#>*_`-]/g, '')
    .slice(0, 160)
}

const getList = async () => {
  loading.value = true
  try {
    const data = await BlogApi.getBlogArticlePage({ pageNo: 1, pageSize: 9 })
    list.value = data.list || []
  } finally {
    loading.value = false
  }
}

onMounted(() => getList())
</script>

<style lang="scss" scoped>
.blog-site {
  min-height: 100%;
  overflow-y: auto;
  background: #f7f8fa;
  color: #17191f;
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #dfe3e8;
  padding: 14px clamp(16px, 5vw, 64px);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
}

.brand,
nav {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand {
  cursor: pointer;
  color: #17191f;
  font-size: 20px;
  font-weight: 700;

  img {
    width: 36px;
    height: 36px;
    border-radius: 8px;
  }
}

.hero {
  max-width: 1040px;
  margin: 0 auto;
  padding: clamp(48px, 8vw, 88px) 16px 34px;

  .eyebrow {
    margin: 0 0 12px;
    color: #4b5f7a;
    font-size: 14px;
  }

  h1 {
    margin: 0;
    color: #17191f;
    font-size: clamp(44px, 7vw, 86px);
    font-weight: 700;
    line-height: 1;
  }

  p:last-child {
    max-width: 620px;
    margin: 22px 0 0;
    color: #5d6470;
    font-size: 16px;
    line-height: 1.85;
  }
}

.content {
  max-width: 1040px;
  margin: 0 auto;
  padding: 0 16px 64px;
}

.site-loading {
  display: flex;
  min-height: 220px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;

  span {
    width: 44px;
    height: 44px;
    border: 4px solid #dce3ea;
    border-top-color: #31445f;
    border-radius: 50%;
    animation: spin 0.9s linear infinite;
  }
}

.featured-article {
  border-top: 1px solid #cfd6dd;
  border-bottom: 1px solid #cfd6dd;
  padding: clamp(24px, 4vw, 36px) 0;

  time {
    color: #4b5f7a;
    font-size: 13px;
  }

  h2 {
    max-width: 760px;
    margin: 12px 0 14px;
    color: #17191f;
    font-size: clamp(28px, 4vw, 44px);
    font-weight: 650;
    line-height: 1.18;
  }

  p {
    max-width: 680px;
    margin: 0;
    color: #5d6470;
    font-size: 16px;
    line-height: 1.85;
  }
}

.article-index {
  margin-top: 34px;
}

.index-heading {
  margin-bottom: 12px;

  p {
    margin: 0 0 6px;
    color: #4b5f7a;
    font-size: 13px;
  }

  h2 {
    margin: 0;
    color: #17191f;
    font-size: 24px;
    font-weight: 650;
  }
}

.article-row {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr);
  gap: 24px;
  border-top: 1px solid #dfe3e8;
  padding: 20px 0;

  time {
    color: #6b7280;
    font-size: 13px;
  }

  h3 {
    margin: 0 0 8px;
    color: #17191f;
    font-size: 20px;
    font-weight: 650;
    line-height: 1.35;
  }

  p {
    max-width: 700px;
    margin: 0;
    color: #5d6470;
    line-height: 1.75;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 760px) {
  .site-header,
  nav {
    flex-wrap: wrap;
  }

  .article-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}
</style>
