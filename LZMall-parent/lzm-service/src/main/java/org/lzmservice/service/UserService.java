package org.lzmservice.service;

import org.lzmmodel.model.userModel.dto.LoginDto;
import org.lzmmodel.model.userModel.dto.RegisterDto;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDto userDto);

    void register(RegisterDto registerDto);
}
