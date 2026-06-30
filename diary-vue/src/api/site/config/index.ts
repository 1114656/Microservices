import request from '@/config/axios'

export type HomePageType = 'diary' | 'blog'

export const DEFAULT_HOME_PAGE_TYPE = 'diary' as const

export interface SiteConfigVO {
  homePageType: HomePageType
}

export const getPublicSiteConfig = () => {
  return request.get<SiteConfigVO>({ url: '/site/config/public', headers: { isToken: false } })
}

export const getSiteConfig = () => {
  return request.get<SiteConfigVO>({ url: '/site/config/get' })
}

export const updateSiteConfig = (data: SiteConfigVO) => {
  return request.put({ url: '/site/config/update', data })
}
