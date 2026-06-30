package com.xiaoyang.diary.module.blog.controller.admin.vo;

import com.xiaoyang.diary.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Admin - Blog article page request")
@Data
@EqualsAndHashCode(callSuper = true)
public class BlogArticlePageReqVO extends PageParam {

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Status: 0 draft, 1 published")
    private Integer status;

    @Schema(description = "Visibility: 0 owner only, 1 logged-in users, 2 public")
    private Integer visibility;

}
