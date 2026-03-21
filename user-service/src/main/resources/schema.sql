-- PostgreSQL schema for user-service

CREATE TABLE IF NOT EXISTS users (
    id               BIGSERIAL PRIMARY KEY,
    username         VARCHAR(255) NOT NULL UNIQUE,
    email            VARCHAR(255) NOT NULL UNIQUE,
    email_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    over18           BOOLEAN      NOT NULL DEFAULT FALSE,
    verification_code VARCHAR(255),
    password         VARCHAR(255) NOT NULL,
    role             VARCHAR(255) NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS user_liked_genres (
    user_id BIGINT       NOT NULL,
    genre   VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, genre),
    CONSTRAINT fk_user_liked_genres_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS user_watchlist (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id INTEGER NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, movie_id),
    CONSTRAINT fk_user_watchlist_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);
