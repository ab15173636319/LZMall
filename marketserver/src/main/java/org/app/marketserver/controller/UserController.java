package org.app.marketserver.controller;

import lombok.RequiredArgsConstructor;
import org.app.marketserver.common.result.Result;
import org.app.marketserver.pojo.dto.RegisterDto;
import org.app.marketserver.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
        return Result.success("注册成功");
    }
}
