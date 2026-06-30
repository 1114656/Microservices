package com.xiaoyang.diary.module.diary.enums;

import com.xiaoyang.diary.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode DIARY_NOT_EXISTS = new ErrorCode(1_005_000_000, "Diary does not exist");
    ErrorCode DIARY_CATEGORY_NOT_EXISTS = new ErrorCode(1_005_000_001, "Diary category does not exist");
    ErrorCode DIARY_CATEGORY_HAS_ENTRIES = new ErrorCode(1_005_000_002, "Diary category has diary entries");

}
