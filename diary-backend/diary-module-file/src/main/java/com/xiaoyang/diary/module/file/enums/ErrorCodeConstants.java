package com.xiaoyang.diary.module.file.enums;

import com.xiaoyang.diary.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode FILE_EMPTY = new ErrorCode(1_004_000_000, "上传文件不能为空");
    ErrorCode FILE_UPLOAD_FAIL = new ErrorCode(1_004_000_001, "文件上传失败");
    ErrorCode FILE_NOT_EXISTS = new ErrorCode(1_004_000_002, "文件不存在");
    ErrorCode FILE_ACCESS_DENIED = new ErrorCode(1_004_000_003, "无权访问该文件");
    ErrorCode FILE_PREVIEW_NOT_SUPPORTED = new ErrorCode(1_004_000_004, "该文件暂不支持预览");

}
