import request from '@/config/axios'

export const mountedOrg = (keyword?: string) =>
  request.post({ url: '/org/mounted', data: { keyword } })

export const switchOrg = (id: number | string) => request.post({ url: `/user/switch/${id}` })

export const userInfo = () => request.get({ url: '/user/info' })

export const searchRoleApi = (keyword: string) =>
  request.post({ url: '/role/query', data: { keyword } })

export const userOptionForRoleApi = data => request.post({ url: '/user/role/option', data })

export const userSelectedForRoleApi = (page: number, limit: number, data) =>
  request.post({ url: `/user/role/selected/${page}/${limit}`, data })

export const userPageApi = (page: number, limit: number, data) =>
  request.post({ url: `/user/pager/${page}/${limit}`, data })

export const userCreateApi = data => request.post({ url: '/user', data })

export const userUpdateApi = data => request.put({ url: '/user', data })
export const userResetPasswordApi = data => request.post({ url: '/user/change/password', data })

export const userStatusUpdateApi = data => request.put({ url: '/user/changeStatus', data })

export const roleStatusUpdateApi = data => request.put({ url: '/role/changeStatus', data })

export const userEditApi = data => request.post({ url: '/user/edit', data })

export const personEditApi = data => request.post({ url: '/user/personEdit', data })

export const roleOptionForUserApi = data => request.post({ url: '/role/user/option', data })

export const userListApi= params => request.get({ url: '/user/list', params })

export const roleListApi= params => request.get({ url: '/role/list', params })

export const roleMenuApi= params => request.get({ url: '/menu/treeselect', params })

export const userDelApi = userId => request.delete({ url: `/user/${userId}` })

export const queryFormApi = uid => request.get({ url: `/user/queryById/${uid}` })

export const personInfoApi = () => request.get({ url: `/user/personInfo` })

export const roleCreateApi = data => request.post({ url: '/role', data })

export const roleUpdateApi = data => request.put({ url: '/role', data })

export const getPermissionByRoleId = rid => request.get({ url: `/menu/roleMenuTreeselect/${rid}` })

export const roleDelApi = rid => request.delete({ url: `/role/${rid}` })

export const beforeUnmountInfoApi = data => request.post({ url: '/role/beforeUnmountInfo', data })

export const unMountUserApi = data => request.post({ url: '/role/unMountUser', data })

export const mountUserApi = data => request.post({ url: '/role/mountUser', data })

export const searchExternalUserApi = keyword =>
  request.get({ url: '/role/searchExternalUser/' + keyword })

export const mountExternalUserApi = data => request.post({ url: '/role/mountExternalUser', data })

export const switchLangApi = data => request.post({ url: '/user/switchLanguage', data })

export const downExcelTemplateApi = () =>
  request.post({ url: '/user/excelTemplate', responseType: 'blob' })

export const importUserApi = data =>
  request.post({
    url: '/user/batchImport',
    headersType: 'multipart/form-data',
    data
  })

export const downErrorRecordApi = (key: string) =>
  request.get({ url: `/user/errorRecord/${key}`, responseType: 'blob' })

export const clearErrorApi = (key: string) => {
  request.get({ url: `/user/clearErrorRecord/${key}` })
}

export const batchDelApi = data => request.post({ url: '/user/batchDel', data })

export const defaultPwdApi = () => request.get({ url: '/user/defaultPwd' })

export const resetPwdApi = uid => request.post({ url: `/user/resetPwd/${uid}` })

export const switchEnableApi = data => request.post({ url: '/user/enable', data })
