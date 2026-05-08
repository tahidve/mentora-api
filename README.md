# Mentora API — Mentor-Mentee Platform Backend

A RESTful API backend powering a comprehensive mentor-mentee connection platform. This project evolved from a university Database course assignment into a fully deployable backend service.

---

## Background

This project originated as a group **Database Systems course assignment** at HCMUT. Building upon the EERD (Enhanced Entity-Relationship Diagram) and core business logic collaboratively designed by my team. Using Spring Boot, I translated our database design into a fully functional RESTful API.

---

## Key Features

- **Authentication & Authorization:** JWT-based auth with access token + HttpOnly refresh token rotation, role-based access control (`ADMIN`, `MENTOR`, `MENTEE`), OTP email verification
- **Mentor Management:** Profile with avatar upload, work experience, multi-skill tagging, approval workflow with admin review
- **Plan System:** JPA Inheritance pattern (`PlanSession`, `PlanMentorship`) with slot scheduling
- **Booking Flow:** Transactional booking with discount codes, invoice generation, and meeting lifecycle management
- **Payment Integration:** Stripe Connect for mentor onboarding and checkout session; webhook handler for payment confirmation
- **Search & Filter:** Full-text mentor search with Turkraft Spring Filter library supporting dynamic query filters
- **Email Notifications:** Transactional emails via Spring Mail
- **Admin Module:** Dashboard stats, mentor approval/rejection, invoice management
- **Production Deployment:** Containerized with Docker, reverse proxied through Caddy with auto-HTTPS

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2, Spring Security 6.x |
| Database | MySQL 8.0 + Hibernate ORM |
| Build Tool | Gradle (Kotlin DSL) |
| Auth | JWT (Spring OAuth2 Resource Server) |
| Payment | Stripe Connect API |
| Email | Spring Mail|
| Search | Turkraft Spring Filter |
| Deployment | Docker, Caddy |

---
## Running Locally

### Prerequisites
- Java 17+
- MySQL 8.0
- Gradle

### Setup

1. Clone the repo:
```bash
git clone https://github.com/tahidve/mentora-api.git
cd mentora-api
```

2. Create `src/main/resources/application-dev.properties` from the template:
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/mentora
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Mail (Mailtrap sandbox)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=your_mailtrap_username
spring.mail.password=your_mailtrap_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Upload path
app.upload-file.base-uri=file:///your/local/upload/path/
```

3. Set environment variables (or use `.env`):
```env
JWT_SECRET=your_base64_jwt_secret
STRIPE_SECRET_KEY=sk_test_xxx
STRIPE_WEBHOOK_SECRET=whsec_xxx
```

4. Run:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

API will be available at `http://localhost:8080`

---

## Project Structure

```
src/main/java/vn/mentora/mentora_api/
├── config/        # Security, CORS, Static Resources
├── controller/    # REST Controllers
├── domain/        # Entities, Request/Response DTOs
│   ├── request/
│   └── response/
├── repository/    # Spring Data JPA Repositories
├── service/       # Business Logic
└── util/          # Security utils, annotations, error handling
```

---

