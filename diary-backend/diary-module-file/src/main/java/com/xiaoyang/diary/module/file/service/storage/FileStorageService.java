package com.xiaoyang.diary.module.file.service.storage;

import java.time.Duration;

public interface FileStorageService {

    void putObject(String objectKey, String contentType, byte[] content);

    String createPreviewUrl(String objectKey, Duration expire);

    void deleteObject(String objectKey);
}
