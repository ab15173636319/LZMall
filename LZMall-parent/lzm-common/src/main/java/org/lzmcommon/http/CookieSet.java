package org.lzmcommon.http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CookieSet {

    private final Logger logger = LoggerFactory.getLogger(CookieSet.class);

    /**
     * 设置cookie
     *
     * @param response
     * @param name
     * @param value
     * @param maxAge
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie refreshTokenCookie = new Cookie(name, value);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);

        response.addCookie(refreshTokenCookie);
    }

    /**
     * 获取cookie中某值
     *
     * @param request
     * @param name
     * @return
     */
    public String getCookie(HttpServletRequest request, String name) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            logger.error("获取cookie时发生错误");
            throw new BusinessException(ResultCode.R_NOT_FOUND.getCode(), "资源请求错误");
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                value = cookie.getValue();
            }
        }
        return value;
    }

}
