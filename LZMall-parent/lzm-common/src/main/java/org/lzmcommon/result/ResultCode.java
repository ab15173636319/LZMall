package org.lzmcommon.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    /* ==================== 通用错误（与 HTTP 状态码对齐） ==================== */
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(404, "参数检验失败"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "数据冲突"),
    PRECONDITION_FAILED(412, "前提条件失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    FAILED(500, "操作失败"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /* ==================== 权限模块 18000+ ==================== */
    PERMISSION_DENIED(18001, "权限不足"),
    ROLE_NOT_EXIST(18002, "角色不存在"),
    MENU_NOT_EXIST(18003, "菜单不存在"),
    ACCOUNT_LOCKED(18004, "账号已锁定");

    private final int code;
    private final String message;
}