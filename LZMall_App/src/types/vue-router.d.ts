import type { IconItem, IconKey } from "@/enum/icons"
import "vue-router"

// 扩充 vue-router 的 RouteMeta 接口，为路由 meta 增加自定义字段的类型提示
declare module "vue-router" {
    interface RouteMeta {
        /** 页面标题 */
        title?: string
        /** 是否需要登录鉴权 */
        auth?: boolean
        // 此页icon
        icon?: IconItem
        // 进入/打开此页所需权限
        role?: string[]
        // 是否显示在菜单中
        showInMenu?: boolean
        // 是否显示在面包屑中
        showInBreadcrumb?: boolean
    }
}
