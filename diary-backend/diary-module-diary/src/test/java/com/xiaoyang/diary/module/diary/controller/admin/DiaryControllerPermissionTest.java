package com.xiaoyang.diary.module.diary.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DiaryControllerPermissionTest {

    @Test
    void shouldRequirePermissionForWritesOnly() throws Exception {
        assertPermission("createDiary", "diary:entry:create");
        assertPermission("updateDiary", "diary:entry:update");
        assertPermission("deleteDiary", "diary:entry:delete");
        assertNoPermission("getDiary");
        assertNoPermission("getDiaryPage");
    }

    private void assertPermission(String methodName, String permission) throws Exception {
        Method method = findMethod(methodName);
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        assertEquals("@ss.hasPermission('" + permission + "')", preAuthorize.value());
    }

    private void assertNoPermission(String methodName) throws Exception {
        Method method = findMethod(methodName);
        assertNull(method.getAnnotation(PreAuthorize.class));
    }

    private Method findMethod(String methodName) {
        for (Method method : DiaryController.class.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Method not found: " + methodName);
    }
}
