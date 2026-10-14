package org.app.marketserver.util;

import cn.hutool.http.HttpStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.app.marketserver.common.result.Result;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ResponseWriteUtil {

    public void writeToResponse(HttpServletResponse response, int httpStatus, String type, Result<?> result) throws IOException {

        response.setStatus(httpStatus);
        response.setContentType(type);
        response.getWriter().write(result.toString());

    }
}
