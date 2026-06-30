package com.xiaoyang.diary.framework.cloud.trace;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TraceIdFilterTest {

    @Test
    void shouldKeepIncomingTraceIdAndExposeItInResponse() throws Exception {
        TraceIdFilter filter = new TraceIdFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(TraceIdFilter.TRACE_ID, "trace-001");

        FilterChain chain = (servletRequest, servletResponse) ->
                assertEquals("trace-001", MDC.get("traceId"));

        filter.doFilter(request, response, chain);

        assertEquals("trace-001", response.getHeader(TraceIdFilter.TRACE_ID));
        assertNull(MDC.get("traceId"));
    }

    @Test
    void shouldGenerateTraceIdWhenRequestDoesNotContainOne() throws Exception {
        TraceIdFilter filter = new TraceIdFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                assertNotNull(MDC.get("traceId")));

        assertNotNull(response.getHeader(TraceIdFilter.TRACE_ID));
        assertNull(MDC.get("traceId"));
    }
}
