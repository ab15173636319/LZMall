import type { Result } from "@type/response"
import { notification } from "antd"


export default function openHttpNotifation<T>(result: Result<T>, errorCondition?: number): Promise<T | boolean> {
    return new Promise((resolve, reject) => {
        const currentCondition = errorCondition || 40000
        const { code, message, data } = result
        if (code === 200) {
            notification.success({
                description: message
            })
            resolve(data == null ? true : data)
        } else if (code >= currentCondition) {
            notification.error({
                description: message
            })
        } else {
            notification.warning({
                description: message
            })
        }
        reject(false)
    })
}
