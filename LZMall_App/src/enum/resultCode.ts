
export const RESULT_CODE = {
    SUCCESS: 200,
    /**token过期 */
    TOKEN_EXPIRED: 11001,
    /**权限不足 */
    PERMISSION_DENIED: 11002,
    /**角色不存在 */
    ROLE_NOT_EXIST: 11003,
    /**菜单不存在 */
    MENU_NOT_EXIST: 11004,
    /**账号已锁定 */
    ACCOUNT_LOCKED:11005,
    /** 未登录 */
    ACCOUNT_NOT_LOGIN: 11006,
    /** 账号已在其它设备登录 */
    ACCOUNT_ON_OTHER_DEVICE: 11007,
} as const

export type ResultCodeValue = (typeof RESULT_CODE)[keyof typeof RESULT_CODE]

export default RESULT_CODE
