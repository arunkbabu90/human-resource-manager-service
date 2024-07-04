CREATE TABLE IF NOT EXISTS permission (
    id SERIAL PRIMARY KEY,
    name VARCHAR(512) UNIQUE
);

CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(1024) NOT NULL,
    is_blocked BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS permission_role (
    permission_id INT,
    role_id INT,
    CONSTRAINT fk_permission
        FOREIGN KEY(permission_id)
        REFERENCES permission(id),
    CONSTRAINT fk_role
        FOREIGN KEY(role_id)
        REFERENCES role(id)
);

CREATE TABLE IF NOT EXISTS role_user (
    role_id INT,
    user_id INT,
    CONSTRAINT fk_role_user_role
        FOREIGN KEY(role_id)
        REFERENCES role(id),
    CONSTRAINT fk_role_user_user
        FOREIGN KEY(user_id)
        REFERENCES "user"(id)
);

