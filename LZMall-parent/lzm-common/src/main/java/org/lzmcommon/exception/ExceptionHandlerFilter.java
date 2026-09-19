package org.lzmcommon.exception;

import org.lzmcommon.result.Result;
import org.lzmcommon.result.ResultCode;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerFilter {

    // 处理自定义异常
    @ExceptionHandler(BusinessException.class)
    public Result<String> businessException(BusinessException e) {
        return Result.failed(e.getCode(), e.getMessage());
    }

    // 处理运行时异常
    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception e) {
        return Result.failed(ResultCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    public Result<String> nullPointerException(NullPointerException e) {
        return Result.failed(ResultCode.BAD_REQUEST, "参数不能为空");
    }

    // 处理非法参数异常
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> illegalArgumentException(IllegalArgumentException e) {
        return Result.failed(ResultCode.BAD_REQUEST, "参数错误");
    }

    // 处理数字格式异常
    @ExceptionHandler(NumberFormatException.class)
    public Result<String> numberFormatException(NumberFormatException e) {
        return Result.failed(ResultCode.BAD_REQUEST, "参数格式错误");
    }

    // 处理非法状态异常
    @ExceptionHandler(IllegalStateException.class)
    public Result<String> illegalStateException(IllegalStateException e) {
        return Result.failed(ResultCode.BAD_REQUEST, "状态错误");
    }

    // 处理请求方式异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.failed(ResultCode.BAD_REQUEST, "请求方式错误");
    }


}