import { createRouter, createWebHistory } from "vue-router"
import { authRouter } from "./modules/authRouter"
import { useUser } from "@/store"
import ICONS from "@/enum/icons"

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: "index",
            meta: {
                title: "LZMall - 首页",
                auth: false,
                icon: ICONS.INDEX
            },
            component: () => import("@/views/HomeView.vue")
        },
        ...authRouter,
        {
            path: '/:pathMatch(.*)*',
            meta: {
                title: "404 - 页面不存在",
                auth: false,
                icon: ICONS.ERROR
            },
            component: () => import("@/views/error/NotFound.vue")
        }
    ]
})

router.beforeEach((to, from, next) => {
    const userStore = useUser()
    // 设置标题
    document.title = to.meta.title || "LZMall"
    //设置图标
    document.querySelector("link[rel='icon']")?.setAttribute("href", to.meta.icon?.path || "")
    if (to.meta.auth && userStore.isLogin) next({ name: "auth" }) // 是否登录
    else if (to.meta.role && !userStore.hasRole(to.meta.role)) next({ name: "auth" }) // 是否有权限
    else next()
})

export default router