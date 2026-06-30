<template>
  <section>
    <div class="mb-18px flex items-end justify-between gap-16px">
      <div>
        <p class="mb-6px text-13px text-[var(--el-color-primary)]">Blog</p>
        <h2 class="m-0 text-26px font-600 text-[#17202a]">公开博客</h2>
      </div>
      <el-button text type="primary" @click="$emit('switch-home', 'diary')">看看日记</el-button>
    </div>
    <div v-if="loading" class="site-loading">
      <span></span>
      <p>正在整理文章...</p>
    </div>
    <el-empty v-else-if="list.length === 0" description="暂无公开博客" />
    <div v-else class="grid grid-cols-1 gap-14px">
      <article v-for="item in list" :key="item.id" class="entry">
        <div class="flex items-center justify-between gap-12px">
          <h3>{{ item.title }}</h3>
          <time>{{ formatDate(item.createTime) }}</time>
        </div>
        <p>{{ item.summary || stripMarkdown(item.contentMarkdown) || '这篇博客还没有摘要。' }}</p>
      </article>
    </div>
  </section>
</template>

<script lang="ts" setup>
import * as BlogApi from '@/api/blog/article'
import { formatDate as formatDateValue } from '@/utils/formatTime'

defineEmits<{
  (event: 'switch-home', value: 'diary'): void
}>()

const loading = ref(true)
const list = ref<BlogApi.BlogArticleVO[]>([])

const formatDate = (value?: Date | string | number) => formatDateValue(value, 'YYYY-MM-DD')

const stripMarkdown = (value?: string) => {
  return (value || '')
    .replace(/```[\s\S]*?```/g, '')
    .replace(/[#>*_`-]/g, '')
    .slice(0, 140)
}

const getList = async () => {
  loading.value = true
  try {
    const data = await BlogApi.getBlogArticlePage({ pageNo: 1, pageSize: 8 })
    list.value = data.list || []
  } finally {
    loading.value = false
  }
}

onMounted(() => getList())
</script>

<style lang="scss" scoped>
.entry {
  border: 1px solid #dbe4ee;
  border-radius: 8px;
  padding: 16px;
  background: #fff;

  h3 {
    margin: 0;
    color: #17202a;
    font-size: 17px;
  }

  time,
  p {
    color: #64748b;
  }

  p {
    margin: 10px 0 0;
    line-height: 1.7;
  }
}
</style>
