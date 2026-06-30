import request from '@/config/axios'

export interface PermissionAssignUserRoleReqVO {
  userId: number
  roleIds: number[]
}

export interface PermissionAssignRoleMenuReqVO {
  roleId: number
  menuIds: number[]
}

export const getRoleMenuList = async (roleId: number) => {
  return await request.get({ url: '/admin-api/system/permission/list-role-menus?roleId=' + roleId })
}

export const assignRoleMenu = async (data: PermissionAssignRoleMenuReqVO) => {
  return await request.post({ url: '/admin-api/system/permission/assign-role-menu', data })
}

export const getUserRoleList = async (userId: number) => {
  return await request.get({ url: '/admin-api/system/permission/list-user-roles?userId=' + userId })
}

export const assignUserRole = async (data: PermissionAssignUserRoleReqVO) => {
  return await request.post({ url: '/admin-api/system/permission/assign-user-role', data })
}
