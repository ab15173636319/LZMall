package org.lzmweb.controller.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.result.Result;
import org.lzmmodel.model.userModel.dto.LoginDto;
import org.lzmmodel.model.userModel.dto.RegisterDto;
import org.lzmservice.impl.UserImpl;
import org.lzmservice.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDto loginDto) {
        return Result.success("登录成功", userService.login(loginDto));
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
        return Result.success("注册成功");
    }

    @PostMapping("/refreshAccess")
    public Result<String> refreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                String refreshToken = cookie.getValue();
                return Result.success("刷新成功", userService.refreshToken(refreshToken));
            }
        }
        return Result.failed("刷新失败");
    }
}
