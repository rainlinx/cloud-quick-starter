package com.xdashen.gateway.util;

import com.xdashen.gateway.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

public class TokenUtils {

    public static String create(Token token, String key, long expiration) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setId(UUID.randomUUID().toString())
                .setIssuer("xDaShen")
                .setIssuedAt(new Date())
                .setSubject(token.getUserName())
                .setExpiration(new Date(LocalDateTime.now().plusMinutes(expiration).toEpochSecond(ZoneOffset.ofHours(8)) * 1000))
                .claim("userName", token.getUserName())
                .compact();
    }

    public static String create(Claims claims, String key, long expiration) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setId(UUID.randomUUID().toString())
                .setIssuer(claims.getIssuer())
                .setIssuedAt(new Date())
                .setSubject(claims.getSubject())
                .setExpiration(new Date(LocalDateTime.now().plusMinutes(expiration).toEpochSecond(ZoneOffset.ofHours(8)) * 1000))
                .claim("userName", claims.get("userName"))
                .compact();
    }

    public static boolean verify(String tokenString, String key) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(tokenString).getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static Token getToken(String tokenString, String key) {
        if (!verify(tokenString, key)) {
            throw new RuntimeException("token已过期");
        }
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(tokenString).getBody();
        Token tokenInfo = new Token();
        tokenInfo.setUserName(String.valueOf(claims.get("userName")));
        return tokenInfo;
    }

    public static String update(String tokenString, String key, long expiration) {
        final Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(tokenString)
                .getBody();
        return create(claims, key, expiration);
    }
}