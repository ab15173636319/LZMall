package org.lzmservice.impl;

import lombok.RequiredArgsConstructor;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.result.ResultCode;
import org.lzmcommon.utils.RedisUtils;
import org.lzmsecurity.jwt.JwtUtils;
import org.lzmservice.mapper.UserMapper;
import org.lzmservice.pojo.dto.LoginDto;
import org.lzmservice.pojo.dto.RegisterDto;
import org.lzmservice.pojo.entity.User;
import org.lzmservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final RedisUtils redisUtils;

    private static final String ACCESS_CACHE_KEY = "accessToken:";

    @Override
    public Map<String, Object> login(LoginDto loginDto) {

        if (!StringUtils.hasText(loginDto.getUsername()) || !StringUtils.hasText(loginDto.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名或密码不能为空");
        }

        Optional<User> userOptional = userMapper.selectByUsername(loginDto.getUsername());

        if (!userOptional.isPresent()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("nickname", user.getNickname());

        String refreshToken = jwtUtils.generateRefreshToken(claims);
        String accessToken = jwtUtils.generateAccessToken(claims);

        redisUtils.set(ACCESS_CACHE_KEY + user.getId(), accessToken, jwtUtils.getAccessExpiration(), TimeUnit.MILLISECONDS);


        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);

        return result;
    }

    @Override
    public void register(RegisterDto registerDto) {

    }
}
