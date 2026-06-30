package com.xiaoyang.diary.framework.mq.core;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@RequiredArgsConstructor
public class RocketMqDomainEventPublisher implements DomainEventPublisher {

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void publish(String destination, DomainEvent event) {
        rocketMQTemplate.convertAndSend(destination, event);
    }
}
