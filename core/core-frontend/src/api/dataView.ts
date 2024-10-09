import request from "@/config/axios";

export const authorizedResourceIdsWithShare = () => request.get({ url: `/dataVisualization/share` })

export const authorizedResourceIdsWithManage = () => request.get({ url: `/dataVisualization/manage` })

export const authorizedResourceIdsWithExport = () => request.get({ url: `/dataVisualization/export` })
