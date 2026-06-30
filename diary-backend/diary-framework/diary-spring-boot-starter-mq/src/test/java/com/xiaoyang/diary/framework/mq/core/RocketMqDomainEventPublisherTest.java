package com.xiaoyang.diary.framework.mq.core;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RocketMqDomainEventPublisherTest {

    @Test
    void shouldSendDomainEventToDestination() {
        RocketMQTemplate rocketMQTemplate = mock(RocketMQTemplate.class);
        DomainEventPublisher publisher = new RocketMqDomainEventPublisher(rocketMQTemplate);
        DomainEvent event = DomainEvent.builder()
                .eventId("event-1")
                .eventType("file.uploaded")
                .producer("diary-file-service")
                .sourceService("diary-file-service")
                .aggregateType("file")
                .aggregateId("100")
                .traceId("trace-1")
                .occurredAt(LocalDateTime.of(2026, 6, 30, 12, 0))
                .payload(Map.of("fileId", 100L))
                .build();

        publisher.publish("diary-file-events:file-uploaded", event);

        verify(rocketMQTemplate).convertAndSend("diary-file-events:file-uploaded", event);
    }
}
