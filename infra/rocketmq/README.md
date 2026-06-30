# RocketMQ Local Topics

Planned topics:

- `diary-file-events`
- `diary-audit-events`
- `diary-domain-events`

Event envelope fields:

- `eventId`
- `traceId`
- `occurredAt`
- `producer`
- `eventType`
- `payload`

Consumer rules:

- Consumers must be idempotent.
- Failed consumption must be retryable.
- Important events must log `eventId` and `traceId`.
