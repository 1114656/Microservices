package com.xiaoyang.diary.module.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileVisibilityEnum {

    PRIVATE("private");

    private final String visibility;

}
