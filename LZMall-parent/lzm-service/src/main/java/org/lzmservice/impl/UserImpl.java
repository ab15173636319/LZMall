package org.lzmservice.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.http.CookieSet;
import org.lzmcommon.result.ResultCode;
import org.lzmcommon.utils.RedisUtils;
import org.lzmmodel.model.userModel.dto.UpdateNickname;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CookieSet cookieSet;


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
        claims.put("jti", jwtUtils.generateJti());
        String refreshToken = jwtUtils.generateRefreshToken(claims);
        String accessToken = jwtUtils.generateAccessToken(claims);


        // 判断是否已经登录
        // 如果已经登录，则将旧的refreshToken存入黑名单，并设置过期时间为剩余时间
        String cacheKey = jwtUtils.getREFRESH_CACHE_KEY() + user.getId();
        if (redisUtils.hasKey(cacheKey)) {
            String oldRefreshToken = (String) redisUtils.get(cacheKey);
            long remainingTime = jwtUtils.getTokenRemainingTime(oldRefreshToken);
            String jti = jwtUtils.getJti(oldRefreshToken);
            redisUtils.set(jwtUtils.getDARK_REFRESH_CACHE_KEY() + user.getId() + jti, oldRefreshToken, remainingTime, TimeUnit.MILLISECONDS);
        }

        redisUtils.set(cacheKey, refreshToken, jwtUtils.getRefreshExpiration(), TimeUnit.MILLISECONDS);
        // 设置cookie
        CookieSet.setCookie(response, "refreshToken", refreshToken, (int) (jwtUtils.getRefreshExpiration() / 1000));


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
    public String refreshToken(String refreshToken, HttpServletResponse response) {
        // 校验refreshToken是否有效（签名+未过期）
        if (jwtUtils.isTokenExpired(refreshToken)) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "登录过期");
        }

        // 校验refreshToken是否匹配内存中的refreshToken
        Map<String, Object> claims = jwtUtils.getTokenClaims(refreshToken);
        Object uid = jwtUtils.getUid(refreshToken);

        // 查看当前token是否在黑名单中
        String jti = (String) claims.get("jti");
        String darkCacheKey = jwtUtils.getBlackKey(jti, uid);
        String darkRefreshToken = (String) redisUtils.get(darkCacheKey);
        // 如果在黑名单中，则返回登录过期
        if (StringUtils.hasText(darkRefreshToken) && darkRefreshToken.equals(refreshToken)) {
            throw new BusinessException(
                    ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getCode(),
                    ResultCode.T_ACCOUNT_ON_OTHER_DEVICE.getMessage()
            );
        }

        // 更新jti
        claims.put("jti", jwtUtils.generateJti());
        // 更新refreshToken
        String newRefreshToken = jwtUtils.generateRefreshToken(claims);
        String refreshCacheKey = jwtUtils.getCacheToken(uid);
        redisUtils.set(refreshCacheKey, newRefreshToken, jwtUtils.getRefreshExpiration(), TimeUnit.MILLISECONDS);
        // 更新cookie
        CookieSet.setCookie(response, "refreshToken", newRefreshToken, (int) (jwtUtils.getRefreshExpiration() / 1000));

        return jwtUtils.generateAccessToken(claims);
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

    @Override
    public void updateNickname(UpdateNickname updateNickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new BusinessException(
                    ResultCode.T_ACCOUNT_NOT_LOGIN.getCode(),
                    ResultCode.T_ACCOUNT_NOT_LOGIN.getMessage()
            );
        }
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(User.class);
        wrapper
                .set(User::getNickname, updateNickname.getNickname())
                .eq(User::getId, user.getId());
        int row = userMapper.update(wrapper);
        if (row <= 0) {
            throw new BusinessException(ResultCode.R_BAD_REQUEST.getCode(), "用户不存在或未被修改");
        }

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
