package com.example.User_Service.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtPlainSecretKey;

    private volatile SecretKey jwtSecretKey;

    private SecretKey getSecretKey() {
        if (jwtSecretKey == null) {
            synchronized (this) {
                if (jwtSecretKey == null) {
                    jwtSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtPlainSecretKey));
                }
            }
        }
        return jwtSecretKey;
    }

    private static final long TOKEN_VALID_TIME = 1000 * 60L * 60L * 24L;  // 1일
    private static final long REF_TOKEN_VALID_TIME = 1000 * 60L * 60L * 24L * 14L;  // 14일

    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TOKEN_VALID_TIME);

        Claims claims = Jwts.claims();
        claims.put("userId", String.valueOf(userId));
        claims.put("tokenType", "access");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REF_TOKEN_VALID_TIME);

        Claims claims = Jwts.claims();
        claims.put("userId", String.valueOf(userId));
        claims.put("tokenType", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
