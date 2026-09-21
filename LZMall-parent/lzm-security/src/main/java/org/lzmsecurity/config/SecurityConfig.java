package org.lzmsecurity.config;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.result.Result;
import org.lzmsecurity.jwt.JwtAuthenticalcationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * Spring Security 安全配置类
 * 当前配置为开发模式：关闭所有认证拦截，允许所有请求直接访问
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final JwtAuthenticalcationFilter jwtAuthenticalcationFilter;

    /**
     * 配置安全过滤链
     * - 放行所有请求，无需登录认证
     * - 关闭表单登录页
     * - 关闭 HTTP Basic 认证
     * - 关闭 CSRF 防护
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 授权配置：所有请求无需认证即可访问
                .authorizeHttpRequests(
                        auth -> {
                            for (String url : ignoreUrlsConfig.getUrls()) {
                                auth.requestMatchers(url).permitAll();
                            }
                            auth.requestMatchers(HttpMethod.OPTIONS).permitAll(); // 允许 OPTIONS 请求
                        }
                )
                // 禁用默认登录页面
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用 HTTP Basic 弹窗认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 禁用 CSRF 防护
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticalcationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        (request, response, authException) ->
                                                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "请先登录")
                                )
                                // 权限不足处理
                                .accessDeniedHandler(
                                        (request, response, accessDeniedException) -> {
                                            writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
                                        }
                                )
                );
        return http.build();
    }

    private static void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        Result<String> result = Result.failed(message);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }

}
