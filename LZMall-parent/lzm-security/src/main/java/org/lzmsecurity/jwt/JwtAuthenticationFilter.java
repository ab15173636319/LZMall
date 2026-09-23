package org.lzmsecurity.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.properties.RedisProperties;
import org.lzmcommon.result.ResponseResult;
import org.lzmcommon.result.ResultCode;
import org.lzmcommon.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisUtils redisUtils;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final ObjectMapper objectMapper;
    private final RedisProperties redisProperties;

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

            String username = jwtUtils.getUsername(token);
            Object uid = jwtUtils.getUid(token);
            String jti = jwtUtils.getJti(token);

            // 检查access token是否有效
            if (!StringUtils.hasText(username) || uid == null || !StringUtils.hasText(jti)) {
                ResponseResult.writeErrorResponse(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_ACCOUNT_NOT_LOGIN.getCode(),
                        ResultCode.T_ACCOUNT_NOT_LOGIN.getMessage()
                );
                return;
            }

            // 获取当前请求路径
            // 检查access token是否过期，排除/user/refreshAccess路径
            String path = request.getServletPath();
            long remainingTime = jwtUtils.getTokenRemainingTime(token);
            if (remainingTime <= 0 && !path.contains("/user/refreshAccess")) {
                ResponseResult.writeErrorResponse(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_TOKEN_EXPIRED.getCode(),
                        ResultCode.T_TOKEN_EXPIRED.getMessage()
                );
                return;
            }

            // 获取refresh token的payload
            String refreshCacheKey = jwtUtils.getCacheToken(uid);
            String refreshToken = (String) redisUtils.get(refreshCacheKey);
            if (!StringUtils.hasText(refreshToken)) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_ACCOUNT_NOT_LOGIN.getCode(), ResultCode.T_ACCOUNT_NOT_LOGIN.getMessage());
                return;
            }
            // 检查refresh token是否与access token匹配
            if (!jwtUtils.isSameToken(token, refreshToken)) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getCode(), ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getMessage());
                return;
            }


            // 如果安全上下文为空，说明未认证，需要认证
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // 获取缓存中的用户信息
                String userCacheKey = redisProperties.getSaveUserInfo() + username + ": ";
                UserDetails userDetails = (UserDetails) redisUtils.get(userCacheKey);
                if (Objects.isNull(userDetails)) {
                    userDetails = userDetailsService.loadUserByUsername(username);
                    redisUtils.set(redisProperties.getSaveUserInfo(), userDetails);
                }

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
