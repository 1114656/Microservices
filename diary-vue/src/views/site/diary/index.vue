<template>
  <main
    ref="pageRef"
    class="diary-site"
    :style="pageMotionStyle"
    @scroll="handlePageScroll"
  >
    <header class="site-header" :class="{ 'header-scrolled': isHeaderScrolled }">
      <div class="brand" @click="router.push('/diary')">
        <img alt="Diary" src="@/assets/imgs/logo.png" />
        <span>Diary</span>
      </div>
      <nav>
        <el-button text @click="router.push('/blog')">博客</el-button>
        <el-button v-if="hasToken" class="glass-button solid" @click="router.push('/index')">
          进入后台
        </el-button>
        <template v-else>
          <el-button text @click="router.push('/login')">登录</el-button>
          <el-button class="glass-button solid" @click="router.push('/register')">注册</el-button>
        </template>
      </nav>
    </header>

    <section class="hero">
      <div class="hero-media" aria-hidden="true">
        <img class="hero-image forest" src="@/assets/imgs/site/green-planet-hero.png" alt="" />
        <img class="hero-image flower" src="@/assets/imgs/site/gardenia-hero.png" alt="" />
        <div class="hero-film"></div>
      </div>

      <div class="hero-copy">
        <p class="eyebrow">LIVE MEMORY CAPTURE</p>
        <h1>把今天，写成一场发光的抵达</h1>
        <p class="hero-subtitle">
          日记不是流水账，是把一瞬间从生活里点亮。这里收集公开的片段、心情、光线和时间留下的回声。
        </p>
        <div class="hero-actions">
          <el-button class="glass-button solid" @click="scrollToDiary">
            <Icon icon="ep:reading" />
            阅读日记
          </el-button>
          <el-button class="glass-button" @click="router.push('/blog')">
            <Icon icon="ep:document" />
            浏览博客
          </el-button>
        </div>

        <div class="hero-metrics" aria-label="Diary overview">
          <div>
            <strong>{{ entryCountText }}</strong>
            <span>公开记录</span>
          </div>
          <div>
            <strong>{{ today }}</strong>
            <span>今日坐标</span>
          </div>
          <div>
            <strong>PUBLIC</strong>
            <span>可见状态</span>
          </div>
        </div>
      </div>

      <aside class="signal-panel" aria-label="Latest diary">
        <div class="panel-topline">
          <span></span>
          <p>RECENT SIGNAL</p>
        </div>
        <template v-if="featuredEntry">
          <time>{{ formatDate(featuredEntry.createTime) }}</time>
          <h2>{{ featuredEntry.title }}</h2>
          <p>{{ getEntrySummary(featuredEntry) }}</p>
        </template>
        <template v-else>
          <time>{{ today }}</time>
          <h2>等待第一篇公开日记</h2>
          <p>发布后，这里会立刻出现最新记录。</p>
        </template>
      </aside>

      <button class="scroll-cue" type="button" @click="scrollToDiary">
        <span>向下阅读</span>
      </button>
    </section>

    <section
      ref="contentRef"
      class="content"
      :class="{ 'content-visible': isContentVisible }"
    >
      <div class="content-head">
        <p>Chronicle Stream</p>
        <h2>公开日记流</h2>
        <span>按时间展开，每一条都是从日常里截取的一束光。</span>
      </div>

      <div v-if="loading" class="site-loading">
        <span></span>
        <p>正在同步日记信号...</p>
      </div>

      <div v-else-if="list.length === 0" class="empty-state">
        <p>Public Diary</p>
        <h3>这里还没有公开日记</h3>
        <span>在后台发布一篇公开日记后，这个页面会自动点亮。</span>
      </div>

      <template v-else>
        <article v-if="featuredEntry" class="featured-log">
          <div class="featured-date">
            <span>{{ formatDate(featuredEntry.createTime) }}</span>
          </div>
          <div>
            <p>FEATURED ENTRY</p>
            <h3>{{ featuredEntry.title }}</h3>
            <span>{{ getEntrySummary(featuredEntry) }}</span>
          </div>
        </article>

        <div class="timeline">
          <article
            v-for="(item, index) in restEntries"
            :key="item.id"
            class="timeline-entry"
            :style="{ '--entry-index': index }"
          >
            <div class="timeline-marker">
              <span>{{ String(index + 2).padStart(2, '0') }}</span>
            </div>
            <div class="entry-body">
              <time>{{ formatDate(item.createTime) }}</time>
              <h3>{{ item.title }}</h3>
              <p>{{ getEntrySummary(item) }}</p>
            </div>
          </article>
        </div>
      </template>
    </section>
  </main>
</template>

<script lang="ts" setup>
import { getAccessToken } from '@/utils/auth'
import * as DiaryApi from '@/api/diary/entry'
import { formatDate as formatDateValue } from '@/utils/formatTime'

defineOptions({ name: 'SiteDiary' })

const router = useRouter()
const pageRef = ref<HTMLElement>()
const contentRef = ref<HTMLElement>()
const isHeaderScrolled = ref(false)
const scrollProgress = ref(0)
const isContentVisible = ref(false)
let contentObserver: IntersectionObserver | undefined
const hasToken = computed(() => Boolean(getAccessToken()))
const loading = ref(true)
const list = ref<DiaryApi.DiaryEntryVO[]>([])
const featuredEntry = computed(() => list.value[0])
const restEntries = computed(() => list.value.slice(1))
const entryCountText = computed(() => String(list.value.length || 0).padStart(2, '0'))
const pageMotionStyle = computed(() => ({
  '--scroll-progress': scrollProgress.value.toFixed(3),
  '--hero-rise': `${scrollProgress.value * -80}px`,
  '--media-scale': (1.08 + scrollProgress.value * 0.08).toFixed(3)
}))
const today = new Intl.DateTimeFormat('zh-CN', {
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(new Date())

const formatDate = (value?: Date | string | number) => formatDateValue(value, 'YYYY-MM-DD')

const pickText = (blocks?: DiaryApi.DiaryContentBlockVO[]) => {
  return blocks?.find((block) => block.type === 'text')?.content
}

const getEntrySummary = (entry?: DiaryApi.DiaryEntryVO) => {
  if (!entry) return '这篇日记还没有摘要。'
  return entry.summary || pickText(entry.contentBlocks) || '这篇日记还没有摘要。'
}

const handlePageScroll = () => {
  const scrollTop = pageRef.value?.scrollTop || 0
  const heroHeight = pageRef.value?.clientHeight || 1
  isHeaderScrolled.value = scrollTop > 28
  scrollProgress.value = Math.min(scrollTop / heroHeight, 1)
}

const scrollToDiary = () => {
  contentRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const getList = async () => {
  loading.value = true
  try {
    const data = await DiaryApi.getPublicDiaryPage({ pageNo: 1, pageSize: 8 })
    list.value = data.list || []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
  if (contentRef.value) {
    contentObserver = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          isContentVisible.value = true
          contentObserver?.disconnect()
        }
      },
      { root: pageRef.value, threshold: 0.16 }
    )
    contentObserver.observe(contentRef.value)
  }
})

onBeforeUnmount(() => {
  contentObserver?.disconnect()
})
</script>

<style lang="scss" scoped>
.diary-site {
  height: 100%;
  min-height: 100%;
  overflow-y: auto;
  scroll-behavior: smooth;
  background: #06080f;
  color: #f7fbff;
}

.site-header {
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(235, 251, 255, 0.1);
  padding: 16px clamp(18px, 5vw, 72px);
  background: rgba(6, 8, 15, 0.06);
  transition:
    background 0.24s ease,
    border-color 0.24s ease,
    box-shadow 0.24s ease,
    backdrop-filter 0.24s ease;

  :deep(.el-button.is-text) {
    color: rgba(247, 251, 255, 0.82);
  }

  :deep(.el-button.is-text:hover) {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
  }
}

.header-scrolled {
  border-color: rgba(108, 243, 225, 0.24);
  background: rgba(5, 9, 18, 0.78);
  box-shadow: 0 18px 60px rgba(0, 0, 0, 0.22);
  backdrop-filter: blur(18px);
}

.brand,
nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand {
  cursor: pointer;
  color: #f7fbff;
  font-size: 20px;
  font-weight: 750;

  img {
    width: 36px;
    height: 36px;
    border: 1px solid rgba(255, 255, 255, 0.28);
    border-radius: 8px;
    box-shadow: 0 0 28px rgba(108, 243, 225, 0.18);
  }
}

.glass-button {
  border: 1px solid rgba(247, 251, 255, 0.22);
  border-radius: 8px;
  background: rgba(247, 251, 255, 0.08);
  color: #f7fbff;
  backdrop-filter: blur(16px);

  &:hover,
  &:focus {
    border-color: rgba(126, 252, 229, 0.5);
    background: rgba(126, 252, 229, 0.14);
    color: #fff;
  }
}

.glass-button.solid {
  border-color: rgba(126, 252, 229, 0.58);
  background: linear-gradient(135deg, rgba(31, 214, 183, 0.28), rgba(255, 217, 117, 0.18));
  box-shadow: 0 16px 42px rgba(12, 195, 166, 0.18);
}

.hero {
  position: relative;
  display: grid;
  min-height: 100svh;
  grid-template-columns: minmax(0, 1fr) minmax(300px, 420px);
  align-items: end;
  gap: clamp(28px, 5vw, 72px);
  overflow: hidden;
  padding: 112px clamp(18px, 6vw, 86px) 86px;
}

.hero-media {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background: #06080f;
}

.hero-image {
  position: absolute;
  inset: -5%;
  width: 110%;
  height: 110%;
  object-fit: cover;
  transform: translate3d(0, var(--hero-rise), 0) scale(var(--media-scale));
  transition: transform 0.08s linear;
  will-change: transform;
}

.hero-image.forest {
  filter: saturate(1.04) contrast(1.06) brightness(0.72);
}

.hero-image.flower {
  inset: auto clamp(18px, 5vw, 72px) 10vh auto;
  width: min(38vw, 560px);
  height: min(34vw, 460px);
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  box-shadow:
    0 32px 110px rgba(0, 0, 0, 0.42),
    0 0 70px rgba(255, 234, 172, 0.16);
  filter: saturate(1.1) contrast(1.02);
  object-position: 62% 48%;
  opacity: calc(0.74 - var(--scroll-progress) * 0.34);
  transform: translate3d(0, calc(var(--hero-rise) * -0.32), 0) scale(1);
}

.hero-film {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(4, 7, 13, 0.94) 0%, rgba(4, 7, 13, 0.66) 40%, rgba(4, 7, 13, 0.2) 100%),
    linear-gradient(180deg, rgba(6, 8, 15, 0.12), #06080f 96%);
}

.hero-copy,
.signal-panel,
.scroll-cue {
  position: relative;
  z-index: 1;
}

.hero-copy {
  max-width: 840px;
  transform: translate3d(0, calc(var(--scroll-progress) * -34px), 0);

  .eyebrow {
    width: fit-content;
    margin: 0 0 18px;
    border: 1px solid rgba(126, 252, 229, 0.36);
    border-radius: 999px;
    padding: 8px 12px;
    background: rgba(6, 8, 15, 0.36);
    color: #7efce5;
    font-size: 12px;
    font-weight: 750;
    letter-spacing: 0;
    animation: rise-in 0.72s cubic-bezier(0.2, 0.72, 0.2, 1) 0.08s both;
    backdrop-filter: blur(12px);
  }

  h1 {
    max-width: 800px;
    margin: 0;
    color: #fffdf3;
    font-size: clamp(48px, 8vw, 104px);
    font-weight: 820;
    line-height: 0.98;
    text-shadow:
      0 24px 90px rgba(0, 0, 0, 0.64),
      0 0 44px rgba(126, 252, 229, 0.16);
    animation: title-strike 0.92s cubic-bezier(0.2, 0.72, 0.2, 1) 0.18s both;
  }
}

.hero-subtitle {
  max-width: 650px;
  margin: 26px 0 0;
  color: rgba(247, 251, 255, 0.82);
  font-size: 18px;
  line-height: 1.85;
  animation: rise-in 0.74s cubic-bezier(0.2, 0.72, 0.2, 1) 0.42s both;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 34px;
  animation: rise-in 0.7s cubic-bezier(0.2, 0.72, 0.2, 1) 0.58s both;

  :deep(.el-button) {
    min-height: 42px;
  }
}

.hero-metrics {
  display: grid;
  max-width: 780px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 34px;
  animation: rise-in 0.72s cubic-bezier(0.2, 0.72, 0.2, 1) 0.7s both;

  div {
    border: 1px solid rgba(247, 251, 255, 0.13);
    border-radius: 8px;
    padding: 14px;
    background: rgba(6, 8, 15, 0.34);
    backdrop-filter: blur(14px);
  }

  strong,
  span {
    display: block;
  }

  strong {
    color: #fffbe8;
    font-size: clamp(18px, 2.4vw, 28px);
    line-height: 1.1;
  }

  span {
    margin-top: 6px;
    color: rgba(247, 251, 255, 0.58);
    font-size: 12px;
  }
}

.signal-panel {
  align-self: center;
  border: 1px solid rgba(247, 251, 255, 0.16);
  border-radius: 8px;
  padding: 24px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.04)),
    rgba(6, 8, 15, 0.42);
  box-shadow:
    0 32px 100px rgba(0, 0, 0, 0.36),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  animation: panel-arrive 0.86s cubic-bezier(0.2, 0.72, 0.2, 1) 0.66s both;
  backdrop-filter: blur(18px);

  time {
    display: block;
    margin-top: 22px;
    color: #ffd975;
    font-size: 13px;
    font-weight: 700;
  }

  h2 {
    margin: 12px 0 12px;
    color: #fffdf3;
    font-size: clamp(22px, 3vw, 34px);
    font-weight: 760;
    line-height: 1.15;
  }

  p {
    margin: 0;
    color: rgba(247, 251, 255, 0.72);
    line-height: 1.78;
  }
}

.panel-topline {
  display: flex;
  align-items: center;
  gap: 10px;

  span {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: #7efce5;
    box-shadow: 0 0 24px #7efce5;
    animation: pulse-dot 1.6s ease-in-out infinite;
  }

  p {
    color: rgba(247, 251, 255, 0.58);
    font-size: 12px;
    font-weight: 750;
    letter-spacing: 0;
  }
}

.scroll-cue {
  position: absolute;
  bottom: 24px;
  left: 50%;
  display: inline-flex;
  min-width: 122px;
  height: 38px;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(247, 251, 255, 0.18);
  border-radius: 999px;
  background: rgba(6, 8, 15, 0.32);
  color: rgba(247, 251, 255, 0.7);
  cursor: pointer;
  font-size: 12px;
  transform: translateX(-50%);
  animation: cue-float 2.4s ease-in-out 1.1s infinite;
  backdrop-filter: blur(12px);
}

.content {
  position: relative;
  min-height: 100svh;
  overflow: hidden;
  padding: clamp(58px, 8vw, 104px) clamp(18px, 6vw, 86px) 82px;
  background:
    linear-gradient(180deg, #06080f, #0b101d 42%, #f5f7f0 42%),
    #f5f7f0;
  color: #151a22;
}

.content-head,
.featured-log,
.timeline,
.empty-state,
.site-loading {
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin-right: auto;
  margin-left: auto;
  opacity: 0;
  transform: translate3d(0, 28px, 0);
  transition:
    opacity 0.72s cubic-bezier(0.2, 0.72, 0.2, 1),
    transform 0.72s cubic-bezier(0.2, 0.72, 0.2, 1);
}

.content-visible {
  .content-head,
  .featured-log,
  .timeline,
  .empty-state,
  .site-loading {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }

  .featured-log,
  .empty-state,
  .site-loading {
    transition-delay: 0.12s;
  }

  .timeline {
    transition-delay: 0.2s;
  }
}

.content-head {
  margin-bottom: 24px;

  p {
    margin: 0 0 8px;
    color: #7efce5;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0;
    text-transform: uppercase;
  }

  h2 {
    margin: 0;
    color: #f7fbff;
    font-size: clamp(34px, 5vw, 62px);
    font-weight: 820;
    line-height: 1.05;
  }

  span {
    display: block;
    max-width: 560px;
    margin-top: 14px;
    color: rgba(247, 251, 255, 0.7);
    line-height: 1.8;
  }
}

.site-loading {
  display: flex;
  min-height: 320px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: rgba(247, 251, 255, 0.76);

  span {
    width: 58px;
    height: 58px;
    border: 3px solid rgba(126, 252, 229, 0.2);
    border-top-color: #7efce5;
    border-radius: 50%;
    animation: spin 0.92s linear infinite;
  }
}

.empty-state,
.featured-log {
  border: 1px solid rgba(16, 24, 34, 0.12);
  border-radius: 8px;
  background: rgba(255, 255, 252, 0.94);
  box-shadow: 0 28px 90px rgba(12, 18, 28, 0.14);
}

.empty-state {
  min-height: 340px;
  padding: clamp(30px, 6vw, 64px);

  p {
    margin: 0 0 14px;
    color: #0d766a;
    font-size: 12px;
    font-weight: 800;
    text-transform: uppercase;
  }

  h3 {
    max-width: 620px;
    margin: 0;
    color: #151a22;
    font-size: clamp(30px, 4vw, 48px);
    font-weight: 820;
    line-height: 1.16;
  }

  span {
    display: block;
    max-width: 520px;
    margin-top: 18px;
    color: #5e6876;
    font-size: 16px;
    line-height: 1.85;
  }
}

.featured-log {
  display: grid;
  grid-template-columns: minmax(120px, 190px) minmax(0, 1fr);
  gap: clamp(18px, 4vw, 48px);
  align-items: stretch;
  padding: clamp(24px, 5vw, 48px);
}

.featured-date {
  display: flex;
  min-height: 170px;
  align-items: end;
  border-radius: 8px;
  padding: 18px;
  background:
    linear-gradient(180deg, rgba(16, 24, 34, 0.2), rgba(16, 24, 34, 0.86)),
    url('@/assets/imgs/site/gardenia-hero.png') center / cover no-repeat;

  span {
    color: #fffdf3;
    font-size: 18px;
    font-weight: 800;
    text-shadow: 0 12px 32px rgba(0, 0, 0, 0.45);
  }
}

.featured-log {
  p {
    margin: 0 0 10px;
    color: #0d766a;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0;
  }

  h3 {
    max-width: 760px;
    margin: 0 0 14px;
    color: #111722;
    font-size: clamp(30px, 4.4vw, 56px);
    font-weight: 820;
    line-height: 1.08;
  }

  div > span {
    display: block;
    max-width: 760px;
    color: #5a6472;
    font-size: 17px;
    line-height: 1.9;
  }
}

.timeline {
  display: grid;
  gap: 14px;
  margin-top: 14px;
}

.timeline-entry {
  display: grid;
  grid-template-columns: 74px minmax(0, 1fr);
  gap: 14px;
  opacity: 0;
  transform: translate3d(0, 18px, 0);
  animation: entry-arrive 0.62s cubic-bezier(0.2, 0.72, 0.2, 1) forwards;
  animation-delay: calc(0.08s * var(--entry-index));
}

.timeline-marker {
  display: flex;
  min-height: 152px;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(16, 24, 34, 0.12);
  border-radius: 8px;
  background: #111722;
  box-shadow: inset 0 0 0 1px rgba(126, 252, 229, 0.1);

  span {
    color: #7efce5;
    font-size: 18px;
    font-weight: 850;
  }
}

.entry-body {
  min-height: 152px;
  border: 1px solid rgba(16, 24, 34, 0.12);
  border-radius: 8px;
  padding: 22px;
  background: rgba(255, 255, 252, 0.92);
  box-shadow: 0 18px 48px rgba(12, 18, 28, 0.08);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;

  &:hover {
    border-color: rgba(13, 118, 106, 0.32);
    box-shadow: 0 26px 70px rgba(12, 18, 28, 0.14);
    transform: translate3d(0, -3px, 0);
  }

  time {
    color: #0d766a;
    font-size: 13px;
    font-weight: 750;
  }

  h3 {
    margin: 12px 0 10px;
    color: #151a22;
    font-size: clamp(21px, 2.5vw, 30px);
    font-weight: 800;
    line-height: 1.25;
  }

  p {
    margin: 0;
    color: #5e6876;
    line-height: 1.85;
  }
}

@keyframes title-strike {
  from {
    opacity: 0;
    transform: translate3d(0, 32px, 0) scale(0.98);
    filter: blur(12px);
  }

  to {
    opacity: 1;
    transform: translate3d(0, 0, 0) scale(1);
    filter: blur(0);
  }
}

@keyframes rise-in {
  from {
    opacity: 0;
    transform: translate3d(0, 24px, 0);
    filter: blur(8px);
  }

  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
    filter: blur(0);
  }
}

@keyframes panel-arrive {
  from {
    opacity: 0;
    transform: translate3d(36px, 24px, 0) rotate(1deg);
    filter: blur(10px);
  }

  to {
    opacity: 1;
    transform: translate3d(0, 0, 0) rotate(0);
    filter: blur(0);
  }
}

@keyframes cue-float {
  0%,
  100% {
    transform: translateX(-50%) translateY(0);
  }

  50% {
    transform: translateX(-50%) translateY(6px);
  }
}

@keyframes pulse-dot {
  0%,
  100% {
    opacity: 0.65;
    transform: scale(0.92);
  }

  50% {
    opacity: 1;
    transform: scale(1.16);
  }
}

@keyframes entry-arrive {
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
  .diary-site {
    scroll-behavior: auto;
  }

  .hero-image,
  .hero-copy,
  .hero-copy .eyebrow,
  .hero-copy h1,
  .hero-subtitle,
  .hero-actions,
  .hero-metrics,
  .signal-panel,
  .scroll-cue,
  .content-head,
  .featured-log,
  .timeline,
  .empty-state,
  .site-loading,
  .timeline-entry,
  .panel-topline span {
    opacity: 1;
    transform: none;
    animation: none;
    transition: none;
    filter: none;
  }
}

@media (max-width: 980px) {
  .site-header,
  nav {
    flex-wrap: wrap;
  }

  .site-header {
    padding: 14px 18px;
  }

  .hero {
    grid-template-columns: 1fr;
    align-content: center;
    padding-top: 132px;
  }

  .hero-image.flower {
    top: 116px;
    right: 18px;
    bottom: auto;
    width: min(48vw, 360px);
    height: min(44vw, 300px);
    opacity: 0.46;
  }

  .signal-panel {
    max-width: 520px;
  }
}

@media (max-width: 680px) {
  .brand {
    width: 100%;
  }

  nav {
    width: 100%;
    justify-content: space-between;
  }

  .hero {
    min-height: auto;
    padding-bottom: 96px;
  }

  .hero-copy h1 {
    font-size: 44px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-metrics,
  .featured-log,
  .timeline-entry {
    grid-template-columns: 1fr;
  }

  .timeline-marker {
    min-height: 52px;
    justify-content: flex-start;
    padding: 0 18px;
  }

  .featured-date {
    min-height: 136px;
  }
}
</style>
