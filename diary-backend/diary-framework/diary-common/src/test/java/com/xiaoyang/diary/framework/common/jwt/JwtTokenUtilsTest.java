package com.xiaoyang.diary.framework.common.jwt;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenUtilsTest {

    @Test
    void shouldCreateAndParseAccessToken() {
        LocalDateTime expiresTime = LocalDateTime.now().plusHours(2);
        JwtUserClaims claims = JwtUserClaims.builder()
                .userId(1001L)
                .userType(2)
                .userInfo(Map.of("nickname", "tester"))
                .scopes(List.of("diary:read", "blog:write"))
                .expiresTime(expiresTime)
                .build();

        String token = JwtTokenUtils.createToken("diary", "secret-for-test", claims);

        JwtUserClaims parsed = JwtTokenUtils.parseToken("diary", "secret-for-test", token);
        assertEquals(1001L, parsed.getUserId());
        assertEquals(2, parsed.getUserType());
        assertEquals("tester", parsed.getUserInfo().get("nickname"));
        assertEquals(List.of("diary:read", "blog:write"), parsed.getScopes());
        assertEquals(expiresTime.withNano(0), parsed.getExpiresTime());
    }

    @Test
    void shouldRejectTokenWhenSecretMismatch() {
        JwtUserClaims claims = JwtUserClaims.builder()
                .userId(1001L)
                .userType(2)
                .expiresTime(LocalDateTime.now().plusHours(2))
                .build();

        String token = JwtTokenUtils.createToken("diary", "secret-for-test", claims);

        assertThrows(RuntimeException.class, () -> JwtTokenUtils.parseToken("diary", "another-secret", token));
    }
}
