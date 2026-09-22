package org.lzmcommon.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    /* ==================== 通用错误（与 HTTP 状态码对齐） ==================== */
    SUCCESS(200, "操作成功"),


    /* ==================== 客户端错误 400+ ==================== */
    R_BAD_REQUEST(400, "请求参数错误"),
    R_FORBIDDEN(403, "没有相关权限"),
    R_VALIDATE_FAILED(404, "参数检验失败"),
    R_METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    R_CONFLICT(409, "数据冲突"),
    R_PRECONDITION_FAILED(412, "前提条件失败"),
    R_TOO_MANY_REQUESTS(429, "请求过于频繁"),
    R_SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    R_NOT_FOUND(404, "资源不存在"),
    R_INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /* ==================== 权限模块 11000+ ==================== */
    T_TOKEN_EXPIRED(11001, "token过期"),
    T_PERMISSION_DENIED(11002, "权限不足"),
    T_ROLE_NOT_EXIST(11003, "角色不存在"),
    T_MENU_NOT_EXIST(11004, "菜单不存在"),
    T_ACCOUNT_LOCKED(11005, "账号已锁定"),
    T_ACCOUNT_NOT_LOGIN(11006, "未登录"),
    T_ACCOUNT_ON_OTHER_DEVICE(11007, "账号已在其它设备登录"),
    T_TOKEN_IS_EMPTY(11008, "token为空"),
    T_TOKEN_PARSE_FAILED(11009, "token解析失败"),
    T_TOKEN_INVALID(11010, "token无效");

    private final int code;
    private final String message;
}