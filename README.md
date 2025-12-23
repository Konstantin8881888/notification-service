### Микросервис для отправки email-уведомлений с использованием Spring Boot и Kafka.

Приложение представляет собой микросервис, который получает события о действиях с пользователями из Kafka и отправляет соответствующие email-уведомления. Также предоставляет REST API для ручной отправки уведомлений.

### Особенности:

- Получение событий из Apache Kafka о создании и удалении пользователей
- Отправка email-уведомлений пользователям (в мок-режиме без реальной отправки)
- REST API для ручной отправки уведомлений
- Spring Boot - автоматическая настройка компонентов
- Spring Dependency Injection через конструкторы
- Spring Test Framework для тестирования
- Глобальная обработка исключений с возвратом структурированных ошибок в формате JSON
- Поддержка HATEOAS (гипермедиа) в REST API для навигации по ресурсам
- Автоматическая генерация документации API через Swagger/OpenAPI 3.0
- Подробное логирование работы с Kafka и email-сервисом

### Основные технологии:

- Java 17 - язык программирования
- Spring Boot 3.5.8 - основной фреймворк
- Spring Web - для создания REST API
- Spring for Apache Kafka - для получения событий из Kafka
- Lombok - для сокращения шаблонного кода
- Mockito - для модульного тестирования
- Spring Kafka Test (EmbeddedKafka) - для интеграционного тестирования
- Maven - для управления зависимостями и сборки
- Spring HATEOAS - для реализации REST API с гипермедиа
- SpringDoc OpenAPI - для автоматической генерации документации API
- Spring Validation - для валидации входных данных
- Spring Test Framework - для модульного и интеграционного тестирования

---

## Архитектура и интеграция

Микросервис получает события из топика Kafka `user-events`, отправляемые микросервисом user-service (Module2).

### Получаемые события Kafka:
- **Топик:** `user-events`
- **Группа потребителей:** `notification-group`
- **Содержимое:** email пользователя и тип операции (`CREATE` или `DELETE`)

### Обработка событий:
- При операции `CREATE` - отправляется приветственное письмо
- При операции `DELETE` - отправляется письмо об удалении аккаунта

---

## Тексты email-уведомлений

### Приветственное письмо (создание аккаунта):
**Тема:** Добро пожаловать!

**Текст:** Здравствуйте! Ваш аккаунт на сайте был успешно создан.

### Письмо об удалении аккаунта:
**Тема:** Ваш аккаунт удален

**Текст:** Здравствуйте! Ваш аккаунт был удалён.

---

## Тестирование

### Проект включает следующие тесты:

### Проект включает следующие тесты:

1. **Интеграционные тесты (KafkaIntegrationTest)**
    - Используют EmbeddedKafka для тестирования взаимодействия с Kafka
    - Проверяют получение событий и вызов email-сервиса
    - Тестируют обработку событий CREATE и DELETE

2. **Модульные тесты**
    - **UserEventsConsumerTest** - тестирование обработки событий Kafka
    - **NotificationControllerTest** - тестирование REST API контроллера с HATEOAS
    - **EmailServiceTest** - тестирование логики отправки email (реальный и мок режимы)
    - **GlobalExceptionHandlerUnitTest** - тестирование обработки исключений

3. **Интеграционные тесты обработки исключений**
    - **GlobalExceptionHandlerIntegrationTest** - тестирование глобального обработчика исключений
    - Проверка валидации параметров запроса и тела запроса
    - Тестирование обработки `MissingServletRequestParameterException`
    - Тестирование обработки `MethodArgumentTypeMismatchException`
    - Тестирование обработки `ConstraintViolationException`
    - Тестирование обработки `MethodArgumentNotValidException`

4. **Контроллер тесты с MockMvc**
    - Тестирование всех REST endpoints
    - Проверка HATEOAS ссылок в ответах
    - Тестирование валидации входных данных
    - Тестирование обработки ошибок

---

## REST API Endpoints

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/api/notifications/welcome` | Ручная отправка приветственного письма |
| `POST` | `/api/notifications/deletion` | Ручная отправка письма об удалении |
| `GET`  | `/api/notifications/health` | Проверка состояния сервиса |

### Параметры запросов:
- `email` (обязательный) - email адрес получателя

---

## Документация API

Сервис предоставляет автоматически сгенерированную документацию API через Swagger UI.

### Доступные URL:
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **OpenAPI спецификация**: `http://localhost:8081/api-docs`

### Особенности документации:
- Полное описание всех endpoints с примерами запросов и ответов
- Автоматическое обнаружение и описание моделей данных
- Интерактивный интерфейс для тестирования API
- Поддержка HATEOAS ссылок в документации
- Документация для всех ошибок валидации

### Основные разделы документации:
1. **Уведомления** - REST API для отправки email-уведомлений
2. **Модели данных** - описание всех DTO объектов
3. **Ошибки** - форматы ответов при различных ошибках

---

## Примеры запросов

### 1. Ручная отправка приветственного письма

**Запрос:**

    POST /api/notifications/welcome?email=user@example.com
    Host: localhost:8081
    Content-Type: application/json

**Пример ответа (200 OK):**

    {
        "message": "Приветственное письмо отправлено",
        "email": "user@example.com",
        "notificationType": "WELCOME",
        "_links": {
        "self": {
        "href": "http://localhost:8081/api/notifications/welcome?email=user%40example.com"
            },
        "send-deletion": {
        "href": "http://localhost:8081/api/notifications/deletion?email=user%40example.com"
            },
        "health": {
        "href": "http://localhost:8081/api/notifications/health"
            }
        }
    }

---

### 2. Ручная отправка письма об удалении

**Запрос:**

    POST /api/notifications/deletion?email=user@example.com
    Host: localhost:8081
    Content-Type: application/json

**Пример ответа (200 OK):**

    {
        "message": "Письмо об удалении отправлено",
        "email": "user@example.com",
        "notificationType": "DELETION",
        "_links": {
        "self": {
        "href": "http://localhost:8081/api/notifications/deletion?email=user%40example.com"
            },
        "send-welcome": {
        "href": "http://localhost:8081/api/notifications/welcome?email=user%40example.com"
            },
        "health": {
        "href": "http://localhost:8081/api/notifications/health"
            }
        }
    }

---

### 3. Проверка состояния сервиса

**Запрос:**

    GET /api/notifications/health
    Host: localhost:8081
    Content-Type: application/json

**Пример ответа (200 OK):**

    {
        "status": "RUNNING",
        "timestamp": "2025-12-22T23:38:30.500917500Z",
        "serviceName": "notification-service",
        "_links": {
        "self": {
        "href": "http://localhost:8081/api/notifications/health"
            },
        "send-welcome": {
        "href": "http://localhost:8081/api/notifications/welcome?email=",
        "title": "Send welcome email"
            },
        "send-deletion": {
        "href": "http://localhost:8081/api/notifications/deletion?email=",
        "title": "Send deletion email"
            }
        }
    }

---

## Конфигурация

### Основные настройки (application.properties):
- `server.port=8081` - порт приложения
- `logging.level.org.klimtsov.notification=DEBUG` - уровень логирования сервиса

#### Настройки Kafka:
- `spring.kafka.bootstrap-servers=localhost:9092` - адрес Kafka
- `spring.kafka.consumer.group-id=notification-group` - группа потребителей
- `spring.kafka.consumer.properties.allow.auto.create.topics=false` - запрет автоматического создания топиков

#### Настройки KRaft (Kafka без Zookeeper):
- `spring.kafka.consumer.properties.session.timeout.ms=45000`
- `spring.kafka.consumer.properties.heartbeat.interval.ms=3000`
- `spring.kafka.consumer.properties.max.poll.interval.ms=300000`
- `spring.kafka.consumer.properties.request.timeout.ms=40000`

#### Настройки отправки на email:
- `notification.email.mock=true` - режим мок-отправки email (логирование вместо реальной отправки), false - реальная отправка.
- `spring.mail.username=${SMTP_USERNAME:}` - адрес SMTP-сервера (только для реальной отправки).
- `spring.mail.password=${SMTP_PASSWORD:}` - пароль SMTP-сервера (только для реальной отправки).

### Режим работы с email:
Используется, в зависимости от настроек в application.properties (см. выше в основных настройка), мок-отправка email - все письма логируются в консоль вместо реальной отправки или реальная отправка на электронную почту пользователя.

### Отправка на реальный email:
- **SMTP сервер** - используется для отправки писем
- **Отправитель** - все письма приходят с одного адреса
- **Любые получатели** - можно отправлять на любые email домены (gmail.com, mail.ru, yandex.ru и др.)

### Настройка для Gmail:
1. **Создайте пароль приложения для Gmail:**
    - Включите двухфакторную аутентификацию в Google аккаунте
    - Перейдите: https://security.google.com/settings/security/apppasswords
    - Создайте пароль для "Почта"

### Альтернативные SMTP серверы:
**Yandex (нужен пароль приложения):**

    SMTP_HOST=smtp.yandex.ru
    SMTP_PORT=587

**Mail.ru:**

    SMTP_HOST=smtp.mail.ru
    SMTP_PORT=465

**Outlook/Office365:**

    SMTP_HOST=smtp.office365.com
    SMTP_PORT=587

---

## HATEOAS (Гипермедиа)

Сервис реализует принцип HATEOAS (Hypermedia as the Engine of Application State), что позволяет клиентам динамически обнаруживать доступные действия через гипермедиа ссылки.

### Преимущества:
- **Самоописательность API**: Клиенты могут навигаровать по API без предварительного знания структуры
- **Динамическое обнаружение**: Ссылки на связанные ресурсы включаются в каждый ответ
- **Лучшая связность**: Клиенты следуют ссылкам вместо хардкодинга URL

### Пример использования:
1. Клиент запрашивает `/api/notifications/health`
2. В ответе получает ссылки на `/api/notifications/welcome` и `/api/notifications/deletion`
3. Клиент может использовать эти ссылки для выполнения соответствующих действий

### Поддерживаемые отношения (rel):
- `self` - ссылка на текущий ресурс
- `send-welcome` - отправка приветственного письма
- `send-deletion` - отправка письма об удалении
- `health` - проверка состояния сервиса

---

