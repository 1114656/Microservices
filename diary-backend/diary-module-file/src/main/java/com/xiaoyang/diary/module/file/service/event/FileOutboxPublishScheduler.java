package com.xiaoyang.diary.module.file.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "diary.mq.outbox", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FileOutboxPublishScheduler {

    private final FileOutboxEventService fileOutboxEventService;

    @Scheduled(fixedDelayString = "${diary.mq.outbox.fixed-delay:5000}")
    public void publishPending() {
        int published = fileOutboxEventService.publishPending(50);
        if (published > 0) {
            log.info("[publishPending][published={}]", published);
        }
    }
}
