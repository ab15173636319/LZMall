import type { Result } from "@type/response";
import axios from "axios";

const http = axios.create({
    baseURL: "/api",
    timeout: 10000
})

http.interceptors.request.use(
    (config) => {
        return config;
    },
    (err) => {
        return Promise.reject(err);
    }
);

http.interceptors.response.use(
    (res) => {
        console.log(res);

        return res;
    }, (err) => {
        return Promise.reject(err);
    }
);

const get = async <P, T>(url: string, params: P): Promise<Result<T>> => {
    const { data } = await http.get<Result<T>>(url, { params });
    return data;
};

const post = async <P, T>(url: string, params: P): Promise<Result<T>> => {
    const { data } = await http.post<Result<T>>(url, params);
    return data;
};

export { get, post }