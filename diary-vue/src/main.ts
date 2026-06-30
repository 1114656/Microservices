import '@/plugins/unocss'
import '@/plugins/svgIcon'
import { setupI18n } from '@/plugins/vueI18n'
import { setupStore } from '@/store'
import { setupGlobCom } from '@/components'
import { setupElementPlus } from '@/plugins/elementPlus'
import '@/styles/index.scss'
import '@/plugins/animate.css'
import router, { setupRouter } from '@/router'
import { setupAuth, setupMountedFocus } from '@/directives'
import { createApp } from 'vue'
import App from './App.vue'
import './permission'
import '@/plugins/tongji'
import Logger from '@/utils/Logger'
import VueDOMPurifyHTML from 'vue-dompurify-html'
import print from 'vue3-print-nb'

const setupAll = async () => {
  const app = createApp(App)

  await setupI18n(app)
  setupStore(app)
  setupGlobCom(app)
  setupElementPlus(app)
  setupRouter(app)
  setupAuth(app)
  setupMountedFocus(app)

  await router.isReady()

  app.use(VueDOMPurifyHTML)
  app.use(print)
  app.mount('#app')
}

setupAll()

Logger.prettyPrimary('欢迎使用', import.meta.env.VITE_APP_TITLE)
