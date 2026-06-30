package com.xiaoyang.diary.module.diary.controller.admin;

import jakarta.annotation.security.PermitAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiaryControllerPublicAccessTest {

    @Test
    void shouldPermitAnonymousDiaryPage() throws Exception {
        Method method = DiaryController.class.getDeclaredMethod("getDiaryPage",
                com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryPageReqVO.class);

        assertNotNull(method.getAnnotation(PermitAll.class));
    }

}
