package com.xiaoyang.diary.module.file.mq;

import com.xiaoyang.diary.framework.mq.core.DomainEvent;
import com.xiaoyang.diary.framework.mq.core.MessageIdempotentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "diary.mq.consumer.file-uploaded", name = "enabled", havingValue = "true", matchIfMissing = true)
@RocketMQMessageListener(topic = FileEventTopics.FILE_EVENTS,
        consumerGroup = "${rocketmq.consumer.group:diary-file-consumer}",
        selectorExpression = FileEventTags.FILE_UPLOADED)
public class FileUploadedEventConsumer implements RocketMQListener<DomainEvent> {

    private final MessageIdempotentService messageIdempotentService;

    @Override
    public void onMessage(DomainEvent event) {
        if (!messageIdempotentService.claim(event.getEventId())) {
            log.info("[onMessage][eventId={} duplicate skipped]", event.getEventId());
            return;
        }
        log.info("[onMessage][eventId={} eventType={} aggregateId={} traceId={}]",
                event.getEventId(), event.getEventType(), event.getAggregateId(), event.getTraceId());
    }
}
