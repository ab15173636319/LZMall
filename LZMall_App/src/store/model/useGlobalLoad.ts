import { defineStore } from "pinia"
import { shallowRef } from "vue"

export const useGlobalLoading = defineStore("globalLoading", () => {
    const loading = shallowRef(false)
    const delay = shallowRef(300)
    let timer: number;
    const showLoading = () => {
        loading.value = true
    }
    const hideLoading = () => {
        if (timer) {
            clearTimeout(timer)
        }
        timer = setTimeout(() => {
            loading.value = false
        }, delay.value)
    }
    return {
        loading,
        showLoading,
        hideLoading
    }
})