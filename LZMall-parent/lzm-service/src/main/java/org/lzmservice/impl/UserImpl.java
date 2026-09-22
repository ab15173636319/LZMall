package org.lzmservice.impl;

import cn.hutool.core.lang.UUID;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.result.ResultCode;
import org.lzmcommon.utils.RedisUtils;
import org.lzmmodel.model.userModel.vo.UserVo;
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


    @Override
    public Map<String, Object> login(LoginDto loginDto, HttpServletResponse response) {

        if (!StringUtils.hasText(loginDto.getUsername()) || !StringUtils.hasText(loginDto.getPassword())) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "用户名或密码不能为空");
        }

        Optional<User> userOptional = getUser(loginDto.getUsername());

        if (userOptional.isEmpty()) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "用户名或密码错误");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "用户名或密码错误");
        }

        // 登陆成功后生成refreshToken和accessToken
        // refreshToken存入内存
        // accessToken给前端
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("username", user.getUsername());
        //
        // lockKey用于防止重复登录
        // lockKey存入内存
        String ssid = UUID.randomUUID().toString().replace("-", "");
        claims.put("jti", ssid);
        String refreshToken;
        String accessToken;
        try {
            refreshToken = jwtUtils.generateRefreshToken(claims);
            accessToken = jwtUtils.generateAccessToken(claims);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.R_INTERNAL_SERVER_ERROR.getCode(), "token生成失败");
        }

        // 判断是否已经登录
        String cacheKey = jwtUtils.getREFRESH_CACHE_KEY() + user.getId();
        if (redisUtils.hasKey(cacheKey)) {
            String oldRefreshToken = (String) redisUtils.get(cacheKey);
            long remainingTime = jwtUtils.getTokenRemainingTime(oldRefreshToken);
            log.info("oldRefreshToken {} 还期时间 {} ms", oldRefreshToken, remainingTime);
            redisUtils.set(jwtUtils.getDARK_REFRESH_CACHE_KEY() + user.getId(), oldRefreshToken, remainingTime, TimeUnit.MILLISECONDS);
        }

        redisUtils.set(cacheKey, refreshToken, jwtUtils.getRefreshExpiration(), TimeUnit.MILLISECONDS);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // 只有https才需要设置为true，否则会报错，本地开发环境不需要设置为true
        refreshTokenCookie.setPath("/"); // 必须加上path！你之前漏掉了
        refreshTokenCookie.setMaxAge((int) (jwtUtils.getRefreshExpiration() / 1000));

        response.addCookie(refreshTokenCookie);


        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);

        return result;
    }

    @Transactional // 如果方法中有异常，回滚事务，确保数据库一致性
    @Override
    public void register(RegisterDto registerDto) {

        if (!registerDto.getPassword().equals(registerDto.getValidatePassword())) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "两次密码不一致");
        }

        Optional<User> userOptional = getUser(registerDto.getUsername());

        if (userOptional.isPresent()) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "用户名已存在");
        }

        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        User user = new User();
        BeanUtils.copyProperties(registerDto, user);
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            log.warn("注册失败：用户名重复", e);
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "用户名已存在");
        } catch (DataIntegrityViolationException e) {
            log.warn("注册失败：数据完整性异常", e);
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "数据校验失败");
        } catch (Exception e) {
            log.error("注册失败：系统异常", e);
            throw new BusinessException(ResultCode.R_INTERNAL_SERVER_ERROR.getCode(), "注册失败：系统异常");
        }
    }

    // 通过刷新token获取新的访问token
    @Override
    public String refreshToken(String refreshToken) {
        // 校验refreshToken是否有效（签名+未过期）
        if (jwtUtils.isTokenExpired(refreshToken)) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "登录过期");
        }

        // 校验refreshToken是否匹配内存中的refreshToken
        Map<String, Object> claims = jwtUtils.getTokenClaims(refreshToken);
        String uid = (String) claims.get("uid");
        String jti = (String) claims.get("jti");
        String cacheKey = jwtUtils.getREFRESH_CACHE_KEY() + uid;
        String oldRefreshToken = (String) redisUtils.get(cacheKey);
        if (!oldRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getCode(), ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getMessage());
        }

        return jwtUtils.refreshAccessToken(refreshToken);
    }

    @Override
    public UserVo getUserInfo(String username) {
        Optional<User> userOptional = getUser(username);
        if (userOptional.isEmpty()) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "用户不存在");
        }
        User user = userOptional.get();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }


    private Optional<User> getUser(String username) {
        try {
            return userMapper.selectByUsername(username);
        } catch (Exception e) {
            log.error("根据用户名查询用户失败：系统异常", e);
            throw new BusinessException(ResultCode.R_INTERNAL_SERVER_ERROR.getCode(), "查询用户失败：系统异常");
        }
    }


}
