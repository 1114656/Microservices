import request from '@/config/axios'

export interface RoleVO {
  id: number
  name: string
  code: string
  sort: number
  status: number
  type: number
  remark?: string
  createTime: Date
}

export const getRolePage = async (params: PageParam) => {
  return await request.get({ url: '/system/role/page', params })
}

export const getSimpleRoleList = async (): Promise<RoleVO[]> => {
  return await request.get({ url: '/system/role/simple-list' })
}

export const getRole = async (id: number) => {
  return await request.get({ url: '/system/role/get?id=' + id })
}

export const createRole = async (data: RoleVO) => {
  return await request.post({ url: '/system/role/create', data })
}

export const updateRole = async (data: RoleVO) => {
  return await request.put({ url: '/system/role/update', data })
}

export const deleteRole = async (id: number) => {
  return await request.delete({ url: '/system/role/delete?id=' + id })
}

export const deleteRoleList = async (ids: number[]) => {
  return await request.delete({ url: '/system/role/delete-list', params: { ids: ids.join(',') } })
}
