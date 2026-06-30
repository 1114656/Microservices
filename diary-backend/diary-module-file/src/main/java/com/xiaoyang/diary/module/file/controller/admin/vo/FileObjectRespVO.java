package com.xiaoyang.diary.module.file.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文件 Response VO")
@Data
public class FileObjectRespVO {

    @Schema(description = "文件编号")
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "扩展名")
    private String extension;

    @Schema(description = "MIME 类型")
    private String contentType;

    @Schema(description = "文件分类")
    private String fileCategory;

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "SHA-256")
    private String sha256;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务编号")
    private String businessId;

    @Schema(description = "是否支持预览")
    private Boolean previewSupported;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
