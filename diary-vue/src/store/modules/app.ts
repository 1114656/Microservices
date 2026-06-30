import { CACHE_KEY, useCache } from '@/hooks/web/useCache'
import { ElementPlusSize } from '@/types/elementPlus'
import { LayoutType } from '@/types/layout'
import { ThemeTypes } from '@/types/theme'
import { humpToUnderline, setCssVar } from '@/utils'
import { getCssColorVariable, hexToRGB, mix } from '@/utils/color'
import { ElMessage } from 'element-plus'
import { defineStore } from 'pinia'
import { store } from '../index'

const { wsCache } = useCache()

interface AppState {
  breadcrumb: boolean
  breadcrumbIcon: boolean
  collapse: boolean
  uniqueOpened: boolean
  hamburger: boolean
  screenfull: boolean
  search: boolean
  size: boolean
  locale: boolean
  tagsView: boolean
  tagsViewImmerse: boolean
  tagsViewIcon: boolean
  logo: boolean
  fixedHeader: boolean
  greyMode: boolean
  pageLoading: boolean
  layout: LayoutType
  title: string
  userInfo: string
  isDark: boolean
  currentSize: ElementPlusSize
  sizeMap: ElementPlusSize[]
  mobile: boolean
  footer: boolean
  theme: ThemeTypes
  fixedMenu: boolean
}

export const useAppStore = defineStore('app', {
  state: (): AppState => {
    return {
      userInfo: 'userInfo', // 鐧诲綍淇℃伅瀛樺偍瀛楁-寤鸿姣忎釜椤圭洰鎹竴涓瓧娈碉紝閬垮厤涓庡叾浠栭」鐩啿绐?
      sizeMap: ['default', 'large', 'small'],
      mobile: false, // 鏄惁鏄Щ鍔ㄧ
      title: import.meta.env.VITE_APP_TITLE, // 鏍囬
      pageLoading: false, // 璺敱璺宠浆loading

      breadcrumb: true, // 闈㈠寘灞?
      breadcrumbIcon: true, // 闈㈠寘灞戝浘鏍?
      collapse: false, // 鎶樺彔鑿滃崟
      uniqueOpened: true, // 鏄惁鍙繚鎸佷竴涓瓙鑿滃崟鐨勫睍寮€
      hamburger: true, // 鎶樺彔鍥炬爣
      screenfull: true, // 鍏ㄥ睆鍥炬爣
      search: true, // 鎼滅储鍥炬爣
      size: true, // 灏哄鍥炬爣
      locale: true, // 澶氳瑷€鍥炬爣
      tagsView: true, // 鏍囩椤?
      tagsViewImmerse: false, // 鏍囩椤垫矇娴?
      tagsViewIcon: true, // 鏄惁鏄剧ず鏍囩鍥炬爣
      logo: true, // logo
      fixedHeader: true, // 鍥哄畾toolheader
      footer: true, // 鏄剧ず椤佃剼
      greyMode: false, // 鏄惁寮€濮嬬伆鑹叉ā寮忥紝鐢ㄤ簬鐗规畩鎮煎康鏃?
      fixedMenu: wsCache.get('fixedMenu') || false, // 鏄惁鍥哄畾鑿滃崟

      layout: wsCache.get(CACHE_KEY.LAYOUT) || 'classic', // layout甯冨眬
      isDark: wsCache.get(CACHE_KEY.IS_DARK) || false, // 鏄惁鏄殫榛戞ā寮?
      currentSize: wsCache.get('default') || 'default', // 缁勪欢灏哄
      theme: wsCache.get(CACHE_KEY.THEME) || {
        // 涓婚鑹?
        elColorPrimary: '#409eff',
        // 宸︿晶鑿滃崟杈规棰滆壊
        leftMenuBorderColor: 'inherit',
        // 宸︿晶鑿滃崟鑳屾櫙棰滆壊
        leftMenuBgColor: '#001529',
        // 宸︿晶鑿滃崟娴呰壊鑳屾櫙棰滆壊
        leftMenuBgLightColor: '#0f2438',
        // 宸︿晶鑿滃崟閫変腑鑳屾櫙棰滆壊
        leftMenuBgActiveColor: 'var(--el-color-primary)',
        // 宸︿晶鑿滃崟鏀惰捣閫変腑鑳屾櫙棰滆壊
        leftMenuCollapseBgActiveColor: 'var(--el-color-primary)',
        // 宸︿晶鑿滃崟瀛椾綋棰滆壊
        leftMenuTextColor: '#bfcbd9',
        // 宸︿晶鑿滃崟閫変腑瀛椾綋棰滆壊
        leftMenuTextActiveColor: '#fff',
        // logo瀛椾綋棰滆壊
        logoTitleTextColor: '#fff',
        // logo杈规棰滆壊
        logoBorderColor: 'inherit',
        // 澶撮儴鑳屾櫙棰滆壊
        topHeaderBgColor: '#fff',
        // 澶撮儴瀛椾綋棰滆壊
        topHeaderTextColor: 'inherit',
        // 澶撮儴鎮仠棰滆壊
        topHeaderHoverColor: '#f6f6f6',
        // 澶撮儴杈规棰滆壊
        topToolBorderColor: '#eee'
      }
    }
  },
  getters: {
    getBreadcrumb(): boolean {
      return this.breadcrumb
    },
    getBreadcrumbIcon(): boolean {
      return this.breadcrumbIcon
    },
    getCollapse(): boolean {
      return this.collapse
    },
    getUniqueOpened(): boolean {
      return this.uniqueOpened
    },
    getHamburger(): boolean {
      return this.hamburger
    },
    getScreenfull(): boolean {
      return this.screenfull
    },
    getSize(): boolean {
      return this.size
    },
    getLocale(): boolean {
      return this.locale
    },
    getTagsView(): boolean {
      return this.tagsView
    },
    getTagsViewImmerse(): boolean {
      return this.tagsViewImmerse
    },
    getTagsViewIcon(): boolean {
      return this.tagsViewIcon
    },
    getLogo(): boolean {
      return this.logo
    },
    getFixedHeader(): boolean {
      return this.fixedHeader
    },
    getGreyMode(): boolean {
      return this.greyMode
    },
    getFixedMenu(): boolean {
      return this.fixedMenu
    },
    getPageLoading(): boolean {
      return this.pageLoading
    },
    getLayout(): LayoutType {
      return this.layout
    },
    getTitle(): string {
      return this.title
    },
    getUserInfo(): string {
      return this.userInfo
    },
    getIsDark(): boolean {
      return this.isDark
    },
    getCurrentSize(): ElementPlusSize {
      return this.currentSize
    },
    getSizeMap(): ElementPlusSize[] {
      return this.sizeMap
    },
    getMobile(): boolean {
      return this.mobile
    },
    getTheme(): ThemeTypes {
      return this.theme
    },
    getFooter(): boolean {
      return this.footer
    }
  },
  actions: {
    setPrimaryLight() {
      if (this.theme.elColorPrimary) {
        const elColorPrimary = this.theme.elColorPrimary
        const color = this.isDark ? '#000000' : '#ffffff'
        const lightList = [3, 5, 7, 8, 9]
        lightList.forEach((v) => {
          setCssVar(`--el-color-primary-light-${v}`, mix(color, elColorPrimary, v / 10))
        })
        setCssVar(`--el-color-primary-dark-2`, mix(color, elColorPrimary, 0.2))

        this.setAllColorRgbVars()
      }
    },

    // 澶勭悊element鑷甫鐨勪富棰樿壊鍜岃緟鍔╄壊鐨?rgb鍒囨崲涓婚鍙樺寲锛屽锛?-el-color-primary-rgb
    setAllColorRgbVars() {
      // 闇€瑕佸鐞嗙殑棰滆壊绫诲瀷鍒楄〃
      const colorTypes = ['primary', 'success', 'warning', 'danger', 'error', 'info']

      colorTypes.forEach((type) => {
        // 鑾峰彇褰撳墠棰滆壊鍊?
        const colorValue = getCssColorVariable(`--el-color-${type}`)
        if (colorValue) {
          // 杞崲涓簉gba骞舵彁鍙朢GB閮ㄥ垎
          const rgbaString = hexToRGB(colorValue, 1)
          const rgbValues = rgbaString.match(/rgba?\((\d+),\s*(\d+),\s*(\d+)/i)
          if (rgbValues) {
            const [, r, g, b] = rgbValues
            // 璁剧疆瀵瑰簲鐨凴GB鍙橀噺
            setCssVar(`--el-color-${type}-rgb`, `${r}, ${g}, ${b}`)
          }
        }
      })
    },
    setBreadcrumb(breadcrumb: boolean) {
      this.breadcrumb = breadcrumb
    },
    setBreadcrumbIcon(breadcrumbIcon: boolean) {
      this.breadcrumbIcon = breadcrumbIcon
    },
    setCollapse(collapse: boolean) {
      this.collapse = collapse
    },
    setUniqueOpened(uniqueOpened: boolean) {
      this.uniqueOpened = uniqueOpened
    },
    setHamburger(hamburger: boolean) {
      this.hamburger = hamburger
    },
    setScreenfull(screenfull: boolean) {
      this.screenfull = screenfull
    },
    setSize(size: boolean) {
      this.size = size
    },
    setLocale(locale: boolean) {
      this.locale = locale
    },
    setTagsView(tagsView: boolean) {
      this.tagsView = tagsView
    },
    setTagsViewImmerse(tagsViewImmerse: boolean) {
      this.tagsViewImmerse = tagsViewImmerse
    },
    setTagsViewIcon(tagsViewIcon: boolean) {
      this.tagsViewIcon = tagsViewIcon
    },
    setLogo(logo: boolean) {
      this.logo = logo
    },
    setFixedHeader(fixedHeader: boolean) {
      this.fixedHeader = fixedHeader
    },
    setGreyMode(greyMode: boolean) {
      this.greyMode = greyMode
    },
    setFixedMenu(fixedMenu: boolean) {
      wsCache.set('fixedMenu', fixedMenu)
      this.fixedMenu = fixedMenu
    },
    setPageLoading(pageLoading: boolean) {
      this.pageLoading = pageLoading
    },
    setLayout(layout: LayoutType) {
      if (this.mobile && layout !== 'classic') {
        ElMessage.warning('绉诲姩绔ā寮忎笅涓嶆敮鎸佸垏鎹㈠叾浠栧竷灞€')
        return
      }
      this.layout = layout
      wsCache.set(CACHE_KEY.LAYOUT, this.layout)
    },
    setTitle(title: string) {
      this.title = title
    },
    setIsDark(isDark: boolean) {
      this.isDark = isDark
      if (this.isDark) {
        document.documentElement.classList.add('dark')
        document.documentElement.classList.remove('light')
      } else {
        document.documentElement.classList.add('light')
        document.documentElement.classList.remove('dark')
      }
      wsCache.set(CACHE_KEY.IS_DARK, this.isDark)
      this.setPrimaryLight()
    },
    setCurrentSize(currentSize: ElementPlusSize) {
      this.currentSize = currentSize
      wsCache.set('currentSize', this.currentSize)
    },
    setMobile(mobile: boolean) {
      this.mobile = mobile
    },
    setTheme(theme: ThemeTypes) {
      this.theme = Object.assign(this.theme, theme)
      wsCache.set(CACHE_KEY.THEME, this.theme)
    },
    setCssVarTheme() {
      for (const key in this.theme) {
        setCssVar(`--${humpToUnderline(key)}`, this.theme[key])
      }
      this.setPrimaryLight()
    },
    setFooter(footer: boolean) {
      this.footer = footer
    }
  },
  persist: false
})

export const useAppStoreWithOut = () => {
  return useAppStore(store)
}
