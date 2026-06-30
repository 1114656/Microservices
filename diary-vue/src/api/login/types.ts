export type UserLoginVO = {
  username: string
  password: string
  rememberMe?: boolean
}

export type TokenType = {
  id: number
  accessToken: string
  refreshToken: string
  userId: number
  userType: number
  clientId: string
  expiresTime: number
}

export type UserVO = {
  id: number
  username: string
  nickname: string
  email: string
  mobile: string
  sex: number
  avatar: string
  loginIp: string
  loginDate: string
}

export type RegisterVO = {
  username: string
  password: string
}
