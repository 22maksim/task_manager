## 👉 Task Management – Java Spring Boot Приложение

### 📦 Стек технологий

- Java 17 + Spring Boot
- PostgreSQL
- Redis
- Spring Security + JWT
- Docker + Docker Compose
- Gradle

---

#### 1. Клонируй репозиторий
```bash
git clone https://github.com/your-username/task_management.git
cd task_management
```

#### 3. Собери jar-файл
```bash
./mvnw clean package
``` 

_Убедись, что в `target/` появился файл `task_management-0.0.1-SNAPSHOT.jar`._

#### 4. Запусти контейнеры через Compose
```bash
docker compose -f compose.yaml up --build
```

_Если у тебя не работает `docker compose`, используй:_
```bash
docker-compose -f compose.yaml up --build
```

---

### 🌐 Доступ к приложению

Приложение будет доступно по адресу:

```
http://localhost:8085
```

---

### 🛠 Полезные команды

Остановить и удалить контейнеры:

```bash
docker compose -f compose.yaml down
```

Просмотреть логи:

```bash
docker compose -f compose.yaml logs -f
```

