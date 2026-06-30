package com.xiaoyang.diary.module.diary.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Admin - Diary response")
@Data
public class DiaryRespVO {

    @Schema(description = "Diary ID")
    private Long id;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Category ID")
    private Long categoryId;

    @Schema(description = "Ordered content blocks")
    private List<DiaryContentBlockReqVO> contentBlocks;

    @Schema(description = "Summary")
    private String summary;

    @Schema(description = "Status: 0 draft, 1 published")
    private Integer status;

    @Schema(description = "Visibility: 0 owner only, 1 logged-in users, 2 public")
    private Integer visibility;

    @Schema(description = "Referenced file IDs")
    private List<Long> fileIds;

    @Schema(description = "Create time")
    private LocalDateTime createTime;

    @Schema(description = "Update time")
    private LocalDateTime updateTime;

}
