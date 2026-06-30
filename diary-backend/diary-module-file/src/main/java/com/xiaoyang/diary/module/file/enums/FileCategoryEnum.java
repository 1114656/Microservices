package com.xiaoyang.diary.module.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileCategoryEnum {

    IMAGE("image"),
    AUDIO("audio"),
    VIDEO("video"),
    TEXT("text"),
    DOCUMENT("document"),
    ARCHIVE("archive"),
    OTHER("other");

    private final String category;

}
