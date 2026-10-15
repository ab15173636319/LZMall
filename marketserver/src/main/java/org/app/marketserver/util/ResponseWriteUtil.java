package org.app.marketserver.util;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.app.marketserver.common.result.Result;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ResponseWriteUtil {

    public void writeToResponse(HttpServletResponse response, int httpStatus, String type, Result<?> result) throws IOException {

        response.setStatus(httpStatus);
        response.setContentType(type);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JSONUtil.toJsonStr(result));

    }
}
