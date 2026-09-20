import type { UserInfo } from '@/types/user'
import { defineStore } from 'pinia'
import { computed, ref, shallowRef } from 'vue'

export const useUser = defineStore('user', () => {
    const accessToken = shallowRef('')
    const refreshToken = shallowRef('')
    const userInfo = ref<UserInfo | null>(null)
    const isLogin = computed(() => accessToken.value !== '')

    const hasRole = (role: string[]) => {
        return userInfo.value?.role.includes(role.join(','))
    }


    return { userInfo, isLogin, hasRole, accessToken, refreshToken }
})