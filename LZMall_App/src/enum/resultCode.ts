/**
 * 后端业务状态码，与后端 ResultCode.java 保持一致。
 *
 * ⚠️ 这是响应体 `code` 字段里的业务码，不是 HTTP 状态码。
 * HTTP 状态码只能是 3 位数字（100~599），把 11007 这类业务码写进状态行，
 * Node / 浏览器会在解析状态行第一行时报 "Invalid response status"。
 */
export const RESULT_CODE = {
    SUCCESS: 200,
    /** 未登录 */
    ACCOUNT_NOT_LOGIN: 11006,
    /** 账号已在其它设备登录 */
    ACCOUNT_ON_OTHER_DEVICE: 11007,
} as const

export type ResultCodeValue = (typeof RESULT_CODE)[keyof typeof RESULT_CODE]

export default RESULT_CODE
