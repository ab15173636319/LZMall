import { getInfo } from '@/api/userAuth'
import type { UserInfo } from '@/types/user'
import { defineStore } from 'pinia'
import { computed, ref, shallowRef } from 'vue'

export const useUser = defineStore('user', () => {
    const accessToken = shallowRef('')
    const userInfo = ref<UserInfo | null>(null)
    const isLogin = computed(() => accessToken.value !== '')

    const hasRole = (role: string[]) => {
        return userInfo.value?.role.includes(role.join(','))
    }

    const getUserInfo = async () => {
        // 如果已经有用户信息，则不再请求
        if (userInfo.value) return
        const res = await getInfo()
        console.log(res);

        if (res.code !== 200) {
            return
        }
        userInfo.value = res.data
    }

    /** 退出登录：后端暂无登出接口，仅清空本地登录态 */
    const logout = () => {
        accessToken.value = ''
        userInfo.value = null
    }

    return { userInfo, isLogin, hasRole, accessToken, getUserInfo, logout }
}, {
    persist: {
        pick: ["accessToken"]
    }
})