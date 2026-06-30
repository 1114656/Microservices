# Diary 微服务改造学习路线

## 目标

把当前的模块化单体 Diary 后端，分阶段改造成一个偏真实生产场景的 Spring Cloud Alibaba 微服务学习项目。

这个改造不是只追求“拆成多个服务能启动”，而是要覆盖真实微服务中常见的问题：

- 服务注册与发现
- 配置中心
- 网关统一入口
- 标准 JWT 鉴权
- 服务间调用
- 每服务独立数据库
- 消息队列与异步事件
- 最终一致性与补偿
- 限流、熔断、降级
- traceId 与可观测性
- Docker Compose 本地编排
- 故障演练与排查

## 技术选型

- 微服务框架：Spring Boot 3.5.x + Spring Cloud 2025.x + Spring Cloud Alibaba 2025.x
- 注册中心：Nacos
- 配置中心：Nacos Config
- 网关：Spring Cloud Gateway
- 服务调用：OpenFeign
- 限流熔断：Sentinel
- 消息队列：RocketMQ
- 数据库：MySQL，每个业务服务独立 database 与账号
- 缓存与会话：Redis
- 对象存储：MinIO
- 本地编排：Docker Compose
- 观测基础：Actuator + traceId 日志 + Micrometer

## 服务拆分

第一版按现有后端模块拆分：

- `diary-gateway`：统一入口、路由、跨域、JWT 校验、限流、traceId 注入、用户上下文透传。
- `diary-system-service`：登录、JWT 签发、刷新 token、登出、用户、角色、权限、菜单。
- `diary-file-service`：文件上传、文件元数据、MinIO 对象存储、文件预览 URL。
- `diary-diary-service`：日记、日记分类、日记与文件关联。
- `diary-blog-service`：博客文章。

## 数据库拆分

每个服务只访问自己的库，不允许跨库直查。

- `diary_system`：用户、角色、权限、菜单、登录日志、操作日志、字典等。
- `diary_file`：文件配置、文件元数据、上传记录。
- `diary_diary`：日记、日记分类、日记与文件引用。
- `diary_blog`：博客文章。

跨服务数据读取必须通过 API 或异步事件完成。列表页需要跨服务补充数据时，优先使用批量查询接口，避免 N+1 远程调用。

## 接口路径规范

前后端同步标准化接口路径，不保留旧路径兼容层。

- `/admin-api/system/**` -> `diary-system-service`
- `/admin-api/file/**` -> `diary-file-service`
- `/admin-api/diary/**` -> `diary-diary-service`
- `/admin-api/blog/**` -> `diary-blog-service`

登录接口归 system 服务，例如：

- `POST /admin-api/system/auth/login`
- `POST /admin-api/system/auth/refresh-token`
- `POST /admin-api/system/auth/logout`
- `GET /admin-api/system/auth/profile`

## 鉴权设计

采用标准 JWT 方案：

- `system-service` 负责登录校验、签发 access token、签发 refresh token、登出和 token 黑名单。
- `gateway` 校验 access token。
- 校验通过后，gateway 通过请求头向后端服务透传用户上下文。
- 业务服务不直接处理浏览器登录态，只读取网关透传的用户上下文。
- Feign 调用继续透传用户上下文和 traceId。
- Redis 存储 refresh token、token 黑名单和会话控制数据。

建议透传请求头：

- `X-User-Id`
- `X-Username`
- `X-Roles`
- `X-Trace-Id`
- `X-Internal-Token`

## 消息队列设计

引入 RocketMQ，用于学习真实微服务里的异步事件、失败重试、消费幂等和最终一致性。

第一阶段只接入基础能力，第二阶段开始承载真实业务事件。

规划主题：

- `diary-file-events`：文件上传、文件删除、文件预览生成等事件。
- `diary-audit-events`：登录、创建日记、上传文件、发布博客等审计事件。
- `diary-domain-events`：日记创建、日记更新、博客发布等领域事件。

基础要求：

- 消息必须带 `eventId`、`traceId`、`occurredAt`、`producer`。
- 消费者必须做幂等处理。
- 消费失败要能重试。
- 关键事件要有可排查日志。
- 跨服务写入优先通过事件实现最终一致性。

## 第一阶段：微服务基础闭环

### 目标

先建立一套能启动、能注册、能路由、能登录、能调用、能观测的微服务基础闭环。

### 主要内容

- 新增 `diary-gateway`。
- 将 system、file、diary、blog 拆成独立 Spring Boot 服务。
- 接入 Nacos 注册中心。
- 接入 Nacos 配置中心。
- 接入 Spring Cloud Gateway 路由。
- 接入标准 JWT 登录与网关鉴权。
- 接入 OpenFeign 服务间调用。
- 每个服务连接自己的 MySQL database。
- 拆分 SQL 初始化脚本。
- 接入 Redis。
- 接入 MinIO。
- 引入 RocketMQ 基础依赖和 Docker Compose 服务。
- 接入 Actuator 健康检查。
- 接入 traceId 日志。
- 新增 Docker Compose 一键启动中间件与服务。
- 前端接口路径同步调整到 `/admin-api/{service}/**`。

### 学习重点

- 单体到微服务的边界变化
- 注册发现如何工作
- Gateway 如何统一入口
- JWT 在微服务里的校验与透传
- Feign 如何替代直接 Java Service 调用
- 独立数据库后为什么不能跨库 join
- Docker Compose 如何编排本地微服务环境

### 验收标准

- `docker compose up` 能启动 Nacos、Sentinel、RocketMQ、MySQL、Redis、MinIO、Gateway 和业务服务。
- 所有业务服务能注册到 Nacos。
- Gateway 能按服务名路由到 system、file、diary、blog。
- 登录能返回 access token 和 refresh token。
- 前端带 `Authorization: Bearer <token>` 能访问受保护接口。
- 日记列表能通过 Gateway 查询。
- 博客列表能通过 Gateway 查询。
- 文件能上传到 MinIO，元数据进入 `diary_file`。
- 日志中能看到同一次请求的 `traceId`。

## 第二阶段：真实业务一致性与异步事件

### 目标

引入真实跨服务业务场景，学习消息队列、最终一致性、补偿、幂等和失败重试。

### 主要内容

- 为 RocketMQ 建立统一事件模型。
- 增加消息生产者和消费者基础封装。
- 建立消费幂等表或 Redis 幂等记录。
- 引入 outbox 或可靠消息记录机制。
- 增加审计事件：登录、创建日记、上传文件、发布博客。
- 增加文件事件：文件上传成功后异步生成预览或记录处理状态。
- 增加日记与文件绑定场景：创建日记后异步校验文件归属和引用状态。
- 增加补偿任务：扫描失败事件并重试。
- 增加 Feign fallback 和 Sentinel 资源规则。
- 增加批量远程查询接口，减少 N+1 调用。

### 学习重点

- 为什么本地事务不能解决跨服务事务
- 最终一致性如何设计
- MQ 消费为什么必须幂等
- 消息发送成功但本地事务失败怎么办
- 本地事务成功但消息发送失败怎么办
- Feign 调用失败如何降级
- Sentinel 如何保护热点接口和下游依赖

### 验收标准

- 创建日记并绑定文件时，不直接跨库写 file 数据。
- 文件上传成功后能发出文件事件。
- 审计事件能被消费并记录。
- 重复消费同一事件不会产生重复业务结果。
- 停掉 `file-service` 后，`diary-service` 对文件信息展示能降级。
- RocketMQ 消费失败后能重试，并有日志可排查。
- 补偿任务能处理失败事件。

## 第三阶段：生产化演练与可观测性

### 目标

把项目从“能跑的微服务”提升为“能排查、能保护、能演练故障”的学习环境。

### 主要内容

- 完善 Sentinel 限流、熔断、热点参数规则。
- 接入 Micrometer 指标。
- 增加 Prometheus/Grafana 预留或接入方案。
- 增加统一日志格式。
- 增加错误码与异常响应规范。
- 增加服务健康检查和依赖健康检查。
- 增加压测脚本。
- 增加故障演练文档。
- 增加灰度路由学习场景，例如按 header 路由到 v1/v2。
- 增加部署参数说明和环境变量清单。
- 增加安全加固：内部接口 token、网关转发头清洗、服务端口暴露控制。

### 学习重点

- 如何发现慢接口
- 如何定位跨服务调用失败
- 如何判断是网关、服务、数据库、Redis、MQ 哪一层出问题
- 限流和熔断的区别
- 服务降级如何让系统保住核心功能
- 灰度发布和版本路由的基本思路

### 验收标准

- 能通过日志 traceId 串起一次跨服务请求。
- 能看到服务健康状态。
- 能触发并观察 Sentinel 限流。
- 能演练下游服务宕机并验证降级结果。
- 能演练 MQ 消费失败并验证重试或补偿。
- 能用文档说明常见故障的排查路径。
- Compose 环境变量和启动方式清晰可复现。

## 完成记录

后续每完成一块改造，都在这里追加记录，方便按文档继续推进。

| 日期 | 阶段 | 完成内容 | 验证方式 | 备注 |
| --- | --- | --- | --- | --- |
| 2026-06-30 | 规划 | 创建微服务改造学习路线文档 | 文档已创建 | 后续按本文档推进 |
| 2026-06-30 | 第一阶段 | 加入微服务上下文透传组件 | `mvn -pl diary-framework/diary-spring-boot-starter-cloud -am test` 通过，4 个测试通过 | 已覆盖 traceId 过滤器和 Feign 请求头透传 |

## 后续推进规则

- 每次只推进一个明确小目标。
- 改代码前先确认该目标属于哪个阶段。
- 每个目标完成后必须运行对应验证。
- 验证通过后，把完成内容写入本文档的“完成记录”。
- 如果实际实现和本文档设计不同，先更新本文档，再继续改造。
