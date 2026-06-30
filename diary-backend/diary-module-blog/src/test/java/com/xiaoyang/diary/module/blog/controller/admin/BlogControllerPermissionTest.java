package com.xiaoyang.diary.module.blog.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BlogControllerPermissionTest {

    @Test
    void shouldRequirePermissionForWritesOnly() throws Exception {
        assertPermission("createArticle", "blog:article:create");
        assertPermission("updateArticle", "blog:article:update");
        assertPermission("deleteArticle", "blog:article:delete");
        assertNoPermission("getArticle");
        assertNoPermission("getArticlePage");
    }

    private void assertPermission(String methodName, String permission) throws Exception {
        Method method = findMethod(methodName);
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        assertEquals("@ss.hasPermission('" + permission + "')", preAuthorize.value());
    }

    private void assertNoPermission(String methodName) {
        Method method = findMethod(methodName);
        assertNull(method.getAnnotation(PreAuthorize.class));
    }

    private Method findMethod(String methodName) {
        for (Method method : BlogController.class.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Method not found: " + methodName);
    }
}
