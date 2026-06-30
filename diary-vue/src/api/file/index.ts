import request from '@/config/axios'

export interface FileObjectVO {
  id: number
  originalName: string
  extension: string
  contentType: string
  fileCategory: string
  size: number
  sha256: string
  businessType?: string
  businessId?: string
  previewSupported: boolean
  createTime?: Date
}

export interface FileObjectPageReqVO extends PageParam {
  fileCategory?: string
  businessType?: string
  businessId?: string
}

export const uploadFile = (file: File, data: Record<string, string | number | undefined> = {}) => {
  const formData = new FormData()
  formData.append('file', file)
  Object.entries(data).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      formData.append(key, String(value))
    }
  })
  return request.upload<FileObjectVO>({ url: '/admin-api/file/upload', data: formData })
}

export const getFilePage = (params: FileObjectPageReqVO) => {
  return request.get({ url: '/admin-api/file/page', params })
}

export const getFile = (id: number) => {
  return request.get({ url: '/admin-api/file/get?id=' + id })
}

export const getFilePreviewUrl = (id: number) => {
  return request.get<{ url: string; previewUrl?: string }>({ url: '/admin-api/file/preview-url?id=' + id })
}

export const deleteFile = (id: number) => {
  return request.delete({ url: '/admin-api/file/delete?id=' + id })
}
