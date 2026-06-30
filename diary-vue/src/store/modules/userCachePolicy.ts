export const USER_PERMISSION_CACHE_SECONDS = 30 * 60

export interface CachedPermissionInfo {
  permissions?: unknown
  roles?: unknown
  user?: unknown
}

export const hasUsablePermissionCache = (
  userInfo: CachedPermissionInfo | null | undefined,
  roleRouters: unknown
): boolean => {
  return Boolean(
    userInfo &&
      Array.isArray(userInfo.permissions) &&
      Array.isArray(userInfo.roles) &&
      userInfo.user &&
      Array.isArray(roleRouters)
  )
}
