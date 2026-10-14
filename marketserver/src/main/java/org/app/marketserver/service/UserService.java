package org.app.marketserver.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.app.marketserver.pojo.dto.LoginDto;
import org.app.marketserver.pojo.dto.RegisterDto;
import org.app.marketserver.security.LoginUser;

public interface UserService {

    void register(RegisterDto registerDto);

    String login(LoginDto loginDto, HttpServletResponse response);

    String refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    void logout(LoginUser user);


}
