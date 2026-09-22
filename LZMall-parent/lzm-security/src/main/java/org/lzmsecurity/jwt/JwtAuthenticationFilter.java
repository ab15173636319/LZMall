package org.lzmsecurity.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.result.ResponseResult;
import org.lzmcommon.result.ResultCode;
import org.lzmcommon.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisUtils redisUtils;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * 认证过滤器
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤链链
     * @throws ServletException servlet异常
     * @throws IOException      io异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(jwtUtils.getHeader());

        String prefix = jwtUtils.getPrefix() + " ";
        // 携带token，进入验证流程
        if (StringUtils.hasText(token)) {
            if (!token.startsWith(prefix)) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        ResultCode.R_BAD_REQUEST.getCode(), "令牌格式错误");
                return;
            }
            token = token.substring(prefix.length());
            // 获取access token的payload
            Map<String, Object> accessClaims = jwtUtils.getTokenClaims(token);
            if (accessClaims == null) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_TOKEN_EXPIRED.getCode(), "登录过期");
                return;
            }

            String username = (String) accessClaims.get("username");
            Object uid = accessClaims.get("uid");
            String jti = (String) accessClaims.get("jti");

            if (!StringUtils.hasText(username) || uid == null || !StringUtils.hasText(jti)) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_ACCOUNT_NOT_LOGIN.getCode(), ResultCode.T_ACCOUNT_NOT_LOGIN.getMessage());
                return;
            }

            // 获取refresh token的payload
            String refreshCacheKey = jwtUtils.getREFRESH_CACHE_KEY() + uid;
            String refreshToken = (String) redisUtils.get(refreshCacheKey);
            if (!StringUtils.hasText(refreshToken)) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_ACCOUNT_NOT_LOGIN.getCode(), ResultCode.T_ACCOUNT_NOT_LOGIN.getMessage());
                return;
            }

            Map<String, Object> refreshClaims = jwtUtils.getTokenClaims(refreshToken);
            if (refreshClaims == null) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_TOKEN_EXPIRED.getCode(), ResultCode.T_TOKEN_EXPIRED.getMessage());
                return;
            }

            String refreshJti = (String) refreshClaims.get("jti");
            if (!jti.equals(refreshJti)) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getCode(), ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getMessage());
                return;
            }


            // 如果安全上下文为空，说明未认证，需要认证
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // 查询出用户对象
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 手动组装一个认证对象
                UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // 将认证对象放到上下文中
                SecurityContextHolder.getContext().setAuthentication(upat);
                logger.info("用户【{}】认证成功", username);
            }
        }
        // 未带token，直接放行交给spring security处理
        filterChain.doFilter(request, response);
    }
}
