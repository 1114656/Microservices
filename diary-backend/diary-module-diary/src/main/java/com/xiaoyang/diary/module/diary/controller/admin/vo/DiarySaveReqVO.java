package com.xiaoyang.diary.module.diary.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Schema(description = "Admin - Diary save request")
@Data
public class DiarySaveReqVO {

    @Schema(description = "Diary ID, required when updating")
    private Long id;

    @Schema(description = "Title")
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title can contain at most 100 characters")
    private String title;

    @Schema(description = "Category ID")
    @NotNull(message = "Category cannot be empty")
    private Long categoryId;

    @Schema(description = "Ordered content blocks with text and file references")
    @Valid
    private List<DiaryContentBlockReqVO> contentBlocks;

    @Schema(description = "Status: 0 draft, 1 published")
    private Integer status;

    @Schema(description = "Visibility: 0 owner only, 1 logged-in users, 2 public")
    private Integer visibility;

}
