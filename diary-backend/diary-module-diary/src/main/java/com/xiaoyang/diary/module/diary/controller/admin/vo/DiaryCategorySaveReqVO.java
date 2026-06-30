package com.xiaoyang.diary.module.diary.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Admin - Diary category save request")
@Data
public class DiaryCategorySaveReqVO {

    @Schema(description = "Category ID, required when updating")
    private Long id;

    @Schema(description = "Category name")
    @NotBlank(message = "Category name cannot be empty")
    @Size(max = 50, message = "Category name can contain at most 50 characters")
    private String name;

    @Schema(description = "Sort order")
    private Integer sort;

    @Schema(description = "Status: 0 disabled, 1 enabled")
    private Integer status;

}
