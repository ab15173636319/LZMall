import { createApp } from 'vue'
import App from './App.vue'
import "@/assets/style/common.css"

import { createPinia } from 'pinia'

import router from "@/router"

import ElementPlus from "element-plus";
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { library } from "@fortawesome/fontawesome-svg-core"
import { fas } from "@fortawesome/free-solid-svg-icons"
import { far } from "@fortawesome/free-regular-svg-icons"
import { fab } from "@fortawesome/free-brands-svg-icons"

// 一次性注册全部图标（solid / regular / brands），模板可直接用字符串名，如 icon="user"
library.add(fas, far, fab)

const pinia = createPinia()
const app = createApp(App)

app.component("font-awesome-icon", FontAwesomeIcon)

app.use(router)
app.use(pinia)
app.use(ElementPlus, {
    locale: zhCn,
})

app.mount('#app')
