package org.app.marketserver.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 业务响应码，见 {@link ResponseCode}
     */
    private int code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 业务数据
     */
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), message, null);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> error(ResponseCode responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResponseCode.SYSTEM_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
}
