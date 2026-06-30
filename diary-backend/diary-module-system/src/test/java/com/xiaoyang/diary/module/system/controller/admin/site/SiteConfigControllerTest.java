package com.xiaoyang.diary.module.system.controller.admin.site;

import com.xiaoyang.diary.module.system.controller.admin.site.vo.SiteConfigSaveReqVO;
import jakarta.annotation.security.PermitAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SiteConfigControllerTest {

    @Test
    void shouldPermitAnonymousPublicConfig() throws Exception {
        Method method = SiteConfigController.class.getDeclaredMethod("getPublicConfig");

        assertNotNull(method.getAnnotation(PermitAll.class));
    }

    @Test
    void shouldRequirePermissionForUpdate() throws Exception {
        Method method = SiteConfigController.class.getDeclaredMethod("updateConfig", SiteConfigSaveReqVO.class);
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

        assertEquals("@ss.hasPermission('site:config:update')", preAuthorize.value());
    }
}
