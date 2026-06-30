# Microservices Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first production-like microservices learning loop for Diary: gateway, Nacos registration/config, JWT auth, independent service databases, Feign calls, RocketMQ baseline, traceId logs, and Docker Compose orchestration.

**Architecture:** Convert the current Maven multi-module modular monolith into independently bootable Spring Boot services behind Spring Cloud Gateway. Each business service owns its own database and communicates through OpenFeign or RocketMQ events, while the gateway handles external routing, JWT validation, traceId injection, and user context propagation.

**Tech Stack:** Spring Boot 3.5.x, Spring Cloud 2025.x, Spring Cloud Alibaba 2025.x, Nacos, Spring Cloud Gateway, OpenFeign, Sentinel, RocketMQ, MySQL, Redis, MinIO, Docker Compose, Maven, Vue/Vite.

---

## File Structure

### New Backend Modules

- Create `diary-backend/diary-gateway/pom.xml`: Gateway application dependencies and packaging.
- Create `diary-backend/diary-gateway/src/main/java/com/xiaoyang/diary/gateway/DiaryGatewayApplication.java`: Gateway boot class.
- Create `diary-backend/diary-gateway/src/main/java/com/xiaoyang/diary/gateway/filter/TraceIdGatewayFilter.java`: Adds or propagates `X-Trace-Id`.
- Create `diary-backend/diary-gateway/src/main/java/com/xiaoyang/diary/gateway/filter/JwtAuthenticationFilter.java`: Validates JWT and propagates user headers.
- Create `diary-backend/diary-gateway/src/main/resources/application.yaml`: Gateway routes and shared runtime settings.
- Create `diary-backend/diary-gateway/src/main/resources/application-local.yaml`: Local gateway ports and Nacos addresses.

### Existing Backend Modules To Convert

- Modify `diary-backend/pom.xml`: Add gateway module and service boot modules.
- Modify `diary-backend/diary-dependencies/pom.xml`: Add Spring Cloud, Spring Cloud Alibaba, Gateway, Nacos, OpenFeign, Sentinel, RocketMQ, JWT, Actuator dependency management.
- Modify `diary-backend/diary-module-system/pom.xml`: Make system module independently bootable and expose auth APIs.
- Modify `diary-backend/diary-module-file/pom.xml`: Make file module independently bootable and expose file APIs.
- Modify `diary-backend/diary-module-diary/pom.xml`: Make diary module independently bootable and add Feign client dependencies.
- Modify `diary-backend/diary-module-blog/pom.xml`: Make blog module independently bootable.
- Create `diary-backend/diary-module-system/src/main/java/com/xiaoyang/diary/module/system/SystemServiceApplication.java`.
- Create `diary-backend/diary-module-file/src/main/java/com/xiaoyang/diary/module/file/FileServiceApplication.java`.
- Create `diary-backend/diary-module-diary/src/main/java/com/xiaoyang/diary/module/diary/DiaryServiceApplication.java`.
- Create `diary-backend/diary-module-blog/src/main/java/com/xiaoyang/diary/module/blog/BlogServiceApplication.java`.

### Shared Microservice Support

- Create `diary-backend/diary-framework/diary-spring-boot-starter-cloud/pom.xml`: Shared cloud starter.
- Create `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/java/com/xiaoyang/diary/framework/cloud/context/LoginUserContext.java`: Reads propagated user context.
- Create `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/java/com/xiaoyang/diary/framework/cloud/feign/FeignHeaderInterceptor.java`: Propagates user headers and traceId.
- Create `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/java/com/xiaoyang/diary/framework/cloud/trace/TraceIdFilter.java`: Adds traceId to MVC service logs.
- Create `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`: Auto-configuration registration.

### API Contracts

- Create `diary-backend/diary-module-system/diary-module-system-api/pom.xml`: System Feign API module.
- Create `diary-backend/diary-module-system/diary-module-system-api/src/main/java/com/xiaoyang/diary/module/system/api/auth/SystemAuthApi.java`: Token validation and profile summary API.
- Create `diary-backend/diary-module-file/diary-module-file-api/pom.xml`: File Feign API module.
- Create `diary-backend/diary-module-file/diary-module-file-api/src/main/java/com/xiaoyang/diary/module/file/api/FileApi.java`: Batch file metadata API.

### Infrastructure

- Create `infra/docker-compose.yml`: Nacos, Sentinel, RocketMQ, MySQL, Redis, MinIO, Gateway, and business services.
- Create `infra/mysql/init/01-system.sql`: System database and tables.
- Create `infra/mysql/init/02-file.sql`: File database and tables.
- Create `infra/mysql/init/03-diary.sql`: Diary database and tables.
- Create `infra/mysql/init/04-blog.sql`: Blog database and tables.
- Create `infra/nacos/config/diary-gateway.yaml`: Gateway config seed.
- Create `infra/nacos/config/diary-system-service.yaml`: System config seed.
- Create `infra/nacos/config/diary-file-service.yaml`: File config seed.
- Create `infra/nacos/config/diary-diary-service.yaml`: Diary config seed.
- Create `infra/nacos/config/diary-blog-service.yaml`: Blog config seed.
- Create `infra/rocketmq/README.md`: RocketMQ topics and local verification commands.

### Frontend

- Modify `diary-vue/.env`: Point Vite proxy to the gateway.
- Modify `diary-vue/src/config/axios/service.ts` or current request wrapper file: Standardize `Authorization: Bearer <token>`.
- Modify `diary-vue/src/api/**/*.ts`: Move API paths to `/admin-api/system/**`, `/admin-api/file/**`, `/admin-api/diary/**`, and `/admin-api/blog/**`.

### Documentation

- Modify `docs/superpowers/specs/2026-06-30-microservices-learning-roadmap.md`: Append completion records after each verified task.
- Create `docs/microservices/runbook.md`: How to start, stop, verify, and troubleshoot the local microservice stack.

---

### Task 1: Initialize Git And Documentation Baseline

**Files:**
- Track: `docs/superpowers/specs/2026-06-30-microservices-learning-roadmap.md`
- Track: `docs/superpowers/plans/2026-06-30-microservices-phase1.md`

- [ ] **Step 1: Initialize Git repository if missing**

Run:

```powershell
git rev-parse --show-toplevel
```

Expected if not initialized:

```text
fatal: not a git repository (or any of the parent directories): .git
```

Then run:

```powershell
git init
git branch -M main
git remote add origin git@github.com:1114656/Microservices.git
```

- [ ] **Step 2: Add documentation files only**

Run:

```powershell
git add docs/superpowers/specs/2026-06-30-microservices-learning-roadmap.md docs/superpowers/plans/2026-06-30-microservices-phase1.md
git status --short
```

Expected:

```text
A  docs/superpowers/plans/2026-06-30-microservices-phase1.md
A  docs/superpowers/specs/2026-06-30-microservices-learning-roadmap.md
```

- [ ] **Step 3: Commit with the requested format**

Run:

```powershell
git commit -m "26-06-30:微服务-加入了消息队列"
```

Expected:

```text
[main <hash>] 26-06-30:微服务-加入了消息队列
```

- [ ] **Step 4: Push baseline**

Run:

```powershell
git push -u origin main
```

Expected:

```text
branch 'main' set up to track 'origin/main'
```

---

### Task 2: Add Dependency Management For Cloud Stack

**Files:**
- Modify: `diary-backend/pom.xml`
- Modify: `diary-backend/diary-dependencies/pom.xml`

- [ ] **Step 1: Add dependency versions**

In `diary-backend/diary-dependencies/pom.xml`, add these properties near the existing Spring Boot property:

```xml
<spring.cloud.version>2025.0.0</spring.cloud.version>
<spring.cloud.alibaba.version>2025.0.0.0</spring.cloud.alibaba.version>
<jjwt.version>0.12.6</jjwt.version>
```

- [ ] **Step 2: Import Spring Cloud BOMs**

In `diary-backend/diary-dependencies/pom.xml`, add these imports after `spring-boot-dependencies`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>${spring.cloud.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
    <version>${spring.cloud.alibaba.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

- [ ] **Step 3: Add managed JWT dependencies**

In `diary-backend/diary-dependencies/pom.xml`, add:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>${jjwt.version}</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>${jjwt.version}</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>${jjwt.version}</version>
</dependency>
```

- [ ] **Step 4: Register new modules**

In `diary-backend/pom.xml`, add:

```xml
<module>diary-gateway</module>
```

Inside `diary-backend/diary-framework/pom.xml`, add:

```xml
<module>diary-spring-boot-starter-cloud</module>
```

- [ ] **Step 5: Verify dependency resolution**

Run:

```powershell
mvn -pl diary-dependencies -am test -DskipTests
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 6: Commit**

Run:

```powershell
git add diary-backend/pom.xml diary-backend/diary-dependencies/pom.xml diary-backend/diary-framework/pom.xml
git commit -m "26-06-30:微服务-加入云原生依赖管理"
```

---

### Task 3: Create Shared Cloud Starter

**Files:**
- Create: `diary-backend/diary-framework/diary-spring-boot-starter-cloud/pom.xml`
- Create: `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/java/com/xiaoyang/diary/framework/cloud/config/DiaryCloudAutoConfiguration.java`
- Create: `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/java/com/xiaoyang/diary/framework/cloud/context/LoginUserContext.java`
- Create: `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/java/com/xiaoyang/diary/framework/cloud/feign/FeignHeaderInterceptor.java`
- Create: `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/java/com/xiaoyang/diary/framework/cloud/trace/TraceIdFilter.java`
- Create: `diary-backend/diary-framework/diary-spring-boot-starter-cloud/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

- [ ] **Step 1: Create starter POM**

Create `pom.xml` with:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>cn.iocoder.boot</groupId>
        <artifactId>diary-framework</artifactId>
        <version>${revision}</version>
    </parent>
    <artifactId>diary-spring-boot-starter-cloud</artifactId>
    <packaging>jar</packaging>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create user context reader**

Create `LoginUserContext.java`:

```java
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
```

- [ ] **Step 3: Create Feign header interceptor**

Create `FeignHeaderInterceptor.java`:

```java
package com.xiaoyang.diary.framework.cloud.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

public class FeignHeaderInterceptor implements RequestInterceptor {

    private static final List<String> PROPAGATED_HEADERS = List.of(
            "Authorization", "X-User-Id", "X-Username", "X-Roles", "X-Trace-Id", "X-Internal-Token"
    );

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        for (String header : PROPAGATED_HEADERS) {
            String value = request.getHeader(header);
            if (value != null && !value.isBlank()) {
                template.header(header, value);
            }
        }
    }
}
```

- [ ] **Step 4: Create trace filter**

Create `TraceIdFilter.java`:

```java
package com.xiaoyang.diary.framework.cloud.trace;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put("traceId", traceId);
        response.setHeader(TRACE_ID, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }
}
```

- [ ] **Step 5: Create auto-configuration**

Create `DiaryCloudAutoConfiguration.java`:

```java
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
```

- [ ] **Step 6: Register auto-configuration**

Create `AutoConfiguration.imports` with:

```text
com.xiaoyang.diary.framework.cloud.config.DiaryCloudAutoConfiguration
```

- [ ] **Step 7: Verify starter compiles**

Run:

```powershell
mvn -pl diary-framework/diary-spring-boot-starter-cloud -am test
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 8: Commit**

Run:

```powershell
git add diary-backend/diary-framework/diary-spring-boot-starter-cloud diary-backend/diary-framework/pom.xml
git commit -m "26-06-30:微服务-加入上下文透传组件"
```

---

### Task 4: Create Gateway Skeleton

**Files:**
- Create: `diary-backend/diary-gateway/pom.xml`
- Create: `diary-backend/diary-gateway/src/main/java/com/xiaoyang/diary/gateway/DiaryGatewayApplication.java`
- Create: `diary-backend/diary-gateway/src/main/java/com/xiaoyang/diary/gateway/filter/TraceIdGatewayFilter.java`
- Create: `diary-backend/diary-gateway/src/main/resources/application.yaml`
- Create: `diary-backend/diary-gateway/src/main/resources/application-local.yaml`

- [ ] **Step 1: Create gateway POM**

Create `diary-gateway/pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>cn.iocoder.boot</groupId>
        <artifactId>diary-backend</artifactId>
        <version>${revision}</version>
    </parent>
    <artifactId>diary-gateway</artifactId>
    <packaging>jar</packaging>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create boot class**

Create `DiaryGatewayApplication.java`:

```java
package com.xiaoyang.diary.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DiaryGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiaryGatewayApplication.class, args);
    }
}
```

- [ ] **Step 3: Add traceId gateway filter**

Create `TraceIdGatewayFilter.java`:

```java
package com.xiaoyang.diary.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {

    public static final String TRACE_ID = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        String finalTraceId = traceId;
        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder.header(TRACE_ID, finalTraceId))
                .response(exchange.getResponse())
                .build();
        mutated.getResponse().getHeaders().set(TRACE_ID, finalTraceId);
        MDC.put("traceId", finalTraceId);
        return chain.filter(mutated).doFinally(signal -> MDC.remove("traceId"));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
```

- [ ] **Step 4: Add gateway routes**

Create `application.yaml`:

```yaml
spring:
  application:
    name: diary-gateway
  profiles:
    active: local
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: diary-system-service
              uri: lb://diary-system-service
              predicates:
                - Path=/admin-api/system/**
            - id: diary-file-service
              uri: lb://diary-file-service
              predicates:
                - Path=/admin-api/file/**
            - id: diary-diary-service
              uri: lb://diary-diary-service
              predicates:
                - Path=/admin-api/diary/**
            - id: diary-blog-service
              uri: lb://diary-blog-service
              predicates:
                - Path=/admin-api/blog/**

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

- [ ] **Step 5: Add local config**

Create `application-local.yaml`:

```yaml
server:
  port: 48080

spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_ADDR:127.0.0.1:8848}
      config:
        server-addr: ${NACOS_ADDR:127.0.0.1:8848}
        file-extension: yaml
```

- [ ] **Step 6: Verify gateway compiles**

Run:

```powershell
mvn -pl diary-gateway -am test
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 7: Commit**

Run:

```powershell
git add diary-backend/diary-gateway diary-backend/pom.xml
git commit -m "26-06-30:微服务-加入网关骨架"
```

---

### Task 5: Convert Business Modules Into Bootable Services

**Files:**
- Modify: `diary-backend/diary-module-system/pom.xml`
- Modify: `diary-backend/diary-module-file/pom.xml`
- Modify: `diary-backend/diary-module-diary/pom.xml`
- Modify: `diary-backend/diary-module-blog/pom.xml`
- Create: four service application classes listed in File Structure.

- [ ] **Step 1: Add service dependencies to each module**

For each business module POM, include these dependencies if not already inherited through local starters:

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>diary-spring-boot-starter-cloud</artifactId>
</dependency>
```

- [ ] **Step 2: Create system service application**

Create `SystemServiceApplication.java`:

```java
package com.xiaoyang.diary.module.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.xiaoyang.diary")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.xiaoyang.diary")
public class SystemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemServiceApplication.class, args);
    }
}
```

- [ ] **Step 3: Create file service application**

Create `FileServiceApplication.java`:

```java
package com.xiaoyang.diary.module.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.xiaoyang.diary")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.xiaoyang.diary")
public class FileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}
```

- [ ] **Step 4: Create diary service application**

Create `DiaryServiceApplication.java`:

```java
package com.xiaoyang.diary.module.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.xiaoyang.diary")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.xiaoyang.diary")
public class DiaryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiaryServiceApplication.class, args);
    }
}
```

- [ ] **Step 5: Create blog service application**

Create `BlogServiceApplication.java`:

```java
package com.xiaoyang.diary.module.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.xiaoyang.diary")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.xiaoyang.diary")
public class BlogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogServiceApplication.class, args);
    }
}
```

- [ ] **Step 6: Verify all service modules compile**

Run:

```powershell
mvn -pl diary-module-system,diary-module-file,diary-module-diary,diary-module-blog -am test
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 7: Commit**

Run:

```powershell
git add diary-backend/diary-module-system diary-backend/diary-module-file diary-backend/diary-module-diary diary-backend/diary-module-blog
git commit -m "26-06-30:微服务-拆分业务服务启动入口"
```

---

### Task 6: Add Docker Compose Infrastructure

**Files:**
- Create: `infra/docker-compose.yml`
- Create: `infra/mysql/init/01-system.sql`
- Create: `infra/mysql/init/02-file.sql`
- Create: `infra/mysql/init/03-diary.sql`
- Create: `infra/mysql/init/04-blog.sql`
- Create: `infra/rocketmq/README.md`

- [ ] **Step 1: Create Compose file**

Create `infra/docker-compose.yml` with services for `mysql`, `redis`, `minio`, `nacos`, `sentinel`, `rocketmq-namesrv`, `rocketmq-broker`, `diary-gateway`, `diary-system-service`, `diary-file-service`, `diary-diary-service`, and `diary-blog-service`.

Use ports:

```text
48080 gateway
48081 system
48082 file
48083 diary
48084 blog
8848 nacos
8080 sentinel
3306 mysql
6379 redis
9000 minio api
9001 minio console
9876 rocketmq nameserver
10911 rocketmq broker
```

- [ ] **Step 2: Split SQL by ownership**

Move system tables to `01-system.sql`, file tables to `02-file.sql`, diary tables to `03-diary.sql`, and blog tables to `04-blog.sql`.

Each script starts with:

```sql
CREATE DATABASE IF NOT EXISTS diary_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Use the matching database name per file.

- [ ] **Step 3: Document RocketMQ topics**

Create `infra/rocketmq/README.md`:

```markdown
# RocketMQ Local Topics

- diary-file-events
- diary-audit-events
- diary-domain-events

Each event must include eventId, traceId, occurredAt, producer, eventType, and payload.
Consumers must be idempotent.
```

- [ ] **Step 4: Validate Compose config**

Run:

```powershell
docker compose -f infra/docker-compose.yml config
```

Expected:

```text
name:
services:
```

- [ ] **Step 5: Commit**

Run:

```powershell
git add infra
git commit -m "26-06-30:微服务-加入本地编排环境"
```

---

### Task 7: Update Completion Record

**Files:**
- Modify: `docs/superpowers/specs/2026-06-30-microservices-learning-roadmap.md`

- [ ] **Step 1: Append completion rows after verified tasks**

After each completed task, add a row like:

```markdown
| 2026-06-30 | 第一阶段 | 加入网关骨架 | `mvn -pl diary-gateway -am test` 通过 | Gateway 已注册为独立模块 |
```

- [ ] **Step 2: Commit documentation update**

Run:

```powershell
git add docs/superpowers/specs/2026-06-30-microservices-learning-roadmap.md
git commit -m "26-06-30:微服务-更新阶段完成记录"
```

---

## Self-Review

- Spec coverage: This plan covers the roadmap's first-stage foundation: Gateway, Nacos, Config, Feign, JWT foundation, independent services, independent databases, RocketMQ baseline, Compose, traceId, and documentation record keeping.
- Known decomposition: JWT implementation details, reliable outbox, idempotent consumers, and Sentinel rules are intentionally implemented in later focused plans because they each need tests and reviewable slices.
- Placeholder scan: The plan avoids implementation placeholders by naming concrete files, paths, commands, ports, and expected verification results.
- Type consistency: Header names and service names match the roadmap: `X-Trace-Id`, `X-User-Id`, `diary-system-service`, `diary-file-service`, `diary-diary-service`, `diary-blog-service`.
