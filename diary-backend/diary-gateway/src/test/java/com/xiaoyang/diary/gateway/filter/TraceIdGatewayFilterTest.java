package com.xiaoyang.diary.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TraceIdGatewayFilterTest {

    @Test
    void shouldKeepIncomingTraceId() {
        TraceIdGatewayFilter filter = new TraceIdGatewayFilter();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/admin-api/system/auth/profile")
                        .header(TraceIdGatewayFilter.TRACE_ID, "trace-001"));

        GatewayFilterChain chain = mutatedExchange -> {
            assertEquals("trace-001", mutatedExchange.getRequest().getHeaders().getFirst(TraceIdGatewayFilter.TRACE_ID));
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertEquals("trace-001", exchange.getResponse().getHeaders().getFirst(TraceIdGatewayFilter.TRACE_ID));
    }

    @Test
    void shouldGenerateTraceIdWhenMissing() {
        TraceIdGatewayFilter filter = new TraceIdGatewayFilter();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/admin-api/system/auth/profile"));

        GatewayFilterChain chain = mutatedExchange -> {
            assertNotNull(mutatedExchange.getRequest().getHeaders().getFirst(TraceIdGatewayFilter.TRACE_ID));
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertNotNull(exchange.getResponse().getHeaders().getFirst(TraceIdGatewayFilter.TRACE_ID));
    }
}
