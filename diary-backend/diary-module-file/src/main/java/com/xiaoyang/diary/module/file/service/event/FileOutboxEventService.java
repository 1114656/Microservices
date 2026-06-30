package com.xiaoyang.diary.module.file.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xiaoyang.diary.framework.mq.core.DomainEvent;
import com.xiaoyang.diary.framework.mq.core.DomainEventPublisher;
import com.xiaoyang.diary.module.file.dal.dataobject.FileOutboxEventDO;
import com.xiaoyang.diary.module.file.dal.mysql.FileOutboxEventMapper;
import com.xiaoyang.diary.module.file.mq.FileEventTags;
import com.xiaoyang.diary.module.file.mq.FileEventTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileOutboxEventService {

    private static final String FILE_UPLOADED = "file.uploaded";
    private static final int MAX_RETRY_COUNT = 5;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private final FileOutboxEventMapper fileOutboxEventMapper;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public void createFileUploadedEvent(Long fileId, Long ownerUserId, String originalName,
                                        String fileCategory, String traceId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("fileId", fileId);
        payload.put("ownerUserId", ownerUserId);
        payload.put("originalName", originalName);
        payload.put("fileCategory", fileCategory);
        DomainEvent event = DomainEvent.builder()
                .eventId(UUID.randomUUID().toString().replace("-", ""))
                .eventType(FILE_UPLOADED)
                .producer("diary-file-service")
                .sourceService("diary-file-service")
                .aggregateType("file")
                .aggregateId(String.valueOf(fileId))
                .traceId(traceId)
                .occurredAt(LocalDateTime.now())
                .payload(payload)
                .build();
        fileOutboxEventMapper.insert(FileOutboxEventDO.builder()
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .topic(FileEventTopics.FILE_EVENTS)
                .tag(FileEventTags.FILE_UPLOADED)
                .payload(writePayload(event))
                .status(0)
                .retryCount(0)
                .nextRetryTime(LocalDateTime.now())
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public int publishPending(int limit) {
        List<FileOutboxEventDO> events = fileOutboxEventMapper.selectPending(LocalDateTime.now(), limit);
        int published = 0;
        for (FileOutboxEventDO event : events) {
            try {
                domainEventPublisher.publish(destination(event), readPayload(event.getPayload()));
                fileOutboxEventMapper.markSent(event.getId(), LocalDateTime.now());
                published++;
            } catch (RuntimeException ex) {
                int retryCount = event.getRetryCount() + 1;
                int status = retryCount >= MAX_RETRY_COUNT ? 2 : 0;
                fileOutboxEventMapper.markFailure(event.getId(), retryCount,
                        LocalDateTime.now().plusSeconds(nextDelaySeconds(retryCount)), ex.getMessage(), status);
            }
        }
        return published;
    }

    private String destination(FileOutboxEventDO event) {
        return event.getTopic() + ":" + event.getTag();
    }

    private long nextDelaySeconds(int retryCount) {
        return Math.min(60L, 5L * retryCount);
    }

    private String writePayload(DomainEvent event) {
        try {
            return OBJECT_MAPPER.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Write outbox event payload failed", e);
        }
    }

    private DomainEvent readPayload(String payload) {
        try {
            Map<String, Object> values = OBJECT_MAPPER.readValue(payload, new TypeReference<>() {
            });
            return OBJECT_MAPPER.convertValue(values, DomainEvent.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Read outbox event payload failed", e);
        }
    }
}
