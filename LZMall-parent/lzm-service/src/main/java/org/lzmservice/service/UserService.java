package org.lzmservice.service;

import org.lzmservice.pojo.dto.LoginDto;
import org.lzmservice.pojo.dto.RegisterDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {
    Map<String, Object> login(LoginDto userDto);

    void register(RegisterDto registerDto);
}
