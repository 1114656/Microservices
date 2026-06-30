import request from '@/config/axios'

export interface ProfileVO {
  id: number
  username: string
  nickname: string
  roles: {
    id: number
    name: string
  }[]
  email: string
  mobile: string
  sex: number
  avatar: string
  status: number
  remark: string
  loginIp: string
  loginDate: Date
  createTime: Date
}

export interface UserProfileUpdateReqVO {
  nickname?: string
  email?: string
  mobile?: string
  sex?: number
}

export const getUserProfile = () => {
  return request.get({ url: '/system/user/profile/get' })
}

export const updateUserProfile = (data: UserProfileUpdateReqVO) => {
  return request.put({ url: '/system/user/profile/update', data })
}

export const updateUserPassword = (oldPassword: string, newPassword: string) => {
  return request.put({
    url: '/system/user/profile/update-password',
    data: {
      oldPassword,
      newPassword
    }
  })
}
