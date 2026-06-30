package com.xiaoyang.diary.module.blog.controller.admin;

import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticlePageReqVO;
import jakarta.annotation.security.PermitAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BlogControllerPublicAccessTest {

    @Test
    void shouldPermitAnonymousArticlePage() throws Exception {
        Method method = BlogController.class.getDeclaredMethod("getArticlePage", BlogArticlePageReqVO.class);

        assertNotNull(method.getAnnotation(PermitAll.class));
    }

}
