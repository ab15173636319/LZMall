package org.app.marketserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.app.marketserver.common.exception.BusinessException;
import org.app.marketserver.common.result.Result;
import org.app.marketserver.mapper.SysUserMapper;
import org.app.marketserver.pojo.dto.LoginDto;
import org.app.marketserver.pojo.dto.RegisterDto;
import org.app.marketserver.pojo.entity.SysUser;
import org.app.marketserver.security.LoginUser;
import org.app.marketserver.service.UserService;
import org.app.marketserver.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    private final String COOKIE_KEY = "refresh_token";

    @Override
    public void register(RegisterDto registerDto) {

        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, registerDto.getUsername())
                        .eq(SysUser::getDeleteFlag, 1)
        );

        if (user != null) {
            throw new BusinessException("用户名已存在");
        }
 
        SysUser sysUser = new SysUser();
        sysUser.setUsername(registerDto.getUsername());
        sysUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        sysUser.setStatus(1);
        sysUser.setDeleteFlag(1);
        sysUserMapper.insert(sysUser);


    }

    @Override
    public String login(LoginDto loginDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword()
                        )
                );
        LoginUser user = (LoginUser) authentication.getPrincipal();

        if (user == null) {
            throw new BusinessException("登录失败，用户名或密码错误666");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenService.createRefreshToken(user, refreshToken, loginDto.getDeviceInfo());

        Cookie cookie = new Cookie(COOKIE_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 开发环境不开启https
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtUtil.getRefreshTokenExpiration().intValue());
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);

        return accessToken;
    }

    @Override
    public String refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        LoginUser user = refreshTokenService.validateRefreshToken(refreshToken);

        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenService.createRefreshToken(user, newRefreshToken, request.getHeader("User-Agent"));

        Cookie cookie = new Cookie(COOKIE_KEY, newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(7 * 24 * 3600);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
        return newAccessToken;
    }

    @Override
    public void logout(LoginUser user) {
        if (user == null) {
            throw new BusinessException("用户未登录");
        }
        refreshTokenService.revokeUserId(user.getUserId());
    }
}
