package org.app.marketserver.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.app.marketserver.mapper.SysRefreshMapper;
import org.app.marketserver.pojo.entity.SysRefreshToken;
import org.app.marketserver.security.LoginUser;
import org.app.marketserver.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final SysRefreshMapper sysRefreshMapper;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private String hash(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("哈希token失败", e);
        }
    }

    public SysRefreshToken createRefreshToken(LoginUser user, String refreshToken, String deviceInfo) {
        SysRefreshToken sysRefreshToken = new SysRefreshToken();
        sysRefreshToken.setUser_id(user.getUserId());
        sysRefreshToken.setToken_hash(hash(refreshToken));
        sysRefreshToken.setDevice_id(deviceInfo);
        sysRefreshToken.setRevoked(0);
        sysRefreshToken.setExpire_time(
                new DateTime(jwtUtil.parse(refreshToken).getExpiration().getTime())
        );
        sysRefreshMapper.insert(sysRefreshToken);
        return sysRefreshToken;
    }

    public LoginUser validateRefreshToken(String refreshToken) {
        Claims claims = jwtUtil.parse(refreshToken);

        if (!"refresh".equals(claims.get("type"))) {
            throw new BadCredentialsException("刷新token类型错误");
        }

        Long userId = claims.get("userId", Long.class);
        String tokenHash = hash(refreshToken);

        SysRefreshToken record = sysRefreshMapper.selectOne(
                new LambdaQueryWrapper<SysRefreshToken>()
                        .eq(SysRefreshToken::getToken_hash, tokenHash)
                        .eq(SysRefreshToken::getRevoked, 0)
        );

        if (record == null || record.getExpire_time().before(DateTime.now())) {
            throw new BadCredentialsException("刷新令牌失效");
        }

        record.setRevoked(1);

        sysRefreshMapper.updateById(record);

        return (LoginUser) userDetailsService.loadUserByUsername(claims.getSubject());
    }

    public void revokeUserId(Long userId) {
        sysRefreshMapper.update(null, new LambdaQueryWrapper<SysRefreshToken>()
                .eq(SysRefreshToken::getRevoked, 1)
                .eq(SysRefreshToken::getUser_id, userId)
        );
    }


}
