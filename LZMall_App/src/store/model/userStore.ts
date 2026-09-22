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
        const res = await getInfo()
        console.log(res);
        
        if (res.code !== 200) {
            return
        }
        userInfo.value = res.data
    }


    return { userInfo, isLogin, hasRole, accessToken, getUserInfo }
}, {
    persist: {
        pick: ["accessToken"]
    }
})