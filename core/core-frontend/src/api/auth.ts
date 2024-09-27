import request from '@/config/axios'

export const getDashboardsByUserId = (userId: string) => {
  return request.get({
    url: '/auth/getDashboardsByUserId',
    params: { userId }
  });
};

export const getDataViewByUserId = (userId: string) => {
  return request.get({
    url: '/auth/getDataViewByUserId',
    params: { userId }
  });
};

export const getDataSourceByUserId = (userId: string) => {
  return request.get({
    url: '/auth/getDataSourceByUserId',
    params: { userId }
  });
};

export const getDataSetByUserId = (userId: string) => {
  return request.get({
    url: '/auth/getDataSetByUserId',
    params: { userId }
  });
};

export const getDashboardsByRoleId = (roleId: string) => {
  return request.get({
    url: '/auth/getDashboardsByRoleId',
    params: { roleId }
  });
};

export const getDataViewByRoleId = (roleId: string) => {
  return request.get({
    url: '/auth/getDataViewByRoleId',
    params: { roleId }
  });
};

export const getDataSourceByRoleId = (roleId: string) => {
  return request.get({
    url: '/auth/getDataSourceByRoleId',
    params: { roleId }
  });
};

export const getDataSetByRoleId = (roleId: string) => {
  return request.get({
    url: '/auth/getDataSetByRoleId',
    params: { roleId }
  });
};

export const queryUserApi = data => request.post({ url: '/user/byCurOrg', data })
export const queryUserOptionsApi = () => request.get({ url: '/user/org/option' })
export const queryRoleApi = data => request.post({ url: '/role/byCurOrg', data })

export const resourceTreeApi = (flag: string) => request.get({ url: '/auth/busiResource/' + flag })

export const menuTreeApi = () => request.get({ url: '/auth/menuResource' })

export const resourcePerApi = data => request.post({ url: '/auth/busiPermission', data })

export const menuPerApi = data => request.post({ url: '/auth/menuPermission', data })

export const busiPerSaveApi = data => request.post({ url: '/auth/saveBusiPer', data })
export const menuPerSaveApi = data => request.post({ url: '/auth/saveMenuPer', data })

export const resourceTargetPerApi = data =>
  request.post({ url: '/auth/busiTargetPermission', data })

export const menuTargetPerApi = data => request.post({ url: '/auth/menuTargetPermission', data })

export const busiTargetPerSaveApi = data => request.post({ url: '/auth/saveBusiTargetPer', data })
export const menuTargetPerSaveApi = data => request.post({ url: '/auth/saveMenuTargetPer', data })
