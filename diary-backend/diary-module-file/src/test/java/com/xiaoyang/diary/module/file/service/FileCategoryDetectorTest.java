package com.xiaoyang.diary.module.file.service;

import com.xiaoyang.diary.module.file.enums.FileCategoryEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileCategoryDetectorTest {

    private final FileCategoryDetector detector = new FileCategoryDetector();

    @Test
    void shouldDetectImageByContentType() {
        assertEquals(FileCategoryEnum.IMAGE, detector.detect("photo.bin", "image/png"));
    }

    @Test
    void shouldDetectAudioByContentType() {
        assertEquals(FileCategoryEnum.AUDIO, detector.detect("voice.bin", "audio/mpeg"));
    }

    @Test
    void shouldDetectVideoByContentType() {
        assertEquals(FileCategoryEnum.VIDEO, detector.detect("movie.bin", "video/mp4"));
    }

    @Test
    void shouldDetectTextByContentType() {
        assertEquals(FileCategoryEnum.TEXT, detector.detect("readme.bin", "text/plain"));
    }

    @Test
    void shouldDetectDocumentByExtensionFallback() {
        assertEquals(FileCategoryEnum.DOCUMENT, detector.detect("report.pdf", null));
    }

    @Test
    void shouldDetectArchiveByExtensionFallback() {
        assertEquals(FileCategoryEnum.ARCHIVE, detector.detect("backup.zip", null));
    }

    @Test
    void shouldUseOtherForUnknownType() {
        assertEquals(FileCategoryEnum.OTHER, detector.detect("blob.unknown", "application/octet-stream"));
    }
}
