package com.xiaoyang.diary.framework.cloud.config;

import com.xiaoyang.diary.framework.cloud.feign.FeignHeaderInterceptor;
import com.xiaoyang.diary.framework.cloud.trace.TraceIdFilter;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DiaryCloudAutoConfiguration {

    @Bean
    @ConditionalOnClass(RequestInterceptor.class)
    public FeignHeaderInterceptor feignHeaderInterceptor() {
        return new FeignHeaderInterceptor();
    }

    @Bean
    @ConditionalOnClass(FilterRegistrationBean.class)
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter());
        registration.setOrder(-100);
        return registration;
    }
}
