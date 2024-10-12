import { defineStore } from 'pinia'
import { store } from '../index'
import { useCache } from '@/hooks/web/useCache'
import { useLocaleStoreWithOut } from './locale'
const { wsCache } = useCache()
const locale = useLocaleStoreWithOut()
interface UserState {
  token: string
  uid: number
  name: string
  nickname: string
  oid: string
  language: string
  exp: number
  perms: string[]
}

export const userStore = defineStore('user', {
  state: (): UserState => {
    return {
      token: null,
      uid: null,
      name: null,
      nickname: null,
      oid: null,
      language: 'zh-CN',
      exp: null,
      perms: []
    }
  },
  getters: {
    getToken(): string {
      return this.token
    },
    getUid(): number {
      return this.uid
    },
    getName(): string {
      return this.name
    },
    getNickname(): string {
      return this.nickname
    },
    getOid(): string {
      return this.oid
    },
    getLanguage(): string {
      return this.language
    },
    getExp(): number {
      return this.exp
    },
    getPerms(): string[] {
      return this.perms
    }
  },
  actions: {
    async setUser() {
      const desktop = wsCache.get('app.desktop')
      let res = null
      if (desktop) {
        res = { data: { uid: '1', name: 'DataEase 用户', oid: '1', language: 'zh-CN' } }
      } else {
        const user = await import('@/api/user')
        res = await user.userInfo()
      }
      const data = res.data
      data.token = wsCache.get('user.token')
      data.exp = wsCache.get('user.exp')
      const keys: string[] = ['token', 'uid', 'name', 'nickname','oid', 'language', 'exp']

      keys.forEach(key => {
        const dkey = key === 'uid' ? 'id' : key
        this[key] = data[dkey]
        wsCache.set('user.' + key, this[key])
      })
      this.setLanguage(this.language)
    },
    setToken(token: string) {
      wsCache.set('user.token', token)
      this.token = token
    },
    setExp(exp: number) {
      wsCache.set('user.exp', exp)
      this.exp = exp
    },
    setUid(uid: number) {
      wsCache.set('user.uid', uid)
      this.uid = uid
    },
    setName(name: string) {
      wsCache.set('user.name', name)
      this.name = name
    },
    setNickname(nickname: string) {
      wsCache.set('user.nickname', nickname)
      this.nickname = nickname
    },
    setIsAdmin(isAdmin: boolean) {
      wsCache.set('user.isAdmin', isAdmin)
      this.isAdmin = isAdmin
    },
    setOid(oid: string) {
      wsCache.set('user.oid', oid)
      this.oid = oid
    },
    setLanguage(language: string) {
      if (!language || language === 'zh_CN') {
        language = 'zh-CN'
      }
      wsCache.set('user.language', language)
      this.language = language
      locale.setLang(language)
    },
    setPerms(perms: string[]) {
      wsCache.set('user.perms', perms)
      this.perms = perms
    },
    clear() {
      const keys: string[] = ['token', 'uid', 'name',  'nickname','oid', 'language', 'exp', 'perms']
      keys.forEach(key => wsCache.delete('user.' + key))
    }
  }
})

export const useUserStoreWithOut = () => {
  return userStore(store)
}
