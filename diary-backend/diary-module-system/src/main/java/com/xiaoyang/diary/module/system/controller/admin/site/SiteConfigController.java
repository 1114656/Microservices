package com.xiaoyang.diary.module.system.controller.admin.site;

import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.module.system.controller.admin.site.vo.SiteConfigRespVO;
import com.xiaoyang.diary.module.system.controller.admin.site.vo.SiteConfigSaveReqVO;
import com.xiaoyang.diary.module.system.service.site.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;

@Tag(name = "Admin - Site config")
@RestController
@RequestMapping("/admin-api/system/site/config")
@Validated
public class SiteConfigController {

    @Resource
    private SiteConfigService siteConfigService;

    @GetMapping("/public")
    @Operation(summary = "Get public site config")
    @PermitAll
    public CommonResult<SiteConfigRespVO> getPublicConfig() {
        return success(new SiteConfigRespVO(siteConfigService.getHomePageType()));
    }

    @GetMapping("/get")
    @Operation(summary = "Get site config")
    public CommonResult<SiteConfigRespVO> getConfig() {
        return success(new SiteConfigRespVO(siteConfigService.getHomePageType()));
    }

    @PutMapping("/update")
    @Operation(summary = "Update site config")
    @PreAuthorize("@ss.hasPermission('site:config:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody SiteConfigSaveReqVO reqVO) {
        siteConfigService.updateHomePageType(reqVO.getHomePageType());
        return success(true);
    }

}
