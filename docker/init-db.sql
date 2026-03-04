-- Runs once when the PostgreSQL container is first created.
-- Creates the two databases needed by user-service and review-service.

CREATE DATABASE user_service;
CREATE DATABASE review_service;