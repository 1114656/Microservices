package com.xiaoyang.diary.framework.mq.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEvent {

    private String eventId;
    private String eventType;
    private String producer;
    private String sourceService;
    private String aggregateType;
    private String aggregateId;
    private String traceId;
    private LocalDateTime occurredAt;
    private Map<String, Object> payload;

}
