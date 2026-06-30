package com.xiaoyang.diary.framework.mq.core;

public interface DomainEventPublisher {

    void publish(String destination, DomainEvent event);

}
