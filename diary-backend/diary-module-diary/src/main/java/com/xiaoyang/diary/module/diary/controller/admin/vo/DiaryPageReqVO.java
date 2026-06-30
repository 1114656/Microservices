package com.xiaoyang.diary.module.diary.controller.admin.vo;

import com.xiaoyang.diary.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Admin - Diary page request")
@Data
@EqualsAndHashCode(callSuper = true)
public class DiaryPageReqVO extends PageParam {

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Category ID")
    private Long categoryId;

    @Schema(description = "Status: 0 draft, 1 published")
    private Integer status;

    @Schema(description = "Visibility: 0 owner only, 1 logged-in users, 2 public")
    private Integer visibility;

}
