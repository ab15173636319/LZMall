package org.app.marketserver.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.app.marketserver.common.exception.BusinessException;
import org.app.marketserver.common.result.Result;
import org.app.marketserver.pojo.dto.LoginDto;
import org.app.marketserver.security.LoginUser;
import org.app.marketserver.service.UserService;
import org.app.marketserver.service.impl.RefreshTokenService;
import org.app.marketserver.service.impl.UserImpl;
import org.app.marketserver.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final String COOKIE_KEY = "refresh_token";


    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {


        return Result.success("登陆成功", userService.login(loginDto, response));
    }

    @PostMapping("/refresh")
    public Result<String> refresh(
            @CookieValue(COOKIE_KEY) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return Result.success("刷新成功", userService.refreshToken(refreshToken, request, response));
    }

    @PostMapping("/logout")
    public Result<String> logout(@AuthenticationPrincipal LoginUser user) {
        userService.logout(user);
        return Result.success("退出成功");
    }


}
