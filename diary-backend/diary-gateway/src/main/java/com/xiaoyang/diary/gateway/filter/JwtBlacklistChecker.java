package com.xiaoyang.diary.gateway.filter;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface JwtBlacklistChecker {

    Mono<Boolean> isBlacklisted(String accessToken);

}
