package com.xiaoyang.diary.framework.mq.core;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisMessageIdempotentServiceTest {

    @Test
    void shouldClaimOnlyWhenRedisKeyIsAbsent() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.setIfAbsent("mq:idempotent:event-1", "1", Duration.ofDays(7)))
                .thenReturn(Boolean.TRUE);
        MessageIdempotentService service = new RedisMessageIdempotentService(redisTemplate, Duration.ofDays(7));

        assertTrue(service.claim("event-1"));

        verify(operations).setIfAbsent("mq:idempotent:event-1", "1", Duration.ofDays(7));
    }

    @Test
    void shouldRejectDuplicateEvent() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.setIfAbsent("mq:idempotent:event-1", "1", Duration.ofDays(7)))
                .thenReturn(Boolean.FALSE);
        MessageIdempotentService service = new RedisMessageIdempotentService(redisTemplate, Duration.ofDays(7));

        assertFalse(service.claim("event-1"));
    }
}
