import request from '@/config/axios'

export const getActiveProjects = () => request.get({ url: '/project/activeProjects' })
