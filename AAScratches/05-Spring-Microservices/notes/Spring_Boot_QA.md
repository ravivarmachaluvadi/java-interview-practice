# Spring Boot Q&A

Interview-prep notes on Spring Boot; this file currently covers one topic: the precedence of the property sources from which Spring Boot resolves `server.port` (or any other property).

## 1. Where can `server.port` be set, and which property source wins when several are present?

The same property is set in seven different places below, listed from lowest to highest precedence. The summary at the end shows which one wins.

### 1. `src/main/resources/application.yml` (properties > yaml)

```yaml
server:
  port: 8080
```

### 2. External `/config/application.yml`

```yaml
server:
  port: 9090
```

### 3. Profile file `application-dev.yml`

```yaml
server:
  port: 8181
```

### 4. System property

```bash
-Dserver.port=9191
```

### 5. Environment variable

```bash
export SERVER_PORT=9292
```

### 6. Command-line argument

```bash
java -jar myapp.jar --server.port=9393
```

### 7. Spring Cloud Config (remote)

```properties
server.port=9494
```

### Summary (highest wins)

```text
Config Server (if used)
↑
Command-line args
↑
Environment variables
↑
System properties (-D)
↑
Profile-specific files (application-dev.yml)
↑
External config files (same dir/config/)
↑
application.yml / application.properties (inside JAR)
```
