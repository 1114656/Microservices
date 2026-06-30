package com.xiaoyang.diary.module.file.controller.admin.vo;

import com.xiaoyang.diary.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 文件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FileObjectPageReqVO extends PageParam {

    @Schema(description = "文件分类：image、audio、video、text、document、archive、other")
    private String fileCategory;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务编号")
    private String businessId;

}
