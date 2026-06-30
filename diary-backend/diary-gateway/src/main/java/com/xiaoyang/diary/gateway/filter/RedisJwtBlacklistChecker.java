package com.xiaoyang.diary.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RedisJwtBlacklistChecker implements JwtBlacklistChecker {

    private static final String JWT_ACCESS_TOKEN_BLACKLIST = "jwt:blacklist:%s";

    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Boolean> isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(String.format(JWT_ACCESS_TOKEN_BLACKLIST, accessToken));
    }
}
