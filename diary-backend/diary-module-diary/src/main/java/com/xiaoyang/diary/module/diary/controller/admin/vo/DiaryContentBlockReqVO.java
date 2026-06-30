package com.xiaoyang.diary.module.diary.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 日记正文块 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryContentBlockReqVO {

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_FILE = "file";

    @Schema(description = "块类型：text、file")
    private String type;

    @Schema(description = "文本内容，仅 type=text 时使用")
    private String content;

    @Schema(description = "文件编号，仅 type=file 时使用")
    private Long fileId;

    public static DiaryContentBlockReqVO text(String content) {
        return new DiaryContentBlockReqVO(TYPE_TEXT, content, null);
    }

    public static DiaryContentBlockReqVO file(Long fileId) {
        return new DiaryContentBlockReqVO(TYPE_FILE, null, fileId);
    }
}
