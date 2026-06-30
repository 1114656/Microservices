import request from '@/config/axios'

export interface BlogArticleVO {
  id?: number
  title: string
  contentMarkdown: string
  summary?: string
  coverFileId?: number
  status: number
  visibility: number
  createTime?: Date
  updateTime?: Date
}

export interface BlogArticlePageReqVO extends PageParam {
  title?: string
  status?: number
  visibility?: number
}

export const getBlogArticlePage = (params: BlogArticlePageReqVO) => {
  return request.get({ url: '/admin-api/blog/page', params })
}

export const getBlogArticle = (id: number) => {
  return request.get({ url: '/admin-api/blog/get?id=' + id })
}

export const createBlogArticle = (data: BlogArticleVO) => {
  return request.post({ url: '/admin-api/blog/create', data })
}

export const updateBlogArticle = (data: BlogArticleVO) => {
  return request.put({ url: '/admin-api/blog/update', data })
}

export const deleteBlogArticle = (id: number) => {
  return request.delete({ url: '/admin-api/blog/delete?id=' + id })
}
