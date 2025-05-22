CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    role       VARCHAR(50),
    enabled    BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE  DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE  DEFAULT now()
);
