<template>
  <main class="site-entry">
    <div class="site-loading">
      <span></span>
      <p>正在打开默认首页...</p>
    </div>
  </main>
</template>

<script lang="ts" setup>
import {
  DEFAULT_HOME_PAGE_TYPE,
  getPublicSiteConfig,
  type HomePageType
} from '@/api/site/config'

defineOptions({ name: 'SiteHome' })

const router = useRouter()

const loadConfig = async () => {
  let homePageType: HomePageType = DEFAULT_HOME_PAGE_TYPE
  try {
    const config = await getPublicSiteConfig()
    homePageType = config?.homePageType || DEFAULT_HOME_PAGE_TYPE
  } catch {
    homePageType = DEFAULT_HOME_PAGE_TYPE
  }

  const target = homePageType === 'blog' ? '/blog' : '/diary'
  router.replace(target)
}

onMounted(() => loadConfig())
</script>

<style lang="scss" scoped>
.site-entry {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
  background: #f5f7fb;
}

.site-loading {
  display: flex;
  min-height: 220px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;

  span {
    width: 52px;
    height: 40px;
    border: 3px solid var(--el-color-primary);
    border-radius: 4px;
    background:
      linear-gradient(90deg, transparent 48%, var(--el-color-primary) 48% 52%, transparent 52%),
      linear-gradient(#fff, #fff);
    animation: page-flip 1.05s ease-in-out infinite;
  }
}

@keyframes page-flip {
  0%,
  100% {
    transform: perspective(140px) rotateY(0deg);
  }

  50% {
    transform: perspective(140px) rotateY(-18deg);
  }
}
</style>
