package org.lzmservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.lzmmodel.model.userModel.dto.LoginDto;
import org.lzmmodel.model.userModel.dto.RegisterDto;
import org.lzmmodel.model.userModel.vo.UserVo;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDto userDto, HttpServletResponse response);

    void register(RegisterDto registerDto);

    String refreshToken(String refreshToken);

    UserVo getUserInfo(String username);
}
