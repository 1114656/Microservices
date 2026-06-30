package com.xiaoyang.diary.module.file.service.storage;

import com.xiaoyang.diary.module.file.framework.config.FileStorageProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3FileStorageServiceTest {

    @Test
    void shouldPutObjectWithMetadata() {
        S3Client s3Client = mock(S3Client.class);
        S3Presigner presigner = mock(S3Presigner.class);
        FileStorageProperties properties = newProperties();
        S3FileStorageService storageService = new S3FileStorageService(s3Client, presigner, properties);

        storageService.putObject("diary/1/voice.mp3", "audio/mpeg", new byte[]{1, 2, 3});

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        PutObjectRequest request = requestCaptor.getValue();
        assertEquals("diary-file", request.bucket());
        assertEquals("diary/1/voice.mp3", request.key());
        assertEquals("audio/mpeg", request.contentType());
        assertEquals(3L, request.contentLength());
    }

    @Test
    void shouldCreatePreviewUrlWithConfiguredExpiry() throws Exception {
        S3Client s3Client = mock(S3Client.class);
        S3Presigner presigner = mock(S3Presigner.class);
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL("http://localhost:9000/diary-file/diary/1/voice.mp3"));
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);
        FileStorageProperties properties = newProperties();
        S3FileStorageService storageService = new S3FileStorageService(s3Client, presigner, properties);

        String previewUrl = storageService.createPreviewUrl("diary/1/voice.mp3", Duration.ofMinutes(5));

        assertEquals("http://localhost:9000/diary-file/diary/1/voice.mp3", previewUrl);
        ArgumentCaptor<GetObjectPresignRequest> requestCaptor =
                ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(presigner).presignGetObject(requestCaptor.capture());
        assertEquals(Duration.ofMinutes(5), requestCaptor.getValue().signatureDuration());
        assertEquals("diary-file", requestCaptor.getValue().getObjectRequest().bucket());
        assertEquals("diary/1/voice.mp3", requestCaptor.getValue().getObjectRequest().key());
    }

    private FileStorageProperties newProperties() {
        FileStorageProperties properties = new FileStorageProperties();
        FileStorageProperties.S3 s3 = new FileStorageProperties.S3();
        s3.setBucket("diary-file");
        properties.setS3(s3);
        return properties;
    }
}
