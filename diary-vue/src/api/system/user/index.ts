import request from '@/config/axios'

export interface UserVO {
  id: number
  username: string
  nickname: string
  email: string
  mobile: string
  sex: number
  avatar: string
  loginIp: string
  status: number
  remark: string
  loginDate: Date
  createTime: Date
}

export const getUserPage = (params: PageParam) => {
  return request.get({ url: '/system/user/page', params })
}

export const getUser = (id: number) => {
  return request.get({ url: '/system/user/get?id=' + id })
}

export const createUser = (data: UserVO) => {
  return request.post({ url: '/system/user/create', data })
}

export const updateUser = (data: UserVO) => {
  return request.put({ url: '/system/user/update', data })
}

export const deleteUser = (id: number) => {
  return request.delete({ url: '/system/user/delete?id=' + id })
}

export const deleteUserList = (ids: number[]) => {
  return request.delete({ url: '/system/user/delete-list', params: { ids: ids.join(',') } })
}

export const resetUserPassword = (id: number, password: string) => {
  return request.put({ url: '/system/user/update-password', data: { id, password } })
}

export const updateUserStatus = (id: number, status: number) => {
  return request.put({ url: '/system/user/update-status', data: { id, status } })
}

export const getSimpleUserList = (): Promise<UserVO[]> => {
  return request.get({ url: '/system/user/simple-list' })
}
