package com.xiaoyang.diary.module.file.service;

import cn.hutool.core.util.StrUtil;
import com.xiaoyang.diary.module.file.enums.FileCategoryEnum;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class FileCategoryDetector {

    private static final Map<FileCategoryEnum, Set<String>> EXTENSIONS = Map.of(
            FileCategoryEnum.IMAGE, Set.of("jpg", "jpeg", "png", "gif", "webp", "svg", "bmp"),
            FileCategoryEnum.AUDIO, Set.of("mp3", "wav", "m4a", "aac", "ogg", "flac", "opus"),
            FileCategoryEnum.VIDEO, Set.of("mp4", "webm", "mov", "m4v", "avi", "mkv"),
            FileCategoryEnum.TEXT, Set.of("txt", "md", "json", "csv", "log", "xml", "yaml", "yml"),
            FileCategoryEnum.DOCUMENT, Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"),
            FileCategoryEnum.ARCHIVE, Set.of("zip", "rar", "7z", "tar", "gz")
    );

    public FileCategoryEnum detect(String filename, String contentType) {
        FileCategoryEnum category = detectByContentType(contentType);
        if (category != null) {
            return category;
        }
        return detectByExtension(filename);
    }

    private FileCategoryEnum detectByContentType(String contentType) {
        if (StrUtil.isBlank(contentType)) {
            return null;
        }
        String normalizedContentType = contentType.toLowerCase();
        if (normalizedContentType.startsWith("image/")) {
            return FileCategoryEnum.IMAGE;
        }
        if (normalizedContentType.startsWith("audio/")) {
            return FileCategoryEnum.AUDIO;
        }
        if (normalizedContentType.startsWith("video/")) {
            return FileCategoryEnum.VIDEO;
        }
        if (normalizedContentType.startsWith("text/")) {
            return FileCategoryEnum.TEXT;
        }
        if (Set.of("application/json", "application/xml", "application/yaml").contains(normalizedContentType)) {
            return FileCategoryEnum.TEXT;
        }
        if (Set.of(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        ).contains(normalizedContentType)) {
            return FileCategoryEnum.DOCUMENT;
        }
        if (Set.of("application/zip", "application/x-rar-compressed", "application/x-7z-compressed",
                "application/gzip", "application/x-tar").contains(normalizedContentType)) {
            return FileCategoryEnum.ARCHIVE;
        }
        return null;
    }

    private FileCategoryEnum detectByExtension(String filename) {
        if (StrUtil.isBlank(filename) || !filename.contains(".")) {
            return FileCategoryEnum.OTHER;
        }
        String extension = StrUtil.subAfter(filename, '.', true).toLowerCase();
        return EXTENSIONS.entrySet().stream()
                .filter(entry -> entry.getValue().contains(extension))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(FileCategoryEnum.OTHER);
    }
}
