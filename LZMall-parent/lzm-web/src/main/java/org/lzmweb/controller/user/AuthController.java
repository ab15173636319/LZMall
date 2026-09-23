package org.lzmweb.controller.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.http.CookieSet;
import org.lzmcommon.result.Result;
import org.lzmcommon.result.ResultCode;
import org.lzmmodel.model.userModel.dto.LoginDto;
import org.lzmmodel.model.userModel.dto.RegisterDto;
import org.lzmmodel.model.userModel.vo.UserVo;
import org.lzmservice.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CookieSet cookieSet;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        return Result.success("登录成功", userService.login(loginDto, response));
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
        return Result.success("注册成功");
    }

    @PostMapping("/refreshAccess")
    public Result<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieSet.getCookie(request, "refreshToken");
        if (StringUtils.hasText(refresh)) {
            return Result.success("刷新成功", userService.refreshToken(refresh, response));
        }
        return Result.failed("刷新失败");
    }

    @GetMapping("/info")
    public Result<UserVo> getUserInfo(Principal principal) {
        if (principal == null) {
            throw new BusinessException(ResultCode.T_ACCOUNT_NOT_LOGIN.getCode(), ResultCode.T_ACCOUNT_NOT_LOGIN.getMessage());
        }
        String username = principal.getName();
        UserVo userVo = userService.getUserInfo(username);
        return Result.success("获取用户信息成功", userVo);
    }
}
