package com.xiaoyang.diary.framework.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class JwtTokenUtils {

    private static final String CLAIM_USER_TYPE = "userType";
    private static final String CLAIM_USER_INFO = "userInfo";
    private static final String CLAIM_TENANT_ID = "tenantId";
    private static final String CLAIM_SCOPES = "scopes";

    private JwtTokenUtils() {
    }

    public static String createToken(String issuer, String secret, JwtUserClaims userClaims) {
        LocalDateTime expiresTime = userClaims.getExpiresTime().withNano(0);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(String.valueOf(userClaims.getUserId()))
                .issuedAt(new Date())
                .expiration(toDate(expiresTime))
                .claim(CLAIM_USER_TYPE, userClaims.getUserType())
                .claim(CLAIM_USER_INFO, userClaims.getUserInfo())
                .claim(CLAIM_TENANT_ID, userClaims.getTenantId())
                .claim(CLAIM_SCOPES, userClaims.getScopes())
                .signWith(buildKey(secret))
                .compact();
    }

    public static JwtUserClaims parseToken(String issuer, String secret, String token) {
        Claims claims = Jwts.parser()
                .verifyWith(buildKey(secret))
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return JwtUserClaims.builder()
                .userId(Long.valueOf(claims.getSubject()))
                .userType(toInteger(claims.get(CLAIM_USER_TYPE)))
                .userInfo(toStringMap(claims.get(CLAIM_USER_INFO)))
                .tenantId(toLong(claims.get(CLAIM_TENANT_ID)))
                .scopes(toStringList(claims.get(CLAIM_SCOPES)))
                .expiresTime(toLocalDateTime(claims.getExpiration()))
                .build();
    }

    private static SecretKey buildKey(String secret) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).withNano(0);
    }

    private static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer integer) {
            return integer;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(value.toString());
    }

    private static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> toStringMap(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return null;
        }
        Map<String, String> result = new LinkedHashMap<>();
        map.forEach((key, mapValue) -> result.put(String.valueOf(key), mapValue == null ? null : String.valueOf(mapValue)));
        return result;
    }

    @SuppressWarnings("unchecked")
    private static List<String> toStringList(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of(value.toString());
    }
}
