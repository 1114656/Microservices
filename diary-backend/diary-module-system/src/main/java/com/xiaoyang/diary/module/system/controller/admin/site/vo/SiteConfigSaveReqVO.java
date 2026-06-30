package com.xiaoyang.diary.module.system.controller.admin.site.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "Admin - Site config save request")
@Data
public class SiteConfigSaveReqVO {

    @Schema(description = "Home page type: diary or blog")
    @NotBlank(message = "Home page type cannot be empty")
    @Pattern(regexp = "diary|blog", message = "Home page type must be diary or blog")
    private String homePageType;

}
