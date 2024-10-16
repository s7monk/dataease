import type { App } from 'vue'
import { createPinia } from 'pinia'
import piniaPersistedstate from 'pinia-plugin-persistedstate'

const store = createPinia()
store.use(piniaPersistedstate)

export const setupStore = (app: App<Element>) => {
  app.use(store)
}

export { store }
