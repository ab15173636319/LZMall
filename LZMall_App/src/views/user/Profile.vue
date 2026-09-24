<script setup lang="ts">
import { computed, ref } from 'vue'
import Navecation from '@/layout/Navecation.vue'
import { useUser } from '@/store'
import { resolveAvatar } from '@/utils/avatar'

/** 侧边菜单，除「个人资料」外均为占位，具体功能待定 */
interface ProfileMenu {
    key: string
    label: string
    icon: string
}

const menus: ProfileMenu[] = [
    { key: 'info', label: '个人资料', icon: 'id-card' },
    { key: 'security', label: '账号安全', icon: 'shield-halved' },
    { key: 'order', label: '我的订单', icon: 'clipboard-list' },
    { key: 'address', label: '收货地址', icon: 'location-dot' },
    { key: 'notice', label: '消息通知', icon: 'bell' },
]

const activeKey = ref(menus[0].key)
const activeMenu = computed(() => menus.find(menu => menu.key === activeKey.value))

const user = useUser()
const userInfo = computed(() => user.userInfo)

const avatar = computed(() => resolveAvatar(userInfo.value))
const displayName = computed(() => userInfo.value?.nickname || userInfo.value?.username || '未登录用户')
const displayAccount = computed(() => userInfo.value?.username ? `@${userInfo.value.username}` : '')
const joinDate = computed(() => userInfo.value?.createTime?.slice(0, 10) || '--')

const infoItems = computed(() => [
    { label: '用户名', value: userInfo.value?.username || '--', tip: '注册后不可修改' },
    { label: '昵称', value: userInfo.value?.nickname || '未设置', tip: '' },
    { label: '邮箱', value: userInfo.value?.email || '未绑定', tip: '' },
    { label: '手机号', value: userInfo.value?.phone || '未绑定', tip: '' },
    { label: '注册时间', value: joinDate.value, tip: '' },
])
</script>

<template>
    <navecation />
    <div class="mx-auto w-full max-w-5xl px-4 py-8">
        <h1 class="text-xl font-bold text-gray-800">用户中心</h1>
        <p class="mt-1 text-sm text-gray-400">管理你的账号资料与商城相关设置</p>

        <div class="mt-6 grid gap-6 lg:grid-cols-[260px_1fr]">
            <!-- 左：用户卡片 + 侧边菜单 -->
            <div class="flex flex-col gap-4">
                <div class="rounded-card bg-white p-5 text-center shadow-sm ring-1 ring-black/5">
                    <el-avatar :size="72" :src="avatar" class="ring-4 ring-brand/10" />
                    <p class="mt-3 truncate font-medium text-gray-800">{{ displayName }}</p>
                    <p v-if="displayAccount" class="mt-1 truncate text-xs text-gray-400">{{ displayAccount }}</p>
                    <p class="mt-4 text-xs text-gray-400">{{ joinDate }} 加入 LZMall</p>
                </div>

                <nav class="rounded-card bg-white p-2 shadow-sm ring-1 ring-black/5">
                    <button v-for="menu in menus" :key="menu.key" type="button"
                        class="flex w-full cursor-pointer items-center gap-3 rounded-lg px-3 py-2.5 text-sm transition"
                        :class="activeKey === menu.key
                            ? 'bg-brand/10 font-medium text-brand'
                            : 'text-gray-600 hover:bg-gray-50'" @click="activeKey = menu.key">
                        <font-awesome-icon :icon="menu.icon" class="w-4 text-center" />
                        <span>{{ menu.label }}</span>
                    </button>
                </nav>
            </div>

            <!-- 右：内容区 -->
            <section class="rounded-card bg-white p-6 shadow-sm ring-1 ring-black/5">
                <template v-if="activeKey === 'info'">
                    <header class="flex items-start justify-between gap-4">
                        <div>
                            <h2 class="font-semibold text-gray-800">个人资料</h2>
                            <p class="mt-1 text-xs text-gray-400">资料编辑、头像上传功能开发中</p>
                        </div>
                        <el-button type="primary" plain disabled>编辑资料</el-button>
                    </header>

                    <dl class="mt-4 divide-y divide-gray-100">
                        <div v-for="item in infoItems" :key="item.label"
                            class="flex items-center justify-between gap-4 py-3.5">
                            <dt class="shrink-0 text-sm text-gray-500">{{ item.label }}</dt>
                            <dd class="flex min-w-0 items-center gap-2 text-sm text-gray-800">
                                <span class="truncate">{{ item.value }}</span>
                                <span v-if="item.tip"
                                    class="shrink-0 rounded bg-gray-100 px-1.5 py-0.5 text-[11px] text-gray-400">
                                    {{ item.tip }}
                                </span>
                            </dd>
                        </div>
                    </dl>
                </template>

                <div v-else class="flex flex-col items-center justify-center gap-3 py-20 text-center">
                    <font-awesome-icon icon="screwdriver-wrench" class="text-3xl text-gray-300" />
                    <p class="text-sm text-gray-500">「{{ activeMenu?.label }}」功能开发中</p>
                    <p class="text-xs text-gray-400">敬请期待</p>
                </div>
            </section>
        </div>
    </div>
</template>
