import request from '@/config/axios'

export interface DiaryCategoryVO {
  id: number
  name: string
  sort?: number
  status?: number
  totalCount?: number
  draftCount?: number
  publishedCount?: number
  createTime?: Date
  updateTime?: Date
}

export const getDiaryCategoryList = () => {
  return request.get({ url: '/diary/category/list' })
}

export const getDiaryCategory = (id: number) => {
  return request.get({ url: '/diary/category/get?id=' + id })
}

export const createDiaryCategory = (data: DiaryCategoryVO) => {
  return request.post({ url: '/diary/category/create', data })
}

export const updateDiaryCategory = (data: DiaryCategoryVO) => {
  return request.put({ url: '/diary/category/update', data })
}

export const deleteDiaryCategory = (id: number) => {
  return request.delete({ url: '/diary/category/delete?id=' + id })
}
