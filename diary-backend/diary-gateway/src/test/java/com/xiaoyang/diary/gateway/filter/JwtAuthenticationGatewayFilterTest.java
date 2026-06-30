package com.xiaoyang.diary.gateway.filter;

import com.xiaoyang.diary.framework.common.jwt.JwtTokenUtils;
import com.xiaoyang.diary.framework.common.jwt.JwtUserClaims;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAuthenticationGatewayFilterTest {

    private static final String ISSUER = "diary";
    private static final String SECRET = "secret-for-test";

    @Test
    void shouldPermitLoginWithoutToken() {
        JwtAuthenticationGatewayFilter filter = new JwtAuthenticationGatewayFilter(ISSUER, SECRET, token -> Mono.just(false));
        AtomicBoolean called = new AtomicBoolean(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/admin-api/system/auth/login"));

        StepVerifier.create(filter.filter(exchange, markCalled(called))).verifyComplete();

        assertTrue(called.get());
    }

    @Test
    void shouldValidateTokenAndPropagateUserContext() {
        JwtAuthenticationGatewayFilter filter = new JwtAuthenticationGatewayFilter(ISSUER, SECRET, token -> Mono.just(false));
        String token = createToken();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/admin-api/diary/page")
                        .header("Authorization", "Bearer " + token)
                        .header("X-User-Id", "spoof"));

        GatewayFilterChain chain = mutatedExchange -> {
            assertEquals("1001", mutatedExchange.getRequest().getHeaders().getFirst("X-User-Id"));
            assertEquals("2", mutatedExchange.getRequest().getHeaders().getFirst("X-User-Type"));
            assertEquals("tester", mutatedExchange.getRequest().getHeaders().getFirst("X-Username"));
            assertEquals("diary:read,blog:write", mutatedExchange.getRequest().getHeaders().getFirst("X-Roles"));
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        assertFalse(exchange.getResponse().isCommitted());
    }

    @Test
    void shouldRejectBlacklistedToken() {
        JwtAuthenticationGatewayFilter filter = new JwtAuthenticationGatewayFilter(ISSUER, SECRET, token -> Mono.just(true));
        AtomicBoolean called = new AtomicBoolean(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/admin-api/diary/page")
                        .header("Authorization", "Bearer " + createToken()));

        StepVerifier.create(filter.filter(exchange, markCalled(called))).verifyComplete();

        assertFalse(called.get());
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldRejectMissingToken() {
        JwtAuthenticationGatewayFilter filter = new JwtAuthenticationGatewayFilter(ISSUER, SECRET, token -> Mono.just(false));
        AtomicBoolean called = new AtomicBoolean(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/admin-api/diary/page"));

        StepVerifier.create(filter.filter(exchange, markCalled(called))).verifyComplete();

        assertFalse(called.get());
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    private static GatewayFilterChain markCalled(AtomicBoolean called) {
        return exchange -> {
            called.set(true);
            return Mono.empty();
        };
    }

    private static String createToken() {
        return JwtTokenUtils.createToken(ISSUER, SECRET, JwtUserClaims.builder()
                .userId(1001L)
                .userType(2)
                .userInfo(Map.of("username", "tester"))
                .scopes(List.of("diary:read", "blog:write"))
                .expiresTime(LocalDateTime.now().plusHours(2))
                .build());
    }
}
