import request from '@/config/axios'

export interface DiaryContentBlockVO {
  type: 'text' | 'file'
  content?: string
  fileId?: number
}

export interface DiaryEntryVO {
  id?: number
  title: string
  categoryId?: number
  contentBlocks: DiaryContentBlockVO[]
  summary?: string
  status: number
  visibility: number
  fileIds?: number[]
  createTime?: Date
  updateTime?: Date
}

export interface DiaryEntryPageReqVO extends PageParam {
  title?: string
  categoryId?: number
  status?: number
  visibility?: number
}

export const getDiaryPage = (params: DiaryEntryPageReqVO) => {
  return request.get({ url: '/diary/page', params })
}

export const getPublicDiaryPage = (params: DiaryEntryPageReqVO) => {
  return request.get({ url: '/diary/page', params, silentError: true })
}

export const getDiary = (id: number) => {
  return request.get({ url: '/diary/get?id=' + id })
}

export const createDiary = (data: DiaryEntryVO) => {
  return request.post({ url: '/diary/create', data })
}

export const updateDiary = (data: DiaryEntryVO) => {
  return request.put({ url: '/diary/update', data })
}

export const deleteDiary = (id: number) => {
  return request.delete({ url: '/diary/delete?id=' + id })
}
