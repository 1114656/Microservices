package com.xiaoyang.diary.module.diary.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - Diary category response")
@Data
public class DiaryCategoryRespVO {

    @Schema(description = "Category ID")
    private Long id;

    @Schema(description = "Category name")
    private String name;

    @Schema(description = "Sort order")
    private Integer sort;

    @Schema(description = "Status: 0 disabled, 1 enabled")
    private Integer status;

    @Schema(description = "All diary count")
    private Long totalCount;

    @Schema(description = "Draft diary count")
    private Long draftCount;

    @Schema(description = "Published diary count")
    private Long publishedCount;

    @Schema(description = "Create time")
    private LocalDateTime createTime;

    @Schema(description = "Update time")
    private LocalDateTime updateTime;

}
