//package com.example.User_Service.security.jwt;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//@RequiredArgsConstructor
//public class JwtTokenProvider {
//    @Value("${jwt.secret}")
//    private String jwtPlainSecretKey;
//
//    private volatile SecretKey jwtSecretKey;
//
//    private SecretKey getSecretKey() {
//        if (jwtSecretKey == null) {
//            synchronized (this) {
//                if (jwtSecretKey == null) {
//                    jwtSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtPlainSecretKey));
//                }
//            }
//        }
//        return jwtSecretKey;
//    }
//
//    private static final long TOKEN_VALID_TIME = 1000 * 60L * 60L * 24L;  // 1일
//    private static final long REF_TOKEN_VALID_TIME = 1000 * 60L * 60L * 24L * 14L;  // 14일
//
//    public String generateAccessToken(Long userId) {
//        Date now = new Date();
//        Date expiration = new Date(now.getTime() + TOKEN_VALID_TIME);
//
//        Claims claims = Jwts.claims();
//        claims.put("userId", String.valueOf(userId));
//        claims.put("tokenType", "access");
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expiration)
//                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String generateRefreshToken(Long userId) {
//        Date now = new Date();
//        Date expiration = new Date(now.getTime() + REF_TOKEN_VALID_TIME);
//
//        Claims claims = Jwts.claims();
//        claims.put("userId", String.valueOf(userId));
//        claims.put("tokenType", "refresh");
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expiration)
//                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String getUserIdByToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSecretKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .get("userId", String.class);
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSecretKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}

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

    // 기본 토큰 만료시간 (초 단위)
    private static final int ACCESS_TOKEN_EXPIRES_IN_SECONDS = 60 * 60 * 24; // 1일
    private static final int REFRESH_TOKEN_EXPIRES_IN_SECONDS = 60 * 60 * 24 * 14; // 14일

    public String generateAccessToken(Long userId) {
        return generateToken(userId, null, false);
    }

    public String generateRefreshToken(Long userId) {
        return generateToken(userId, null, true);
    }

    // deviceType은 선택사항으로, 기본값 null로 둠
    public String generateToken(Long userId, String deviceType, boolean isRefreshToken) {
        int expiresIn = isRefreshToken ? REFRESH_TOKEN_EXPIRES_IN_SECONDS : ACCESS_TOKEN_EXPIRES_IN_SECONDS;
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiresIn * 1000L);

        Claims claims = Jwts.claims();
        claims.put("userId", String.valueOf(userId));
        if (deviceType != null) {
            claims.put("deviceType", deviceType);
        }
        claims.put("tokenType", isRefreshToken ? "refresh" : "access");

        return Jwts.builder()
                .setIssuer("user-service")
                .setSubject(String.valueOf(userId))
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .setHeaderParam("typ", "JWT")
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
