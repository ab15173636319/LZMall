package org.lzmsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类
 * 当前配置为开发模式：关闭所有认证拦截，允许所有请求直接访问
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
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
                        auth ->
                                auth.requestMatchers("/test/**").permitAll()
                                        .anyRequest().permitAll()
                )
                // 禁用默认登录页面
                .formLogin(form -> form.disable())
                // 禁用 HTTP Basic 弹窗认证
                .httpBasic(basic -> basic.disable())
                // 禁用 CSRF 跨站请求伪造防护
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

}
