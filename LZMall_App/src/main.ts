import { createApp } from 'vue'
import App from './App.vue'
import "@/assets/style/common.css"

import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import router from "@/router"

import ElementPlus from "element-plus";
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { library } from "@fortawesome/fontawesome-svg-core"
import { fas } from "@fortawesome/free-solid-svg-icons"
import { far } from "@fortawesome/free-regular-svg-icons"
import { fab } from "@fortawesome/free-brands-svg-icons"

library.add(fas, far, fab)

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)


const app = createApp(App)

app.component("font-awesome-icon", FontAwesomeIcon)

for (const [key, _component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, _component)
}

app.use(router)
app.use(pinia)
app.use(ElementPlus, {
    locale: zhCn,
})

app.mount('#app')
