package org.lzmsecurity.jwt;


import cn.hutool.jwt.JWTUtil;
import lombok.Getter;
import org.lzmcommon.exception.BusinessException;
import org.lzmcommon.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
@Getter
public class JwtUtils {
    private static Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    @Value("${jwt.access-expiration}")
    private Long accessExpiration;
    @Value("${jwt.header}")
    private String header;
    @Value("${jwt.prefix}")
    private String prefix;

    // 获取 JWT 密钥
    private byte[] getSecretKey() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    // 生成 JWT 令牌
    private String generateToken(Map<String, Object> claims, Long expiration) {
        //设置过期时间
        long expSecond = expiration / 1000;
        claims.put("exp", expSecond);
        String token = JWTUtil.createToken(claims, getSecretKey());
        return token;
    }

    /**
     * 生成刷新令牌
     *
     * @param claims 令牌载荷，包含用户信息
     * @return 刷新令牌
     */
    public String generateRefreshToken(Map<String, Object> claims) {
        long expiration = System.currentTimeMillis() + refreshExpiration;
        return generateToken(claims, expiration);
    }

    /**
     * 生成访问令牌
     *
     * @param claims 令牌载荷，包含用户信息
     * @return 访问令牌
     */
    public String generateAccessToken(Map<String, Object> claims) {
        long expiration = System.currentTimeMillis() + accessExpiration;
        return generateToken(claims, expiration);
    }

    /**
     * 从令牌中获取载荷
     *
     * @param token JWT 令牌
     * @return 令牌载荷，包含用户信息
     */
    public Map<String, Object> getTokenClaims(String token) {
        try {
            if (!JWTUtil.verify(token, getSecretKey())) {
                logger.warn("令牌验证失败，token={}", token);
                return null;
            }
            return (Map<String, Object>) JWTUtil.parseToken(token).getPayload();
        } catch (Exception e) {
            logger.warn("从令牌中获取载荷失败，token={}", token);
            return null;
        }
    }

    /**
     * 判断token是否已经失效
     *
     * @param token JWT 令牌
     * @return 是否失效
     */
    public boolean isTokenExpired(String token) {
        try {
            Map<String, Object> claims = getTokenClaims(token);
            // 令牌验证失败，返回true
            if (claims == null) {
                return true;
            }
            long expiration = (Long) claims.get("exp") * 1000L;
            long currentTime = System.currentTimeMillis();
            // 令牌过期，返回true
            return currentTime > expiration;
        } catch (Exception e) {
            // 令牌解析异常，返回true
            logger.warn("令牌失效，token={}", token);
            return true;
        }
    }

    /**
     * 获取过期时间
     *
     * @param token JWT 令牌
     * @return 过期时间
     */

    public Date getTokenExpired(String token) {
        Map<String, Object> claims = getTokenClaims(token);
        if (claims == null) {
            return null;
        }
        long exp = ((Number) claims.get("exp")).longValue();
        return new Date(exp);
    }

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 刷新后的访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "刷新令牌不能为空");
        }
        // 校验refreshToken是否有效（签名+未过期）
        if (isTokenExpired(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "刷新令牌已失效，请重新登录");
        }
        Map<String, Object> claims = getTokenClaims(refreshToken);
        if (claims == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "刷新令牌解析失败");
        }
        // 生成新accessToken
        return generateAccessToken(claims);
    }

    /**
     * 刷新刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 刷新后的刷新令牌
     */

    public String refreshRefreshToken(String refreshToken) {
        Date refreshExpired = getTokenExpired(refreshToken);
        Map<String, Object> claims = getTokenClaims(refreshToken);
        if (Objects.isNull(refreshExpired) || Objects.isNull(claims)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "刷新令牌验证失败");
        }
        Date currentTime = new Date();
        // 当剩余时间不足20%时返回新refreshToken
        long remainingTime = refreshExpired.getTime() - currentTime.getTime();
        if (remainingTime > refreshExpiration * 0.2) {
            return refreshToken;
        }
        // 生成新refreshToken
        return generateRefreshToken(claims);
    }

    /**
     * 判断是否异地登录
     * 判断保存到http的accessToken和缓存中的accessToken是否相等
     *
     * @param httpToken  从请求头中获取的令牌
     * @param cacheToken 从缓存中获取的令牌
     * @return 是否异地登录
     */

    public boolean isRemoteLogin(String httpToken, String cacheToken) {
        if (!StringUtils.hasText(httpToken) || !StringUtils.hasText(cacheToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录过期");
        }

        return !Objects.equals(httpToken, cacheToken);
    }


}
