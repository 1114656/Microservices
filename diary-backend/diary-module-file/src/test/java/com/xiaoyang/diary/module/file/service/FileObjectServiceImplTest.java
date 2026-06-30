package com.xiaoyang.diary.module.file.service;

import com.xiaoyang.diary.framework.common.exception.ServiceException;
import com.xiaoyang.diary.framework.test.core.ut.BaseMockitoUnitTest;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileUploadReqVO;
import com.xiaoyang.diary.module.file.dal.dataobject.FileObjectDO;
import com.xiaoyang.diary.module.file.dal.mysql.FileObjectMapper;
import com.xiaoyang.diary.module.file.enums.FileCategoryEnum;
import com.xiaoyang.diary.module.file.framework.config.FileStorageProperties;
import com.xiaoyang.diary.module.file.service.event.FileOutboxEventService;
import com.xiaoyang.diary.module.file.service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FileObjectServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private FileObjectMapper fileObjectMapper;
    @Mock
    private FileOutboxEventService fileOutboxEventService;

    private FileObjectServiceImpl fileObjectService;

    @BeforeEach
    void setUp() {
        FileStorageProperties properties = new FileStorageProperties();
        properties.getS3().setBucket("diary-file");
        fileObjectService = new FileObjectServiceImpl(fileStorageService, fileObjectMapper,
                new FileCategoryDetector(), properties, fileOutboxEventService);
    }

    @Test
    void shouldUploadAudioAndSaveMetadata() {
        byte[] content = new byte[]{1, 2, 3, 4};
        MockMultipartFile file = new MockMultipartFile("file", "voice.mp3", "audio/mpeg", content);
        FileUploadReqVO reqVO = new FileUploadReqVO();
        reqVO.setBusinessType("diary");
        reqVO.setBusinessId("100");
        when(fileObjectMapper.insert(any(FileObjectDO.class))).thenAnswer(invocation -> {
            FileObjectDO fileObject = invocation.getArgument(0);
            fileObject.setId(10L);
            return 1;
        });

        FileObjectDO result = fileObjectService.upload(file, reqVO, 1L);

        ArgumentCaptor<String> objectKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> contentCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(fileStorageService).putObject(objectKeyCaptor.capture(), eq("audio/mpeg"), contentCaptor.capture());
        assertTrue(objectKeyCaptor.getValue().startsWith("audio/"));
        assertTrue(objectKeyCaptor.getValue().endsWith(".mp3"));
        assertArrayEquals(content, contentCaptor.getValue());

        ArgumentCaptor<FileObjectDO> metadataCaptor = ArgumentCaptor.forClass(FileObjectDO.class);
        verify(fileObjectMapper).insert(metadataCaptor.capture());
        FileObjectDO metadata = metadataCaptor.getValue();
        assertEquals("diary-file", metadata.getBucket());
        assertEquals("voice.mp3", metadata.getOriginalName());
        assertEquals("mp3", metadata.getExtension());
        assertEquals(FileCategoryEnum.AUDIO.getCategory(), metadata.getFileCategory());
        assertEquals(4L, metadata.getSize());
        assertEquals(64, metadata.getSha256().length());
        assertEquals(1L, metadata.getOwnerUserId());
        assertEquals("diary", metadata.getBusinessType());
        assertEquals("100", metadata.getBusinessId());
        assertTrue(metadata.getPreviewSupported());
        assertEquals(10L, result.getId());
        verify(fileOutboxEventService).createFileUploadedEvent(10L, 1L, "voice.mp3", "audio", null);
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        assertThrows(ServiceException.class, () -> fileObjectService.upload(file, new FileUploadReqVO(), 1L));

        verifyNoInteractions(fileStorageService, fileObjectMapper);
    }

    @Test
    void shouldCreatePreviewUrlForOwner() {
        FileObjectDO fileObject = FileObjectDO.builder()
                .id(10L)
                .objectKey("audio/2026/06/13/id.mp3")
                .ownerUserId(1L)
                .previewSupported(true)
                .build();
        when(fileObjectMapper.selectById(10L)).thenReturn(fileObject);
        when(fileStorageService.createPreviewUrl(eq("audio/2026/06/13/id.mp3"), eq(Duration.ofMinutes(30))))
                .thenReturn("http://localhost:9000/diary-file/audio/2026/06/13/id.mp3");

        String previewUrl = fileObjectService.createPreviewUrl(10L, 1L);

        assertEquals("http://localhost:9000/diary-file/audio/2026/06/13/id.mp3", previewUrl);
    }

    @Test
    void shouldRejectPreviewForOtherUser() {
        FileObjectDO fileObject = FileObjectDO.builder()
                .id(10L)
                .objectKey("audio/2026/06/13/id.mp3")
                .ownerUserId(1L)
                .previewSupported(true)
                .build();
        when(fileObjectMapper.selectById(10L)).thenReturn(fileObject);

        assertThrows(ServiceException.class, () -> fileObjectService.createPreviewUrl(10L, 2L));

        verifyNoInteractions(fileStorageService);
        assertNotNull(fileObject);
    }
}
