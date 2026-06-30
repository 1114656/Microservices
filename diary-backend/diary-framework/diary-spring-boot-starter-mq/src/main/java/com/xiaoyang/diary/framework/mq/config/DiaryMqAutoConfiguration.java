package com.xiaoyang.diary.framework.mq.config;

import com.xiaoyang.diary.framework.mq.core.DomainEventPublisher;
import com.xiaoyang.diary.framework.mq.core.MessageIdempotentService;
import com.xiaoyang.diary.framework.mq.core.RedisMessageIdempotentService;
import com.xiaoyang.diary.framework.mq.core.RocketMqDomainEventPublisher;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
@EnableConfigurationProperties(DiaryMqProperties.class)
public class DiaryMqAutoConfiguration {

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    @ConditionalOnMissingBean
    public DomainEventPublisher domainEventPublisher(RocketMQTemplate rocketMQTemplate) {
        return new RocketMqDomainEventPublisher(rocketMQTemplate);
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean
    public MessageIdempotentService messageIdempotentService(StringRedisTemplate redisTemplate,
                                                             DiaryMqProperties properties) {
        return new RedisMessageIdempotentService(redisTemplate, properties.getIdempotentTtl());
    }
}
