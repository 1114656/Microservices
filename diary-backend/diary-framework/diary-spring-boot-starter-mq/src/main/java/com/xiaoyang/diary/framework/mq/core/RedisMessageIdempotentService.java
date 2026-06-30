package com.xiaoyang.diary.framework.mq.core;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@RequiredArgsConstructor
public class RedisMessageIdempotentService implements MessageIdempotentService {

    private static final String IDEMPOTENT_KEY = "mq:idempotent:%s";

    private final StringRedisTemplate redisTemplate;
    private final Duration ttl;

    @Override
    public boolean claim(String eventId) {
        Boolean claimed = redisTemplate.opsForValue().setIfAbsent(formatKey(eventId), "1", ttl);
        return Boolean.TRUE.equals(claimed);
    }

    private String formatKey(String eventId) {
        return String.format(IDEMPOTENT_KEY, eventId);
    }
}
