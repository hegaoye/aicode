# aicode

Code generation framework based on Freemarker/MyBatis Plus + Spring Boot.

## Build & Run

```bash
cd aicode && ./gradlew bootRun   # dev server on :8080
cd aicode && ./gradlew build     # assemble
```

Java 21 required. Main class: `com.aicode.Application`.

## Modules

- `aicode/` — Spring Boot application (web, websocket, actuator)
- `common/` — shared library
- `facade/` — facade layer

## Database

Default H2 file-based: `jdbc:h2:file:/tmp/aicode`. Credentials: `sa/sa`.
H2 console at `http://127.0.0.1:8080/h2`.
Schema initialized from `aicode/src/main/resources/db/schema.sql`.

## Default Credentials

Web UI: `admin` / `888888`

## Key Dependencies

- Spring Boot 3.3.9, Spring MVC + WebSocket
- MyBatis Plus 3.5.7 + Druid 1.2.22
- Freemarker 2.1.2, Beetl 3.15.12
- H2 (1.4.200), fastjson2, Hutool, PageHelper

## Ports

- App: `8080` (actuator management: `8088`)