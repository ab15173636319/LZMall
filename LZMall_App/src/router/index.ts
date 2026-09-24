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
            path: '/profile',
            name: "profile",
            meta: {
                title: "用户中心 - LZMall",
                auth: true,
                icon: ICONS.INDEX,
                showInMenu: true
            },
            component: () => import("@/views/user/Profile.vue")
        },
        {
            path: '/net-error',
            meta: {
                title: "网络异常",
                auth: false,
                icon: ICONS.ERROR
            },
            component: () => import("@/views/error/NetworkError.vue")
        },
        {
            path: '/:pathMatch(.*)*',
            meta: {
                title: "404 - 页面不存在",
                auth: false,
                icon: ICONS.ERROR
            },
            component: () => import("@/views/error/NotFound.vue")
        },
    ]
})

router.beforeEach(async (to, _, next) => {
    const userStore = useUser()
    // 设置标题
    document.title = to.meta.title || "LZMall"
    //设置图标
    document.querySelector("link[rel='icon']")?.setAttribute("href", to.meta.icon?.path || "")
    // meta.auth 表示「该页面需要登录」：未登录时带上来源地址跳转登录页，登录后可原路返回
    if (to.meta.auth && !userStore.isLogin) next({ name: "auth", query: { redirect: to.fullPath } })
    else if (to.meta.role && !userStore.hasRole(to.meta.role)) next({ name: "auth" }) // 是否有权限
    else next()
})

export default router