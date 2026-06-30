package com.xiaoyang.diary.gateway.filter;

import com.xiaoyang.diary.framework.common.jwt.JwtTokenUtils;
import com.xiaoyang.diary.framework.common.jwt.JwtUserClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_PARAMETER = "token";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_ROLES = "X-Roles";

    private static final List<String> PERMIT_ALL_PATHS = List.of(
            "/admin-api/system/auth/login",
            "/admin-api/system/auth/register",
            "/admin-api/system/auth/refresh-token",
            "/admin-api/system/site/config/public"
    );

    private final String jwtIssuer;
    private final String jwtSecret;
    private final JwtBlacklistChecker blacklistChecker;

    public JwtAuthenticationGatewayFilter(@Value("${diary.jwt.issuer:diary}") String jwtIssuer,
                                          @Value("${diary.jwt.secret:diary-local-development-secret-change-me}") String jwtSecret,
                                          JwtBlacklistChecker blacklistChecker) {
        this.jwtIssuer = jwtIssuer;
        this.jwtSecret = jwtSecret;
        this.blacklistChecker = blacklistChecker;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isPermitAll(path)) {
            return chain.filter(exchange);
        }

        String token = obtainToken(exchange);
        if (token == null || token.isBlank()) {
            return unauthorized(exchange, "Missing access token");
        }

        JwtUserClaims userClaims;
        try {
            userClaims = JwtTokenUtils.parseToken(jwtIssuer, jwtSecret, token);
        } catch (RuntimeException ex) {
            log.debug("JWT validation failed for path {}", path, ex);
            return unauthorized(exchange, "Invalid access token");
        }

        return blacklistChecker.isBlacklisted(token)
                .flatMap(blacklisted -> {
                    if (Boolean.TRUE.equals(blacklisted)) {
                        return unauthorized(exchange, "Access token has been revoked");
                    }
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(builder -> builder.headers(headers -> setUserHeaders(headers, userClaims)))
                            .build();
                    return chain.filter(mutatedExchange);
                });
    }

    @Override
    public int getOrder() {
        return -190;
    }

    private static boolean isPermitAll(String path) {
        return path.startsWith("/actuator") || PERMIT_ALL_PATHS.contains(path);
    }

    private static String obtainToken(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return exchange.getRequest().getQueryParams().getFirst(TOKEN_PARAMETER);
    }

    private static void setUserHeaders(HttpHeaders headers, JwtUserClaims userClaims) {
        headers.remove(HEADER_USER_ID);
        headers.remove(HEADER_USER_TYPE);
        headers.remove(HEADER_USERNAME);
        headers.remove(HEADER_ROLES);
        headers.set(HEADER_USER_ID, String.valueOf(userClaims.getUserId()));
        headers.set(HEADER_USER_TYPE, String.valueOf(userClaims.getUserType()));
        headers.set(HEADER_USERNAME, getUsername(userClaims.getUserInfo()));
        headers.set(HEADER_ROLES, join(userClaims.getScopes()));
    }

    private static String getUsername(Map<String, String> userInfo) {
        return userInfo == null ? "" : userInfo.getOrDefault("username", "");
    }

    private static String join(List<String> values) {
        return values == null ? "" : String.join(",", values);
    }

    private static Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"code\":401,\"data\":null,\"msg\":\"" + message + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
