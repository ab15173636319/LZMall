package org.app.marketserver.security;

import lombok.RequiredArgsConstructor;
import org.app.marketserver.common.result.ResponseCode;
import org.app.marketserver.common.result.Result;
import org.app.marketserver.filter.JwtAuthenticationFilter;
import org.app.marketserver.util.ResponseWriteUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthorizeIgnoreUrl authorizeIgnoreUrl;
    private final ResponseWriteUtil responseWriteUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                        {
                            authorizeIgnoreUrl.getIgnores().forEach(url -> auth.requestMatchers(url).permitAll());
                            auth.anyRequest().authenticated();
                        }
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex ->
                        ex
                                .authenticationEntryPoint((req, resp, e) -> {
                                    responseWriteUtil.writeToResponse(resp, 401, MediaType.APPLICATION_JSON_VALUE, Result.error(ResponseCode.AUTH_UNAUTHORIZED.getCode(), "未登录或登录过期，请重新登录"));
                                })
                                .accessDeniedHandler((req, resp, e) -> {
                                    responseWriteUtil.writeToResponse(resp, 403, MediaType.APPLICATION_JSON_VALUE, Result.error(ResponseCode.AUTH_FORBIDDEN.getCode(), "权限不足"));
                                })

                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

}
