import { defineStore } from 'pinia'
import { store } from '@/store/index'

interface GnState {
  gn: string
}

export const gnStore = defineStore('gnStore', {
  persist: true,
  state: (): GnState => {
    return {
      gn: ''
    }
  },
  getters: {
    getGn(): string {
      return this.gn
    }
  },
  actions: {
    setGn(gn: string) {
      this.gn = gn
    }
  }
})

export const useGnStoreWithOut = () => {
  return gnStore(store)
}
