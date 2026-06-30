package com.xiaoyang.diary.module.system.controller.admin.site.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Admin - Site config response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfigRespVO {

    @Schema(description = "Home page type: diary or blog")
    private String homePageType;

}
