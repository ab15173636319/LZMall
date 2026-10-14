package org.app.marketserver.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.app.marketserver.security.LoginUser;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtUtil {

    private String secret;
    private Long refreshTokenExpiration;
    private Long accessTokenExpiration;
    private String refreshTokenKey;
    private String refreshTokenBlackKey;
    private String cookieKey;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", ((LoginUser) user).getUserId())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + getAccessTokenExpiration()))
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", ((LoginUser) user).getUserId())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + getRefreshTokenExpiration()))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
