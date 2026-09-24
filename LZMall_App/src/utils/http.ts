import type { Response } from "@/types/http";
import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
import { useUser } from "@/store";
import { RESULT_CODE } from "@/enum/resultCode";
import { refreshAccess } from "@/api/userAuth";
import router from "@/router";

let isRefreshing = false;

interface RefreshQueueItem {
    resolve: () => void;
    reject: (reason?: unknown) => void
}
const waitQueue: RefreshQueueItem[] = [];



const clearAuth = () => {
    const user = useUser()
    user.accessToken = ""
    user.userInfo = null
    if (router.currentRoute.value.name !== 'login') {
        router.push({ name: "login" })
    }
}



const http = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: Number(import.meta.env.VITE_API_TIMEOUT),
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
    }
})

http.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const user = useUser()
        if (user.accessToken) {
            config.headers["Authorization"] = "Bearer " + user.accessToken
        }
        return config;
    },
    (err) => {
        return Promise.reject(err);
    }
);


http.interceptors.response.use(
    (response) => {
        const res = response.data;
        if (res.code === 200) {
            ElMessage.success(res.message)
            return res;
        }
        return ElMessage.warning(res.message);
    },
    async (err: AxiosError<Response<any>>) => {
        const status = err.response?.status;
        const message = err.response?.data?.message;
        const code = err.response?.data?.code;
        const originConfig = err.config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined;

        switch (status) {
            case 401: {
                const user = useUser();
                user.userInfo = null;
                // 刷新接口自身 401（refreshToken 也废了）或已重试过 → 不再刷新
                if (!originConfig || originConfig?.url?.includes("/refreshAccess") || originConfig._retry) {
                    clearAuth()
                    ElMessage.error(message || "登录已失效，请重新登录")
                    return Promise.reject(err)
                }
                //执行刷新
                if (code === RESULT_CODE.TOKEN_EXPIRED) {
                    if (!isRefreshing) {
                        return new Promise((resolve, reject) => {
                            waitQueue.push({
                                resolve: () => resolve(http(originConfig)),
                                reject: () => reject(err)
                            })
                        })
                    }
                    isRefreshing = true
                    try {
                        const res = await refreshAccess()
                        if (res.code !== RESULT_CODE.SUCCESS) {
                            ElMessage.error(res.message || "刷新权限失败，请重新登陆")
                        }
                        const user = useUser();
                        user.accessToken = res.data;
                        // 执行队列中的请求
                        waitQueue.splice(0).forEach(q => q.resolve())
                        originConfig._retry = true;
                        return http.request(originConfig);
                    } catch (e) {
                        waitQueue.splice(0).forEach(q => q.reject(e))
                        clearAuth()
                        ElMessage.error(e instanceof Error ? e.message : "登录已失效，请重新登录");
                        return Promise.reject(e)
                    } finally {
                        isRefreshing = false
                    }
                }
                if (code === RESULT_CODE.ACCOUNT_ON_OTHER_DEVICE) {
                    router.push("/auth");
                    return ElMessage.error("账号已在其它设备登录，请重新登录");
                }
                clearAuth()
                ElMessage.error(message || "登录已失效，请重新登录");
                return Promise.reject(err)
            }
            case 403:
                return ElMessage.error(message || "没有权限访问该资源");
            case 404:
                return ElMessage.error(message || "请求的资源不存在");
            case 500:
                return ElMessage.error(message || "服务器内部错误");
            default:
                if (!err.response) {
                    return ElMessage.error("网络异常，请检查网络连接");
                }
                return ElMessage.error(message || "请求失败，请稍后重试");
        }
    }
);

export const post = <T extends Record<string, any>, R>(url: string, data: T): Promise<Response<R>> => {
    return http.post(url, data)
}

export const get = <T extends Record<string, any>, R>(url: string, params: Record<string, T>): Promise<Response<R>> => {
    return http.get(url, { params })
}

export const put = <T extends Record<string, object>, R>(url: string, data: T): Promise<Response<R>> => {
    return http.put(url, data)
}