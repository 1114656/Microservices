package com.xiaoyang.diary.framework.cloud.feign;

import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FeignHeaderInterceptorTest {

    @Test
    void shouldPropagateAuthUserAndTraceHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        request.addHeader("X-User-Id", "1001");
        request.addHeader("X-Username", "xiaoyang");
        request.addHeader("X-Roles", "admin");
        request.addHeader("X-Trace-Id", "trace-001");
        request.addHeader("X-Internal-Token", "internal");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        try {
            new FeignHeaderInterceptor().apply(template);
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }

        assertEquals("Bearer token", template.headers().get("Authorization").iterator().next());
        assertEquals("1001", template.headers().get("X-User-Id").iterator().next());
        assertEquals("xiaoyang", template.headers().get("X-Username").iterator().next());
        assertEquals("admin", template.headers().get("X-Roles").iterator().next());
        assertEquals("trace-001", template.headers().get("X-Trace-Id").iterator().next());
        assertEquals("internal", template.headers().get("X-Internal-Token").iterator().next());
    }

    @Test
    void shouldIgnoreBlankHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", " ");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        try {
            new FeignHeaderInterceptor().apply(template);
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }

        assertFalse(template.headers().containsKey("X-User-Id"));
    }
}
