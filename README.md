# EduLib — Educational Resource Library

Full-stack educational library platform.
Backend: Spring Boot 3.2 · MySQL · JWT
Frontend: React (Vite) · Tailwind CSS · Axios

---

## Backend — Quick Start

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3.8+

### 1. Create the database
```sql
CREATE DATABASE edulib_db;
```

### 2. Create the admin account
Start the app once so Hibernate creates the schema, then run:
```sql
USE edulib_db;
INSERT INTO users (name, email, password, role, enabled, created_at, updated_at)
VALUES (
  'Super Admin',
  'admin@edulib.com',
  '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
  'ADMIN', true, NOW(), NOW()
);
```
Password: `Admin@1234` — change it immediately via `PATCH /api/v1/users/me/password`.

To promote any registered user to ADMIN:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'someone@example.com';
```

### 3. Configure (edit application.yml or set env vars)
```
DB_USERNAME=root
DB_PASSWORD=yourpassword
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
FILE_UPLOAD_DIR=./uploads
```

### 4. Run
```bash
mvn clean spring-boot:run
# Server starts at http://localhost:8080
```

---

## Project Structure

```
src/main/java/com/edulib/
├── config/
│   ├── SecurityConfig.java          # JWT filter chain, role rules
│   └── FileStorageConfig.java       # PDF upload/download/delete
├── controller/
│   ├── AuthController.java          # /api/v1/auth/**
│   ├── BookController.java          # /api/v1/books/**
│   ├── ReviewController.java        # /api/v1/books/*/reviews, /api/v1/reviews/**
│   └── UserController.java          # /api/v1/users/me/**, /api/v1/auth/reset-password
├── dto/
│   ├── request/  AuthRequest, BookRequest, ReviewRequest,
│   │             ChangePasswordRequest, ResetPasswordRequest
│   └── response/ ApiResponse, AuthResponse, BookResponse,
│                 ReviewResponse, DownloadResponse
├── entity/
│   ├── User.java                    # Role: ADMIN | USER
│   ├── Book.java                    # PDF metadata + tags
│   ├── Review.java                  # rating 1-5 + comment
│   └── Download.java                # download history record
├── repository/
│   ├── UserRepository
│   ├── BookRepository               # JPQL search queries
│   ├── ReviewRepository             # avg rating, count
│   └── DownloadRepository           # history, counts
├── service/impl/
│   ├── AuthServiceImpl              # register, login
│   ├── BookServiceImpl              # CRUD + search + download recording
│   ├── ReviewServiceImpl            # add/edit/delete, ownership check
│   └── UserServiceImpl              # changePassword, resetPassword
├── security/
│   ├── JwtAuthenticationFilter      # OncePerRequestFilter, skips /error
│   ├── JwtAuthEntryPoint            # 401 JSON, logs original URI
│   └── CustomUserDetailsService
└── util/JwtUtil.java
```

---

## API Reference

### Auth (public)
| Method | Endpoint | Body |
|--------|----------|------|
| POST | /api/v1/auth/register | name, email, password |
| POST | /api/v1/auth/login | email, password |
| POST | /api/v1/auth/reset-password | email, newPassword, confirmPassword |

### Books
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | /api/v1/books | Public |
| GET | /api/v1/books/search?keyword= | Public |
| GET | /api/v1/books/{id} | Public |
| POST | /api/v1/books (multipart) | ADMIN |
| PUT | /api/v1/books/{id} | ADMIN |
| DELETE | /api/v1/books/{id} | ADMIN |
| GET | /api/v1/books/{id}/download | Authenticated |

### Reviews
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | /api/v1/books/{id}/reviews | Public |
| POST | /api/v1/books/{id}/reviews | Authenticated |
| PUT | /api/v1/reviews/{id} | Owner |
| DELETE | /api/v1/reviews/{id} | Owner or ADMIN |
| GET | /api/v1/reviews/my | Authenticated |

### User / Dashboard
| Method | Endpoint | Auth |
|--------|----------|------|
| PATCH | /api/v1/users/me/password | Authenticated |
| GET | /api/v1/users/me/downloads | Authenticated |
| GET | /api/v1/users/me/stats | Authenticated |

---

## Database Schema

```
users          id, name, email, password, role, enabled, created_at, updated_at
books          id, title, author, description, file_path, file_name, file_size,
               content_type, tags, created_at, updated_at
reviews        id, rating, comment, user_id→users, book_id→books,
               created_at, updated_at  [UNIQUE user_id+book_id]
downloads      id, user_id→users, book_id→books, downloaded_at
```

---

## Key Design Decisions

- `/error` is always `permitAll()` — prevents Spring's internal error forward from triggering 401
- `JwtAuthenticationFilter` skips `/error` explicitly — no JWT on internal forwards
- Download records written in the same transaction as the file read — atomic
- `GlobalExceptionHandler` catches every exception including `DataIntegrityViolationException` — nothing escapes to `/error`
- BCrypt cost 12, stateless JWT, `@EnableMethodSecurity` for method-level control
- User enumeration prevented in reset-password — same response whether email exists or not
