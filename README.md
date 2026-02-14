# Eliteback

Spring Boot backend for authentication, profile, and subscription management with PostgreSQL, Redis, Stripe, and JWT.

## Features
- User registration, email verification, login, JWT auth
- Profile onboarding and photo upload
- Subscription management with Stripe
- Health check endpoint
- PostgreSQL and Redis integration
- OpenAPI/Swagger UI docs

## Requirements
- Java 21
- PostgreSQL
- Redis
- Stripe account (for production)

## Setup

### 1. Clone the repository
```sh
git clone <repo-url>
cd eliteback
```

### 2. Configure environment variables
Set these in your deployment or `.env` (for local dev, see `src/main/resources/application.properties`):
- `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`
- `REDIS_URL`
- `JWT_SECRET`
- `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, `STRIPE_PRICE_ID`
- `APP_BASE_URL`, `PORT`

### 3. Run PostgreSQL and Redis

### 4. Build and run the app
```sh
./gradlew bootRun
```

### 5. Access Swagger UI
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## API Endpoints
- `/api/v1/auth/signup` — регистрация
- `/api/v1/auth/verify-email` — подтверждение email
- `/api/v1/auth/login` — вход
- `/api/v1/auth/refresh` — обновление токена
- `/api/v1/profile/onboarding/*` — анкета пользователя
- `/api/v1/profile/photos` — загрузка фото
- `/api/v1/subscription/create` — создание подписки
- `/health` — проверка статуса

## Тестирование
- Интеграционные тесты: `./gradlew test`
- Swagger UI: используйте "Try it out" для ручного тестирования

## Stripe: получение paymentMethodId
1. На фронте используйте Stripe.js для создания paymentMethodId
2. Передайте его на backend в `/api/v1/subscription/create`

## Пример CURL для логина
```sh
curl -X POST \
  'http://localhost:8080/api/v1/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"email": "user@example.com", "password": "yourpassword"}'
```

## Лицензия
MIT
