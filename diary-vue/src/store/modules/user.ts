import { store } from '@/store'
import { defineStore } from 'pinia'
import { getAccessToken, removeToken } from '@/utils/auth'
import { CACHE_KEY, useCache, deleteUserCache } from '@/hooks/web/useCache'
import { getInfo, loginOut } from '@/api/login'
import { hasUsablePermissionCache, USER_PERMISSION_CACHE_SECONDS } from './userCachePolicy'

const { wsCache } = useCache()
let refreshUserInfoPromise: Promise<void> | null = null

interface UserVO {
  id: number
  avatar: string
  nickname: string
  deptId: number
}

interface UserInfoVO {
  permissions: Set<string>
  roles: string[]
  isSetUser: boolean
  user: UserVO
}

export const useUserStore = defineStore('admin-user', {
  state: (): UserInfoVO => ({
    permissions: new Set<string>(),
    roles: [],
    isSetUser: false,
    user: {
      id: 0,
      avatar: '',
      nickname: '',
      deptId: 0
    }
  }),
  getters: {
    getPermissions(): Set<string> {
      return this.permissions
    },
    getRoles(): string[] {
      return this.roles
    },
    getIsSetUser(): boolean {
      return this.isSetUser
    },
    getUser(): UserVO {
      return this.user
    }
  },
  actions: {
    setUserInfo(userInfo: any) {
      this.permissions = new Set(userInfo.permissions || [])
      this.roles = userInfo.roles || []
      this.user = userInfo.user
      this.isSetUser = true
      wsCache.set(CACHE_KEY.USER, userInfo, { exp: USER_PERMISSION_CACHE_SECONDS })
      wsCache.set(CACHE_KEY.ROLE_ROUTERS, userInfo.menus || [], {
        exp: USER_PERMISSION_CACHE_SECONDS
      })
    },
    refreshUserInfoSilently() {
      if (refreshUserInfoPromise) {
        return refreshUserInfoPromise
      }
      refreshUserInfoPromise = getInfo()
        .then((userInfo) => {
          if (userInfo) {
            this.setUserInfo(userInfo)
          }
        })
        .catch(() => {
          // Backend APIs still validate the token; keep cached menus usable during transient failures.
        })
        .finally(() => {
          refreshUserInfoPromise = null
        })
      return refreshUserInfoPromise
    },
    async setUserInfoAction() {
      if (!getAccessToken()) {
        this.resetState()
        return null
      }

      let userInfo = wsCache.get(CACHE_KEY.USER)
      const roleRouters = wsCache.get(CACHE_KEY.ROLE_ROUTERS)
      if (hasUsablePermissionCache(userInfo, roleRouters)) {
        this.setUserInfo(userInfo)
        this.refreshUserInfoSilently()
        return userInfo
      }

      userInfo = await getInfo()
      if (!userInfo) {
        removeToken()
        deleteUserCache()
        this.resetState()
        return null
      }
      this.setUserInfo(userInfo)
      return userInfo
    },
    async setUserAvatarAction(avatar: string) {
      const userInfo = wsCache.get(CACHE_KEY.USER)
      this.user.avatar = avatar
      userInfo.user.avatar = avatar
      wsCache.set(CACHE_KEY.USER, userInfo, { exp: USER_PERMISSION_CACHE_SECONDS })
    },
    async setUserNicknameAction(nickname: string) {
      const userInfo = wsCache.get(CACHE_KEY.USER)
      this.user.nickname = nickname
      userInfo.user.nickname = nickname
      wsCache.set(CACHE_KEY.USER, userInfo, { exp: USER_PERMISSION_CACHE_SECONDS })
    },
    async loginOut() {
      await loginOut()
      removeToken()
      deleteUserCache()
      this.resetState()
    },
    resetState() {
      this.permissions = new Set<string>()
      this.roles = []
      this.isSetUser = false
      this.user = {
        id: 0,
        avatar: '',
        nickname: '',
        deptId: 0
      }
    }
  }
})

export const useUserStoreWithOut = () => {
  return useUserStore(store)
}
