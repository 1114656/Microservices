package com.xiaoyang.diary.module.file.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 文件预览地址 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilePreviewUrlRespVO {

    @Schema(description = "短时预览 URL")
    private String url;

}
