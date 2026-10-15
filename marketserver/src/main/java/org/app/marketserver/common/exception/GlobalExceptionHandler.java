package org.app.marketserver.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.app.marketserver.common.result.ResponseCode;
import org.app.marketserver.common.result.Result;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 业务异常：返回业务响应码与提示信息
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.warn("用户名不存在：{}", e.getMessage());
        return Result.error(404, e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse(ResponseCode.PARAM_INVALID.getMessage());
        log.warn("参数校验失败：{}", message);
        return Result.error(ResponseCode.PARAM_INVALID.getCode(), message);
    }

    /**
     * 缺少必要的请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数：{}", e.getParameterName());
        return Result.error(ResponseCode.PARAM_MISSING.getCode(),
                ResponseCode.PARAM_MISSING.getMessage() + "：" + e.getParameterName());
    }

    /**
     * 请求体无法读取（JSON 格式错误、类型不匹配等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败：{}", e.getMessage());
        return Result.error(ResponseCode.PARAM_INVALID.getCode(), "请求体格式错误");
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持：{}", e.getMessage());
        return Result.error(ResponseCode.REQUEST_METHOD_NOT_SUPPORTED);
    }

    /**
     * 静态资源/接口路径不存在
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<String> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("请求资源不存在：{}", e.getResourcePath());
        return Result.error(ResponseCode.RESOURCE_NOT_FOUND);
    }

    /**
     * 方法级鉴权（@PreAuthorize 等）权限不足；过滤器链上的 403 由 RestAccessDeniedHandler 处理
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足：{}", e.getMessage());
        return Result.error(ResponseCode.SYSTEM_ERROR);
    }

    /**
     * 兜底：未知异常不暴露堆栈，只返回系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResponseCode.SYSTEM_ERROR);
    }

    /**
     * 认证失败：登录校验（用户名/密码错误）、账号禁用等
     * 由 AuthenticationManager 抛出，必须在进入兜底 Exception 之前拦下，
     * 否则会被当成 40000 系统异常返回并打印完整堆栈。
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<String> handleAuthenticationException(AuthenticationException e) {
        if (e instanceof DisabledException || e instanceof LockedException) {
            log.warn("账号状态异常：{}", e.getMessage());
            return Result.error(ResponseCode.AUTH_ACCOUNT_DISABLED);
        }
        log.warn("认证失败：{}", e.getMessage());
        return Result.error(ResponseCode.AUTH_FAILED);
    }

}
