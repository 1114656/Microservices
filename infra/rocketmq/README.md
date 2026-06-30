# RocketMQ Local Topics

Planned topics:

- `diary-file-events`
  - `file-uploaded`: produced by `diary-file-service` after file metadata and the local outbox row are committed.
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
- Business services should write an outbox row inside the local transaction first, then publish to RocketMQ from a retryable scheduler.
