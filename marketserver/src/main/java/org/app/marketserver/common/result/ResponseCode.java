package org.app.marketserver.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应码常量类
 * <p>
 * 正常响应码 10000*
 * <p>
 * 认证异常响应码 20000*
 * <p>
 * 业务异常响应码 30000*
 * <p>
 * 系统异常响应码 40000*
 * <p>
 * 本枚举是响应码的唯一来源，前端常量 {@code app/src/constant/responseCode.ts} 与之一一对应，
 * 新增或修改响应码时必须同步前端，避免前端无法识别新的响应码。
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {
    // ==================== 正常响应码 10000* ====================
    /**
     * 成功响应码
     */
    SUCCESS(10000, "成功"),
    // -------- 幂等 / 并发 30500* --------
    /**
     * 重复提交
     */
    REPEAT_SUBMIT(30500, "请求正在处理中，请勿重复提交"),
    /**
     * 操作过于频繁
     */
    OPERATION_TOO_FREQUENT(30501, "操作过于频繁，请稍后再试"),

    // ==================== 系统异常响应码 40000* ====================
    /**
     * 系统异常
     */
    SYSTEM_ERROR(40000, "系统异常，请稍后重试"),
    /**
     * 参数校验失败
     */
    PARAM_INVALID(40001, "参数校验失败"),
    /**
     * 缺少必要参数
     */
    PARAM_MISSING(40002, "缺少必要参数"),
    /**
     * 请求方法不支持
     */
    REQUEST_METHOD_NOT_SUPPORTED(40003, "请求方法不支持"),
    /**
     * 请求资源不存在
     */
    RESOURCE_NOT_FOUND(40004, "请求资源不存在"),
    /**
     * 数据库操作异常
     */
    DATABASE_ERROR(40005, "数据库操作异常"),
    /**
     * 缓存服务异常
     */
    REDIS_ERROR(40006, "缓存服务异常"),
    /**
     * 数据已被其他请求修改（乐观锁冲突）
     */
    CONCURRENT_UPDATE_FAILED(40007, "数据已被其他请求修改，请重试"),
    /**
     * 获取锁失败
     */
    LOCK_ACQUIRE_FAILED(40008, "系统繁忙，请稍后重试"),
    /**
     * 远程服务调用失败
     */
    REMOTE_SERVICE_ERROR(40009, "远程服务调用失败");

    /**
     * 响应码
     */
    private final int code;
    /**
     * 响应信息
     */
    private final String message;

    /**
     * 响应码 -> 枚举 的索引，避免每次查询遍历枚举
     */
    private static final Map<Integer, ResponseCode> CODE_MAP;

    static {
        Map<Integer, ResponseCode> codeMap = new HashMap<>(values().length);
        for (ResponseCode responseCode : values()) {
            if (codeMap.put(responseCode.code, responseCode) != null) {
                throw new IllegalStateException("响应码重复定义：" + responseCode.code);
            }
        }
        CODE_MAP = Collections.unmodifiableMap(codeMap);
    }

    /**
     * 根据响应码获取枚举
     *
     * @param code 响应码
     * @return 匹配的枚举，未匹配到返回 {@code null}
     */
    public static ResponseCode fromCode(int code) {
        return CODE_MAP.get(code);
    }

    /**
     * 根据响应码获取枚举，兼容前后端响应码不一致的场景
     *
     * @param code         响应码
     * @param defaultValue 未匹配到响应码时返回的默认枚举
     * @return 匹配的枚举，未匹配到返回 {@code defaultValue}
     */
    public static ResponseCode fromCodeOrDefault(int code, ResponseCode defaultValue) {
        return CODE_MAP.getOrDefault(code, defaultValue);
    }

    /**
     * 判断响应码是否为成功
     *
     * @param code 响应码
     * @return 成功返回 {@code true}
     */
    public static boolean isSuccess(int code) {
        return SUCCESS.code == code;
    }
}
