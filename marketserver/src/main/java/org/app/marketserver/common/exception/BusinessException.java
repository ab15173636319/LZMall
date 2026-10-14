package org.app.marketserver.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.app.marketserver.common.result.ResponseCode;

/**
 * 业务异常
 * <p>
 * 业务逻辑校验不通过时抛出，由 {@code GlobalExceptionHandler} 统一转换为 Result 响应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    /**
     * 业务响应码，见 {@link ResponseCode}
     */
    private int code;
    /**
     * 提示信息
     */
    private String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    /**
     * 包装底层异常为业务异常（对外只暴露 responseCode 的提示，原始异常保留在 cause 中）
     * <p>
     * 必须传 cause：否则日志里只剩"系统异常，请稍后重试"，排查时看不到真实失败原因。
     */
    public BusinessException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}
