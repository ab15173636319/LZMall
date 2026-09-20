package org.lzmservice.impl;

import lombok.RequiredArgsConstructor;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.result.ResultCode;
import org.lzmcommon.utils.RedisUtils;
import org.lzmsecurity.jwt.JwtUtils;
import org.lzmservice.mapper.UserMapper;
import org.lzmmodel.model.userModel.dto.LoginDto;
import org.lzmmodel.model.userModel.dto.RegisterDto;
import org.lzmmodel.model.userModel.entity.User;
import org.lzmservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserImpl.class);
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final RedisUtils redisUtils;

    private static final String ACCESS_CACHE_KEY = "accessToken:";

    @Override
    public Map<String, Object> login(LoginDto loginDto) {

        if (!StringUtils.hasText(loginDto.getUsername()) || !StringUtils.hasText(loginDto.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名或密码不能为空");
        }

        Optional<User> userOptional = getUser(loginDto.getUsername());

        if (userOptional.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名或密码错误");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名或密码错误");
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
        result.put("intro", "注意refresh为刷新token（有效时间7天），最好做持久化，access为访问token（有效时间30分钟），可存入session，最好不要做持久化");

        return result;
    }

    @Transactional // 如果方法中有异常，回滚事务，确保数据库一致性
    @Override
    public void register(RegisterDto registerDto) {

        if (!registerDto.getPassword().equals(registerDto.getValidatePassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "两次密码不一致");
        }

        Optional<User> userOptional = getUser(registerDto.getUsername());

        if (userOptional.isPresent()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名已存在");
        }

        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        User user = new User();
        BeanUtils.copyProperties(registerDto, user);
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            log.warn("注册失败：用户名重复", e);
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名已存在");
        } catch (DataIntegrityViolationException e) {
            log.warn("注册失败：数据完整性异常", e);
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "数据校验失败");
        } catch (Exception e) {
            log.error("注册失败：系统异常", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "注册失败：系统异常");
        }
    }

    private Optional<User> getUser(String username) {
        try {
            return userMapper.selectByUsername(username);
        } catch (Exception e) {
            log.error("根据用户名查询用户失败：系统异常", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统异常");
        }
    }



}
