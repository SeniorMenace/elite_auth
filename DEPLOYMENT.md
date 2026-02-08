# Deploy eliteback to Railway – Step-by-step roadmap

## 1. Before you start

- GitHub account (repo pushed with this project).
- Railway account: [railway.com](https://railway.com) → Sign up / Log in.
- Stripe account (for `STRIPE_SECRET_KEY`, `STRIPE_PRICE_ID`).
- SMTP provider for OTP emails (e.g. SendGrid, Mailgun, Resend) for `MAIL_*` vars.

---

## 2. Create a new project on Railway

1. Go to [railway.app](https://railway.app) → **Dashboard**.
2. **New Project**.
3. Choose **Deploy from GitHub repo**.
4. Connect GitHub (if needed) and select the **eliteback** repository.
5. Railway will add a **service** for the repo (this will be your Spring Boot app).

---

## 3. Add PostgreSQL

1. In the same project, click **+ New**.
2. Select **Database** → **PostgreSQL**.
3. Wait until the Postgres service is running.
4. Click the Postgres service → **Variables** (or **Connect**).
5. Note the variables Railway provides, e.g.:
   - `PGHOST`
   - `PGPORT`
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`
   - Sometimes `DATABASE_URL`

---

## 4. Add Redis

1. In the project, click **+ New**.
2. Select **Database** → **Redis**.
3. Wait until Redis is running.
4. Click the Redis service → **Variables**.
5. Note **REDIS_URL** (or host/port/password).

---

## 5. Configure your app service (eliteback)

1. Click the service that is your **eliteback** app (the one from GitHub).
2. Go to **Variables** (or **Settings** → **Environment Variables**).
3. Add the variables below. Replace placeholders with your real values.

### Required variables

| Variable | Description | Example / where to get it |
|----------|-------------|---------------------------|
| `PORT` | Server port (Railway often sets this automatically; app uses it if present). | Leave to Railway or set `8080`. |
| `JWT_SECRET` | Secret for signing JWTs (min 32 chars). | Generate: e.g. 64-char random string. |
| `PGHOST` | Postgres host. | From Postgres service variables. |
| `PGPORT` | Postgres port. | From Postgres service variables. |
| `PGDATABASE` | Postgres database name. | From Postgres service variables. |
| `PGUSER` | Postgres user. | From Postgres service variables. |
| `PGPASSWORD` | Postgres password. | From Postgres service variables. |
| `REDIS_URL` | Full Redis URL. | From Redis service variables. |

### Optional but recommended (production)

| Variable | Description |
|----------|-------------|
| `STRIPE_SECRET_KEY` | Stripe secret key (live or test). |
| `STRIPE_PRICE_ID` | Stripe Price ID for subscription. |
| `STRIPE_WEBHOOK_SECRET` | For Stripe webhooks (if you add them). |
| `MAIL_HOST` | SMTP host (e.g. `smtp.sendgrid.net`). |
| `MAIL_PORT` | SMTP port (e.g. `587`). |
| `MAIL_USERNAME` | SMTP username / API user. |
| `MAIL_PASSWORD` | SMTP password / API key. |

### Reference (copy-paste template)

```env
# Railway often sets PORT automatically
PORT=8080

# JWT – generate a long random string (e.g. 64 chars)
JWT_SECRET=your-256-bit-secret-key-at-least-32-characters-long

# Postgres – from Railway Postgres service
PGHOST=
PGPORT=
PGDATABASE=
PGUSER=
PGPASSWORD=

# Redis – from Railway Redis service (e.g. redis://default:xxx@host:port)
REDIS_URL=

# Stripe
STRIPE_SECRET_KEY=
STRIPE_PRICE_ID=

# Mail (OTP)
MAIL_HOST=
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=
```

---

## 6. Run database schema (first time only)

Railway Postgres starts empty. You must create tables once.

**Option A – Railway Postgres shell**

1. Postgres service → **Data** or **Query** (or use “Connect” to get a connection string).
2. Open a SQL tab and run the contents of **`src/main/resources/schema.sql`** (copy-paste and execute).

**Option B – Local run against Railway DB**

1. In your app’s Variables, copy `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` into a local `.env` or run config.
2. Point the app to that DB (see “Places you must change” below) and run the app once so it can connect; run `schema.sql` manually with a SQL client (e.g. DBeaver, psql) using the same credentials.

**Option C – Temporary ddl-auto (not for long-term production)**

- In Variables, set `SPRING_JPA_HIBERNATE_DDL_AUTO=update` for the **first deploy only** so Hibernate creates tables.
- After the first successful run, remove it or set back to `validate` and rely on `schema.sql` for future changes.

---

## 7. Deploy

1. **If using Dockerfile (recommended)**  
   - Ensure **Dockerfile** exists in the repo root (see “Places you must change”).  
   - In the app service → **Settings** → **Build**: set **Builder** to **Dockerfile** (or leave Railway to detect it).  
   - Push to the branch Railway watches; build and deploy will run automatically.

2. **If using Nixpacks / Gradle**  
   - No Dockerfile: Railway may detect Gradle and run a build.  
   - Ensure **Root Directory** and **Build Command** in **Settings** point to the project root and something like `./gradlew build -x test` (or `gradle build -x test`).  
   - **Start Command**: `java -Dserver.port=$PORT -jar build/libs/eliteback-*.jar` (adjust jar name to match `build.gradle`).

3. After deploy, open the **Generated URL** (e.g. `https://eliteback-production.up.railway.app`) and test:
   - `POST /api/v1/auth/signup`
   - Health/readiness if you add them later.

---

## 8. Optional: custom domain and HTTPS

- In the app service → **Settings** → **Domains** → **Custom Domain**.
- Add your domain and follow Railway’s DNS instructions (CNAME). Railway provides HTTPS.

---

## 9. Places in the project you must change (summary)

These changes are already applied in the repo; use this as a reference.

| Place | What was done |
|-------|----------------|
| **`src/main/resources/application.properties`** | Uses **`PORT`** for the server port; Postgres from **`PGHOST`**, **`PGPORT`**, **`PGDATABASE`**, **`PGUSER`**, **`PGPASSWORD`** (and optional **`PGSSLMODE=require`** on Railway); Redis from **`REDIS_URL`**; JWT/Stripe/Mail from env vars. |
| **`Dockerfile`** (repo root) | Added. Builds with Gradle and runs `java -jar`; reads **`PORT`** at runtime. Railway can use this as the build method. |
| **`.dockerignore`** (repo root) | Added. Speeds up Docker build by excluding `.git`, `build`, etc. |
| **`config/WebMvcConfig.java`** | No change; upload dir comes from `app.upload.dir`. |
| **`service/FileStorageService.java`** | No change. On Railway, local disk is ephemeral; for production you may later switch to S3/Cloudinary. |
| **Database** | Run **`src/main/resources/schema.sql`** once on Railway Postgres (step 6). |
| **Environment variables** | Set all variables in the Railway app service (step 5); no secrets in the repo. |

---

## 10. Checklist before going live

- [ ] All env vars set in Railway (JWT, PG*, REDIS_URL, Stripe, Mail).
- [ ] `schema.sql` executed once on Railway Postgres.
- [ ] No `spring.jpa.hibernate.ddl-auto=update` in production (use `validate` and migrations/schema).
- [ ] JWT_SECRET is strong and not default.
- [ ] Stripe keys and Mail credentials are production (or test) and correct.
- [ ] File upload: accept ephemeral local storage or plan migration to object storage.

---

## 11. If something goes wrong

- **Build fails**: Check **Deploy logs** (build phase). For Dockerfile, run `docker build .` locally.
- **App crashes at startup**: Check **Runtime logs**; often DB or Redis connection (wrong host/port/URL or SSL).
- **DB connection**: Ensure `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` match Postgres service. If Railway Postgres requires SSL, add variable **`PGSSLMODE=require`** (the app already appends it to the JDBC URL).
- **Redis**: Ensure `REDIS_URL` is the full URL (e.g. `redis://default:password@host:port`).
