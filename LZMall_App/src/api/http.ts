import type { Response } from "@/types/http";
import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
import { useUser } from "@/store";
import router from "@/router";


const http = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: import.meta.env.VITE_API_TIMEOUT,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
    }
})

http.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const user = useUser()
        // 只把 accessToken 放进 Authorization 头；
        // refreshToken 是长期凭证，绝不能随业务请求发给后端，只在「刷新接口」里单独提交。
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
            return res;
        }
        return ElMessage.warning(res.message);
    },
    (err: AxiosError<Response<any>>) => {
        const status = err.response?.status;
        const message = err.response?.data?.message;

        switch (status) {
            case 401: {
                // 登录失效：清除本地用户信息并跳转登录页
                // 注意：这里是 axios 回调，不在 setup() 里，不能用 useRouter()（会返回 undefined）
                const user = useUser();
                user.userInfo = null;
                user.accessToken = '';
                user.refreshToken = '';
                router.push("/auth");
                return ElMessage.error(message || "登录已失效，请重新登录");
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