# Absolute Cinema

An EECS 4314 project — a movie review platform built as a Spring Boot microservices application.

> This project uses the TMDB API but is not endorsed or certified by TMDB.
> ![TMDB Logo](https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_2-d537fb228cf3ded904ef09b136fe3fec72548ebc1fea3fbbd1ad9e36364db38b.svg)

---

## Services & Ports

| Service | Port | Description |
|---|---|---|
| ui-service | 8080 | Vaadin frontend |
| api-service | 8081 | Gateway — proxies requests to backend services |
| user-service | 8082 | User accounts and authentication (PostgreSQL) |
| movie-service | 8083 | Fetches movie data from TMDB API and caches (MongoDB andcaffeine |
| review-service | 8084 | Stores and serves movie reviews (PostgreSQL) |
| forum-service | 8085 | Forum/discussion with role-based permissions (PostgreSQL) |

---

## Functionality

### UI (ui-service)
- **Home page** — browse a catalogue of films in addition to a working search bar 
  - Search by title.
  - Filter search results by genre.
  - View now playing and trending movies.
  - Recommended Movies (Signed-in)
- **Movie detail page** — click any film card to open a full detail page showing:
  - Poster image (from TMDB), title, tagline, genres, runtime, release year
  - Dual score display — TMDB score alongside our users' average review score
  - Director and top-billed cast with profile photos
  - Film details — budget, box office, production companies, language, runtime
  - Reviews section — top recommended review + 3 most recent
  - **Write a Review** button (requires login) — opens a modal dialog with rating, title, body, spoiler toggle; submits directly to review-service
- **Login / Register** — session-based auth (currently in-memory; user-service integration pending)
- **Account page** — view profile info and review history
- **Edit profile** — update username, email, password, date of birth, genres

### Forum (forum-service)
- **Create posts** — users can create forum posts with title and content
- **View posts** — browse all forum posts with GET /forum/posts
- **Role-based deletion** — 
  - **ADMIN users** can delete any post
  - **Regular users** can only delete their own posts
  - Legacy posts without owner information can only be deleted by admins
- **Permission validation** — DELETE requests require userId and userRole parameters

> **Note**: The Admin account can be accessed by using the username and password ADMIN123.
---

## Running with Docker (Local Deployment)

Docker is the easiest way to run the full stack — no local Java, PostgreSQL or MongoDB installation required.

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- A TMDB Read Access Token — get one free at https://www.themoviedb.org/settings/api
- Gmail with an App Password (Provided Gmail will be used to send verification codes)

### Setup

1. Copy the example env file and fill in your values:

```
cp .env.example .env
```

Edit `.env`:
```
TMDB_TOKEN=your_tmdb_read_access_token_here
MONGO_DB_PASSWORD=your_chosen_db_password
DB_PASSWORD=your_chosen_db_password
MAIL_USERNAME=your_gmail_address@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

> **Note**: Both MONGO_DB_PASSWORD and DB_PASSWORD can be any password, it's up to you. In the docker compose, the database user for both Postgres and Mongo is set up to have admin privileges.

> **Important:**
> `MAIL_USERNAME` and `MAIL_PASSWORD` are required for email verification to work. Use a Gmail address and a [Gmail App Password](https://myaccount.google.com/apppasswords) (not your regular Gmail password). Each developer needs to set these to their own values locally.
> `.env` is gitignored and must be created manually on each machine you use.

2. Build and start all services:

```
docker compose up --build
```

The first build takes 5–10 minutes (Maven downloads dependencies, Vaadin compiles the frontend bundle). Subsequent starts are fast:

```
docker compose up
```

3. Open http://localhost:8080

### Rebuilding after code changes

If you change source code in a service, rebuild just that service before starting:

```
docker compose build <service-name>
docker compose up
```

For example, after changing ui-service:
```
docker compose build ui-service
docker compose up
```

To force a full clean rebuild of everything (e.g. after Dockerfile changes):
```
docker compose build --no-cache
docker compose up
```

### Stopping

```
Ctrl+C
```

or to also remove containers:
```
docker compose down
```

The PostgreSQL and MongoDB data volume persists between restarts. To wipe the database and start fresh:
```
docker compose down -v
```

---
