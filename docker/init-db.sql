-- Runs once when the PostgreSQL container is first created.
-- Creates the databases needed by user-service, review-service, and forum-service.

CREATE DATABASE user_service;
CREATE DATABASE review_service;
CREATE DATABASE forum_service;