package org.lzmcommon.result;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseResult {
    public static void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        Result<String> result = Result.failed(message);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
