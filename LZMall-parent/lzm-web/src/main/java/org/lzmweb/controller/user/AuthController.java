package org.lzmweb.controller.user;

import lombok.RequiredArgsConstructor;
import org.lzmcommon.result.Result;
import org.lzmmodel.model.userModel.dto.LoginDto;
import org.lzmservice.impl.UserImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final UserImpl userImpl;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDto loginDto) {
        return Result.success(userImpl.login(loginDto));
    }
}
