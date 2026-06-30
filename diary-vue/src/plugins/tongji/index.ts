import router from '@/router'

declare global {
  interface Window {
    _hmt: unknown[][]
  }
}

window._hmt = window._hmt || []
const HM_ID = import.meta.env.VITE_APP_BAIDU_CODE

;(function () {
  if (!HM_ID) return
  const hm = document.createElement('script')
  hm.src = 'http://localhost/hm.js?' + HM_ID
  const s = document.getElementsByTagName('script')[0]
  s.parentNode?.insertBefore(hm, s)
})()

router.afterEach((to) => {
  if (!HM_ID) return
  window._hmt.push(['_trackPageview', to.fullPath])
})
