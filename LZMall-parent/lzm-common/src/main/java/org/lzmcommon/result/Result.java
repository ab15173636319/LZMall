package org.lzmcommon.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Result<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(String message) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), message, null);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> systemError(String message) {
        return new Result<T>(ResultCode.R_SERVICE_UNAVAILABLE.getCode(), message, null);
    }

    public static <T> Result<T> failed(String message) {
        return new Result<T>(ResultCode.R_INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> failed(int code, String message) {
        return new Result<T>(code, message, null);
    }

}
