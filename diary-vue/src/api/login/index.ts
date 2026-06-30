import request from '@/config/axios'
import type { RegisterVO, UserLoginVO } from './types'

export const login = (data: UserLoginVO) => {
  return request.post({
    url: '/admin-api/system/auth/login',
    data,
    headers: {
      isEncrypt: false
    }
  })
}

export const register = (data: RegisterVO) => {
  return request.post({ url: '/admin-api/system/auth/register', data })
}

export const loginOut = () => {
  return request.post({ url: '/admin-api/system/auth/logout' })
}

export const getInfo = () => {
  return request.get({ url: '/admin-api/system/auth/get-permission-info' })
}
