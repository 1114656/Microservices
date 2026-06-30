# Sentinel Rules

This project keeps a local, reviewable Sentinel baseline for learning.

## Rule Sources

- `diary-module-diary/src/main/resources/sentinel/flow-rules.json`
- `diary-module-diary/src/main/resources/sentinel/degrade-rules.json`
- `diary-module-blog/src/main/resources/sentinel/flow-rules.json`
- `diary-module-blog/src/main/resources/sentinel/degrade-rules.json`

The Nacos seed configs enable file datasource loading through:

```yaml
spring.cloud.sentinel.datasource.flow.file.rule-type: flow
spring.cloud.sentinel.datasource.degrade.file.rule-type: degrade
```

## Fallbacks

- Gateway returns HTTP 429 with `gateway sentinel blocked` when Spring Cloud Gateway Sentinel blocks a request.
- Feign Sentinel is enabled for diary/blog via `feign.sentinel.enabled=true`.
- `FileObjectApiFallbackFactory` returns a recognizable `file-service unavailable, please retry later` result when file-service is unavailable.

## Drill

1. Start the Compose stack on a machine with Docker.
2. Open Sentinel Dashboard at `http://localhost:8080`.
3. Hit `GET /admin-api/diary/page` or `GET /admin-api/blog/page` through Gateway repeatedly.
4. Stop `diary-file-service`, then create/update a diary or blog with a file reference to observe the Feign fallback path.
