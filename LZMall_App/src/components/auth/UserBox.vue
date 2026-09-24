<script setup lang="ts">
import { ElAvatar, ElMessageBox } from 'element-plus';
import { computed, onUnmounted, shallowRef } from 'vue';
import type { UserInfo } from '@/types/user';
import { useUser } from '@/store';
import { resolveAvatar } from '@/utils/avatar';
import router from '@/router';

/** 头像常态尺寸，同时是外层占位盒的尺寸 */
const AVATAR_BASE_SIZE = 50
/** 移出后延迟收起时间，给鼠标移到卡片上留出余量 */
const HOVER_LEAVE_DELAY = 500

const userInfo = defineModel<UserInfo | null>()
const userStore = useUser()

const isHover = shallowRef(false)
let timer: ReturnType<typeof setTimeout> | undefined

const clearTimer = () => {
    if (timer) {
        clearTimeout(timer)
        timer = undefined
    }
}

const hoverToggle = (active: boolean) => {
    if (active) {
        // 快速移出再移入时，取消上一次待收起的定时器
        clearTimer()
        isHover.value = true
        return
    }
    clearTimer()
    timer = setTimeout(() => {
        isHover.value = false
        timer = undefined
    }, HOVER_LEAVE_DELAY)
}

const InitialImg = computed(() => resolveAvatar(userInfo.value))

/** 昵称兜底为用户名 */
const displayName = computed(() => userInfo.value?.nickname || userInfo.value?.username || "未登录用户")

const displayAccount = computed(() => userInfo.value?.username ? `@${userInfo.value.username}` : "")

const collapse = () => {
    clearTimer()
    isHover.value = false
}

const goProfile = async () => {
    collapse()
    if (router.currentRoute.value.name !== "profile") {
        await router.push("/profile")
    }
}

const handleLogout = async () => {
    try {
        await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
        })
    } catch {
        // 用户取消
        return
    }
    userStore.logout()
    collapse()
    // 非首页时回到首页，避免停留在需要登录态的页面
    if (router.currentRoute.value.name !== "index") {
        await router.push("/")
    }
}

onUnmounted(clearTimer)
</script>

<template>
    <div class="relative z-50 shrink-0" @mouseenter="hoverToggle(true)" @mouseleave="hoverToggle(false)">
        <!-- 固定 50×50 占位盒：放大只作用于 transform，盒尺寸不变，导航栏其它内容不会被挤压 -->
        <div class="size-12.5 relative  z-[51]">
            <ElAvatar class="cursor-pointer origin-top-right transition-transform duration-300"
                :class="isHover ? 'scale-[2]' : 'scale-100'" :size="AVATAR_BASE_SIZE" :src="InitialImg" />
        </div>
        <!-- 卡片从放大后头像的下沿开始，避免盖住头像；整体位于头像正下方、右对齐 -->
        <Transition name="user-card">
            <div v-show="isHover"
                class="absolute right-0 z-50 w-52 origin-top-right rounded-card bg-white p-4 shadow-lg ring-1 ring-black/5">
                <p class="truncate font-medium text-gray-800">{{ displayName }}</p>
                <p v-if="displayAccount" class="mt-1 truncate text-xs text-gray-400">{{ displayAccount }}</p>
                <div class="my-3 h-px bg-gray-100"></div>
                <div class="space-y-1">
                    <button type="button"
                        class="w-full cursor-pointer rounded-md py-2 text-sm text-gray-600 transition hover:bg-gray-50"
                        @click="goProfile">个人中心</button>
                    <button type="button"
                        class="w-full cursor-pointer rounded-md py-2 text-sm text-red-500 transition hover:bg-red-50"
                        @click="handleLogout">退出登录</button>
                </div>
            </div>
        </Transition>
    </div>
</template>

<style scoped lang="scss">
.user-card-enter-active,
.user-card-leave-active {
    transition: opacity 0.2s ease, transform 0.2s ease;
}

.user-card-enter-from,
.user-card-leave-to {
    opacity: 0;
    transform: translateY(-6px) scale(0.96);
}
</style>
