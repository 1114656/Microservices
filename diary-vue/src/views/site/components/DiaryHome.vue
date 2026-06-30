<template>
  <section class="diary-home">
    <div class="diary-home-hero">
      <img src="@/assets/imgs/site/gardenia-hero.png" alt="" aria-hidden="true" />
      <div>
        <p>Diary Signal</p>
        <h2>公开日记</h2>
        <span>从最近的日常里，抽取几束正在发亮的片段。</span>
      </div>
      <el-button class="switch-button" @click="$emit('switch-home', 'blog')">看看博客</el-button>
    </div>

    <div v-if="loading" class="site-loading">
      <span></span>
      <p>正在同步日记...</p>
    </div>
    <el-empty v-else-if="list.length === 0" description="暂无公开日记" />
    <div v-else class="entry-grid">
      <article
        v-for="(item, index) in list"
        :key="item.id"
        class="entry"
        :style="{ '--entry-index': index }"
      >
        <div class="entry-index">{{ String(index + 1).padStart(2, '0') }}</div>
        <div>
          <div class="entry-head">
            <h3>{{ item.title }}</h3>
            <time>{{ formatDate(item.createTime) }}</time>
          </div>
          <p>{{ item.summary || pickText(item.contentBlocks) || '这篇日记还没有摘要。' }}</p>
        </div>
      </article>
    </div>
  </section>
</template>

<script lang="ts" setup>
import * as DiaryApi from '@/api/diary/entry'
import { formatDate as formatDateValue } from '@/utils/formatTime'

defineEmits<{
  (event: 'switch-home', value: 'blog'): void
}>()

const loading = ref(true)
const list = ref<DiaryApi.DiaryEntryVO[]>([])

const formatDate = (value?: Date | string | number) => formatDateValue(value, 'YYYY-MM-DD')

const pickText = (blocks?: DiaryApi.DiaryContentBlockVO[]) => {
  return blocks?.find((block) => block.type === 'text')?.content
}

const getList = async () => {
  loading.value = true
  try {
    const data = await DiaryApi.getDiaryPage({ pageNo: 1, pageSize: 8 })
    list.value = data.list || []
  } finally {
    loading.value = false
  }
}

onMounted(() => getList())
</script>

<style lang="scss" scoped>
.diary-home {
  color: #151a22;
}

.diary-home-hero {
  position: relative;
  display: grid;
  min-height: 210px;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: end;
  gap: 18px;
  overflow: hidden;
  border: 1px solid rgba(16, 24, 34, 0.12);
  border-radius: 8px;
  padding: 22px;
  background: #111722;
  box-shadow: 0 24px 70px rgba(12, 18, 28, 0.14);

  img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    filter: saturate(1.08) contrast(1.04) brightness(0.74);
    transition: transform 0.5s ease;
  }

  &:hover img {
    transform: scale(1.04);
  }

  &::after {
    position: absolute;
    inset: 0;
    background: linear-gradient(90deg, rgba(8, 12, 20, 0.92), rgba(8, 12, 20, 0.36));
    content: '';
  }

  > div,
  .switch-button {
    position: relative;
    z-index: 1;
  }

  p {
    margin: 0 0 8px;
    color: #7efce5;
    font-size: 12px;
    font-weight: 800;
    text-transform: uppercase;
  }

  h2 {
    margin: 0;
    color: #fffdf3;
    font-size: clamp(28px, 4vw, 42px);
    font-weight: 820;
    line-height: 1.08;
  }

  span {
    display: block;
    max-width: 420px;
    margin-top: 10px;
    color: rgba(247, 251, 255, 0.72);
    line-height: 1.75;
  }
}

.switch-button {
  border: 1px solid rgba(126, 252, 229, 0.5);
  border-radius: 8px;
  background: rgba(126, 252, 229, 0.12);
  color: #f7fbff;
  backdrop-filter: blur(12px);

  &:hover,
  &:focus {
    border-color: rgba(255, 217, 117, 0.66);
    background: rgba(255, 217, 117, 0.16);
    color: #fff;
  }
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
    border: 3px solid rgba(13, 118, 106, 0.2);
    border-top-color: #0d766a;
    border-radius: 50%;
    animation: spin 0.9s linear infinite;
  }
}

.entry-grid {
  display: grid;
  gap: 14px;
  margin-top: 14px;
}

.entry {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  gap: 14px;
  border: 1px solid rgba(16, 24, 34, 0.1);
  border-radius: 8px;
  padding: 16px;
  background: rgba(255, 255, 252, 0.96);
  box-shadow: 0 18px 46px rgba(12, 18, 28, 0.08);
  opacity: 0;
  transform: translate3d(0, 14px, 0);
  animation: entry-rise 0.52s cubic-bezier(0.2, 0.72, 0.2, 1) forwards;
  animation-delay: calc(0.06s * var(--entry-index));
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;

  &:hover {
    border-color: rgba(13, 118, 106, 0.28);
    box-shadow: 0 24px 64px rgba(12, 18, 28, 0.13);
    transform: translate3d(0, -2px, 0);
  }
}

.entry-index {
  display: flex;
  width: 52px;
  height: 52px;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: #111722;
  color: #7efce5;
  font-weight: 850;
}

.entry-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.entry {
  h3 {
    margin: 0;
    color: #151a22;
    font-size: 18px;
    font-weight: 800;
    line-height: 1.35;
  }

  time {
    flex: none;
    color: #0d766a;
    font-size: 13px;
    font-weight: 700;
  }

  p {
    margin: 10px 0 0;
    color: #5e6876;
    line-height: 1.75;
  }
}

@keyframes entry-rise {
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (prefers-reduced-motion: reduce) {
  .diary-home-hero img,
  .entry,
  .site-loading span {
    opacity: 1;
    transform: none;
    animation: none;
    transition: none;
  }
}

@media (max-width: 680px) {
  .diary-home-hero {
    grid-template-columns: 1fr;
    align-items: start;
  }

  .entry {
    grid-template-columns: 1fr;
  }

  .entry-index {
    width: 44px;
    height: 44px;
  }

  .entry-head {
    display: block;
  }

  .entry time {
    display: block;
    margin-top: 8px;
  }
}
</style>
