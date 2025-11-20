## Deploying to Railway (no Docker)

1. **Create a Railway project**
   - `railway init` in the repo or create a new project from https://railway.app.
   - Choose the Maven template or “Deploy from GitHub”.

2. **Set build & start commands**
   - Build: `./mvnw -DskipTests package`
   - Start: `java -jar target/ev-trade-0.0.1-SNAPSHOT.jar`

3. **Provision a database**
   - Add the SQL Server (or preferred) plugin.
   - Railway exposes the connection string with `RAILWAY_DATABASE_URL`, plus username/password variables. Use these variable names in the service.

4. **Configure environment variables**
   | Variable | Purpose |
   | --- | --- |
   | `SPRING_PROFILES_ACTIVE` | set to `railway`. |
   | `RAILWAY_DATABASE_URL` | JDBC URL from the DB plugin. |
   | `RAILWAY_DATABASE_USERNAME` | DB user from Railway. |
   | `RAILWAY_DATABASE_PASSWORD` | DB password from Railway. |
   | `RAILWAY_DATABASE_DRIVER` | driver FQN (default SQL Server). |
   | `RAILWAY_HIBERNATE_DIALECT` | override Hibernate dialect if DB ≠ SQL Server. |
   | `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | Gmail credentials. |
   | `JWT_SECRET` | JWT signing key (256-bit). |
   | `STRIPE_*` / `VNPAY_*` / `GOOGLE_*` | Payment & OAuth secrets. |

   > Any variable left empty falls back to the default already defined in `application.properties`, so local development keeps working.

5. **Ports**
   - Railway injects `PORT`. We already bind `server.port=${PORT:8080}`, so no action needed.

6. **Run migrations / seed data (optional)**
   - Use `railway run ./mvnw spring-boot:run -Dspring-boot.run.arguments=--some-arg` if you need to create initial data.

7. **Redeploy**
   - Trigger via `git push` (if GitHub connected) or `railway up`.
   - Tail logs with `railway logs`.


