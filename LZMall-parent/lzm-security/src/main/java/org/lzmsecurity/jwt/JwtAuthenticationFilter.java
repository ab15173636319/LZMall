package org.lzmsecurity.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.result.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "令牌格式错误");
                return;
            }
            token = token.substring(prefix.length());
            Map<String, Object> claims = jwtUtils.getTokenClaims(token);
            if (claims == null) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "未登录或登录过期");
                return;
            }
            String username = (String) claims.get("username");

            if (!StringUtils.hasText(username)) {
                ResponseResult.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "令牌载荷无效");
                return;
            }
            // 如果安全上下文为空，说明未认证，需要认证
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // 查询出用户对象
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 手动组装一个认证对象
                UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
                // 将认证对象放到上下文中
                SecurityContextHolder.getContext().setAuthentication(upat);
                logger.info("用户【{}】认证成功", username);
            }
        }
        // 未带token，直接放行交给spring security处理
        filterChain.doFilter(request, response);
    }
}
