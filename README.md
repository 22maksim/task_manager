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
./gradlew clean build
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

Доступно для Swagger http://localhost:8085/swagger-ui/index.html#/

Приложение будет доступно по адресу:

```
http://localhost:8085
```

---

### Безопасность

```
Тебе будет доступен администратор для того чтобы начать добавлять новых админов.
Задачами управлять может только админ.
'email': ouremail@mail.com
'password': 1408password
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

