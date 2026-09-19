package org.lzmservice.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.result.ResultCode;
import org.lzmsecurity.jwt.JwtUtils;
import org.lzmservice.mapper.UserMapper;
import org.lzmservice.pojo.dto.LoginDto;
import org.lzmservice.pojo.dto.RegisterDto;
import org.lzmservice.pojo.entity.User;
import org.lzmservice.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final RedisTemplate<String, Object> redisTemplate;

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

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("nickname", user.getNickname());

        String refreshToken = jwtUtils.generateRefreshToken(claims);
        String accessToken = jwtUtils.generateAccessToken(claims);

        redisTemplate.opsForValue().set(ACCESS_CACHE_KEY + user.getId(), accessToken);

        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);

        return map;
    }

    @Override
    public void register(RegisterDto registerDto) {

    }
}
