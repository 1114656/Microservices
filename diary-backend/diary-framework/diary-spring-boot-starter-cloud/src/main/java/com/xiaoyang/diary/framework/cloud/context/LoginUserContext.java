package com.xiaoyang.diary.framework.cloud.context;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class LoginUserContext {

    public static final String USER_ID = "X-User-Id";
    public static final String USERNAME = "X-Username";
    public static final String ROLES = "X-Roles";
    public static final String TRACE_ID = "X-Trace-Id";

    private LoginUserContext() {
    }

    public static String getUserId() {
        return getHeader(USER_ID);
    }

    public static String getUsername() {
        return getHeader(USERNAME);
    }

    public static String getRoles() {
        return getHeader(ROLES);
    }

    public static String getTraceId() {
        return getHeader(TRACE_ID);
    }

    private static String getHeader(String name) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader(name);
    }
}
