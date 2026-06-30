package com.xiaoyang.diary.module.file.service.event;

import com.xiaoyang.diary.framework.mq.core.DomainEventPublisher;
import com.xiaoyang.diary.module.file.dal.dataobject.FileOutboxEventDO;
import com.xiaoyang.diary.module.file.dal.mysql.FileOutboxEventMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileOutboxEventServiceTest {

    @Test
    void shouldCreatePendingEventForFileUploaded() {
        FileOutboxEventMapper mapper = mock(FileOutboxEventMapper.class);
        DomainEventPublisher publisher = mock(DomainEventPublisher.class);
        FileOutboxEventService service = new FileOutboxEventService(mapper, publisher);

        service.createFileUploadedEvent(10L, 1L, "photo.png", "image", "trace-1");

        ArgumentCaptor<FileOutboxEventDO> captor = ArgumentCaptor.forClass(FileOutboxEventDO.class);
        verify(mapper).insert(captor.capture());
        FileOutboxEventDO event = captor.getValue();
        assertNotNull(event.getEventId());
        assertEquals("file.uploaded", event.getEventType());
        assertEquals("diary-file-events", event.getTopic());
        assertEquals("file-uploaded", event.getTag());
        assertEquals(0, event.getStatus());
        assertEquals(0, event.getRetryCount());
    }

    @Test
    void shouldMarkEventSentAfterPublish() {
        FileOutboxEventMapper mapper = mock(FileOutboxEventMapper.class);
        DomainEventPublisher publisher = mock(DomainEventPublisher.class);
        FileOutboxEventService service = new FileOutboxEventService(mapper, publisher);
        FileOutboxEventDO event = FileOutboxEventDO.builder()
                .id(1L)
                .eventId("event-1")
                .eventType("file.uploaded")
                .topic("diary-file-events")
                .tag("file-uploaded")
                .payload("{}")
                .status(0)
                .retryCount(0)
                .build();
        when(mapper.selectPending(any(LocalDateTime.class), eq(10))).thenReturn(List.of(event));

        int published = service.publishPending(10);

        assertEquals(1, published);
        verify(publisher).publish(eq("diary-file-events:file-uploaded"), any());
        verify(mapper).markSent(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void shouldScheduleRetryWhenPublishFails() {
        FileOutboxEventMapper mapper = mock(FileOutboxEventMapper.class);
        DomainEventPublisher publisher = mock(DomainEventPublisher.class);
        FileOutboxEventService service = new FileOutboxEventService(mapper, publisher);
        FileOutboxEventDO event = FileOutboxEventDO.builder()
                .id(1L)
                .eventId("event-1")
                .eventType("file.uploaded")
                .topic("diary-file-events")
                .tag("file-uploaded")
                .payload("{}")
                .status(0)
                .retryCount(1)
                .build();
        when(mapper.selectPending(any(LocalDateTime.class), eq(10))).thenReturn(List.of(event));
        doThrow(new IllegalStateException("mq down")).when(publisher).publish(eq("diary-file-events:file-uploaded"), any());

        int published = service.publishPending(10);

        assertEquals(0, published);
        verify(mapper).markFailure(eq(1L), eq(2), any(LocalDateTime.class), eq("mq down"), eq(0));
    }
}
