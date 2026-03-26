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
| movie-service | 8083 | Fetches movie data from TMDB API |
| review-service | 8084 | Stores and serves movie reviews (PostgreSQL) |
| forum-service | 8085 | Forum/discussion with role-based permissions (PostgreSQL) |

---

## Current Functionality

### UI (ui-service)
- **Home page** — browse a catalogue of films with a live search bar and collapsible filter panel
  - Search by title, director, cast, genre, overview, year, production company
  - Filter by genre, year range, min user score, min TMDB score, original language
  - Sort by title, year, user score, TMDB score, or runtime
  - Each card shows both the TMDB score (🎬) and our users' average score (👥)
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

### Live vs Dummy Data

| Data | Source |
|---|---|
| Movie detail pages | **Live** — api-service → movie-service → TMDB |
| Movie poster & cast images | **Live** — loaded directly from TMDB image CDN |
| Reviews on movie pages | **Live** — review-service |
| User score on movie pages | **Live** — calculated from review-service average |
| Home page grid | **Live** — uses now_playing, trending, and search endpoint |
| Account page reviews | **Dummy** — labelled `[DUMMY]` — pending user-service integration |
| User accounts | **In-memory** — pending user-service integration |

All dummy data is clearly prefixed with `[DUMMY]` in the UI so it cannot be mistaken for real API data.

---

## Running with Docker (Recommended)

Docker is the easiest way to run the full stack — no local Java or PostgreSQL installation required.

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- A TMDB Read Access Token — get one free at https://www.themoviedb.org/settings/api
- MongoDB URI

> **Note:** The MongoDB URI will only be given to the development team.

### Setup

1. Copy the example env file and fill in your values:

```
cp .env.example .env
```

Edit `.env`:
```
TMDB_TOKEN=your_tmdb_read_access_token_here
MONGODB_URI=your_given_mongodb_uri
DB_PASSWORD=your_chosen_db_password
MAIL_USERNAME=your_gmail_address@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

> **Important:** `DB_PASSWORD` is required for PostgreSQL authentication. The password can be up to you. Just note that after the initial `docker compose up`, you must use the current password that you have written. In order to change the password, do `docker compose down -v`.
> You can then change your `DB_PASSSWORD` in the .env, then procced to use `docker compose up`.
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

The PostgreSQL data volume persists between restarts. To wipe the database and start fresh:
```
docker compose down -v
```

---

## Running Manually (Without Docker)

### Prerequisites

- Java 21
- PostgreSQL (for review-service and user-service)
- A TMDB API token
- MongoDB URI

> **Note:** The MongoDB URI will only be given to the development team.

### Setup

Create three PostgreSQL databases:
- `review_service`
- `user_service`
- `forum_service`

### Starting Each Service

Open a separate terminal for each service.

**movie-service:** 
```bash
# Windows
setx TDMB_TOKEN "your_token_here"
setx MONGODB_URI "your_given_uri_here"
cd movie-service && .\mvnw.cmd spring-boot:run

# Mac/Linux
export TDMB_TOKEN=your_token_here
export MONGODB_URI=your_given_uri_here
cd movie-service && ./mvnw spring-boot:run
```

**api-service:**
```bash
cd api-service
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Mac/Linux
```

**review-service:**
```bash
cd review-service
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Mac/Linux
```

**user-service:**
```bash
cd user-service
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Mac/Linux
```

**forum-service:**
```bash
cd forum-service
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Mac/Linux
```

**ui-service:**
```bash
cd ui-service
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Mac/Linux
```

Then open http://localhost:8080.

> **Note:** The first run after a clean checkout takes 2–5 minutes while Vaadin downloads Node.js and compiles the frontend bundle. Use `spring-boot:run` (not `mvn clean spring-boot:run`) to preserve the dev bundle cache between runs.

---

## Logging In

The UI currently uses in-memory accounts pre-seeded on startup:

| Username | Password |
|---|---|
| alice | password |
| bob | password |

You can also register a new account — it persists for the duration of the session.

---

## Forum API Usage

The forum-service provides REST endpoints for managing forum posts:

### Basic Operations
```bash
# Get all posts
curl http://localhost:8085/forum/posts

# Create a post
curl -X POST http://localhost:8085/forum/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"My Post","content":"Post content","userId":1}'

# Get specific post
curl http://localhost:8085/forum/posts/1
```

### Role-based Deletion
```bash
# User deletes own post (succeeds)
curl -X DELETE "http://localhost:8085/forum/posts/1?userId=1&userRole=USER"

# User tries to delete others' post (fails with 403)
curl -X DELETE "http://localhost:8085/forum/posts/2?userId=1&userRole=USER"

# Admin deletes any post (succeeds)
curl -X DELETE "http://localhost:8085/forum/posts/1?userId=2&userRole=ADMIN"
```

---

## What's Still Pending
- **Persistent user accounts** — replace in-memory auth with real user-service calls; also enables correct user ID on submitted reviews
- **Username on reviews** — review-service needs to enrich `ReviewDTO` with usernames fetched from user-service
- **Account page reviews** — requires real auth (above) to look up the current user's review history
