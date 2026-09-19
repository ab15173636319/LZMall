package org.lzmcommon.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Result<T> implements Serializable {

    private ResultCode code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultCode.SUCCESS, ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>(ResultCode.SUCCESS, message, data);
    }

    public static <T> Result<T> systemError(String message) {
        return new Result<T>(ResultCode.SERVICE_UNAVAILABLE, message, null);
    }

    public static <T> Result<T> failed(String message) {
        return new Result<T>(ResultCode.FAILED, message, null);
    }

    public static <T> Result<T> failed(ResultCode code, String message) {
        return new Result<T>(code, message, null);
    }

}
