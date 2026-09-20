import type { RouteRecordRaw } from "vue-router";

export const authRouter: RouteRecordRaw[] = [
    {
        path: "/auth",
        redirect: "/auth/login",
        children: [
            { name: "login", path: "login", component: () => import("@/views/auth/Login.vue") },
            { name: "register", path: "register", component: () => import("@/views/auth/Register.vue") }
        ]
    }
]
