package org.lzmcommon.result;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseResult {

    /**
     * <strong>
     * 注意：httpStatus 必须是合法的 HTTP 状态码（3 位数字，100~599）。
     * 状态行会原样落到网络上，若写入业务码（如 11007），Node / 浏览器的 HTTP 解析器
     * 会在第一行就报 "Invalid response status"，Vite 开发代理会因此返回空 502。
     * <strong>
     * 业务码请通过 code 参数放进响应体，由前端读取 body.code 判断。
     *
     * @param response   HTTP 响应
     * @param httpStatus 合法的 HTTP 状态码（3 位）
     * @param code       业务状态码（如 ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getCode()）
     * @param message    提示信息
     */
    public static void writeErrorResponse(HttpServletResponse response, int httpStatus, int code, String message)
            throws IOException {
        // 兜底：非法状态码统一降级为 200，业务码仍从响应体透出，避免污染状态行
        if (httpStatus < 100 || httpStatus > 599) {
            httpStatus = HttpServletResponse.SC_OK;
        }
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(httpStatus);
        response.getWriter().write(JSONUtil.toJsonStr(Result.failed(code, message)));
    }
}
