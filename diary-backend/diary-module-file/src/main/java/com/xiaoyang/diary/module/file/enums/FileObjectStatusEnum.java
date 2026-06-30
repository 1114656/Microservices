package com.xiaoyang.diary.module.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileObjectStatusEnum {

    NORMAL(0),
    DELETED(1);

    private final Integer status;

}
