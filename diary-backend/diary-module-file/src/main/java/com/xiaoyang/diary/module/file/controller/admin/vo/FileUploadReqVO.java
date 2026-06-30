package com.xiaoyang.diary.module.file.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 文件上传 Request VO")
@Data
public class FileUploadReqVO {

    @Schema(description = "业务类型，例如 diary、avatar")
    @Size(max = 64)
    private String businessType;

    @Schema(description = "业务编号")
    @Size(max = 64)
    private String businessId;

}
