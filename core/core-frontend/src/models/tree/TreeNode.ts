export interface BusiTreeNode {
  id: string | number
  pid: string | number
  name: string
  leaf?: boolean
  weight: number
  extraFlag: number
  children?: BusiTreeNode[]
  isManage?: boolean
  isShare?: boolean
  isExport?: boolean
}

export interface BusiTreeRequest {
  busiFlag?: string
  leaf?: boolean
  weight?: number
  sortType?: string
}
