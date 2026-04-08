-- ============================================================
--  EduLib — Admin Account Setup
--  Run this ONCE against your database before starting the app.
--
--  Password encoded below is BCrypt(cost=12) of: Admin@1234
--  Change it immediately after first login via:
--    PATCH /api/v1/users/me/password
-- ============================================================

USE edulib_db;

INSERT INTO users (name, email, password, role, enabled, created_at, updated_at)
VALUES (
    'Super Admin',
    'admin@edulib.com',
    '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMIN',
    true,
    NOW(),
    NOW()
);

-- Verify the admin was inserted
SELECT id, name, email, role, enabled, created_at FROM users WHERE role = 'ADMIN';

-- ============================================================
--  To promote an existing registered user to ADMIN:
--    UPDATE users SET role = 'ADMIN' WHERE email = 'user@example.com';
--
--  To create more admins with a different password,
--  register normally via POST /api/v1/auth/register,
--  then promote them with the UPDATE above.
-- ============================================================
