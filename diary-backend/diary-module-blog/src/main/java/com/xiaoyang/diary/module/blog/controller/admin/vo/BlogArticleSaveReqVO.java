package com.xiaoyang.diary.module.blog.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Admin - Blog article save request")
@Data
public class BlogArticleSaveReqVO {

    @Schema(description = "Article ID, required when updating")
    private Long id;

    @Schema(description = "Title")
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 120, message = "Title can contain at most 120 characters")
    private String title;

    @Schema(description = "Markdown content. Code blocks are passed as fenced code blocks by the frontend")
    @NotBlank(message = "Content cannot be empty")
    private String contentMarkdown;

    @Schema(description = "Cover file ID")
    private Long coverFileId;

    @Schema(description = "Status: 0 draft, 1 published")
    private Integer status;

    @Schema(description = "Visibility: 0 owner only, 1 logged-in users, 2 public")
    private Integer visibility;

}
