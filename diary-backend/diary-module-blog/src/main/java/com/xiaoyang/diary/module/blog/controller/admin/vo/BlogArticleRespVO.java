package com.xiaoyang.diary.module.blog.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Admin - Blog article response")
@Data
public class BlogArticleRespVO {

    @Schema(description = "Article ID")
    private Long id;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Markdown content")
    private String contentMarkdown;

    @Schema(description = "Summary")
    private String summary;

    @Schema(description = "Cover file ID")
    private Long coverFileId;

    @Schema(description = "Status: 0 draft, 1 published")
    private Integer status;

    @Schema(description = "Visibility: 0 owner only, 1 logged-in users, 2 public")
    private Integer visibility;

    @Schema(description = "Create time")
    private LocalDateTime createTime;

    @Schema(description = "Update time")
    private LocalDateTime updateTime;

}
