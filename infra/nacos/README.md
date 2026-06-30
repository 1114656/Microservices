# Nacos config bootstrap

The application YAML files already import these Data IDs with `optional:nacos:`.

- Group: `DIARY_GROUP`
- Format: `yaml`
- Data IDs:
  - `diary-common.yaml`
  - `diary-gateway.yaml`
  - `diary-system-service.yaml`
  - `diary-file-service.yaml`
  - `diary-diary-service.yaml`
  - `diary-blog-service.yaml`

Import them in the Nacos console or through the OpenAPI after Nacos is running.
The values use environment placeholders so Docker Compose, local shells, and
later deployment platforms can provide secrets without changing the files.

