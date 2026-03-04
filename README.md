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
| forum-service | 8085 | Forum/discussion (not yet implemented) |

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

### What is live vs dummy data
| Data | Source |
|---|---|
| Movie detail pages | **Live** — api-service → movie-service → TMDB (falls back to dummy if services are down) |
| Movie poster & cast images | **Live** — loaded directly from TMDB image CDN |
| Reviews on movie pages | **Live** — review-service |
| User score on movie pages | **Live** — calculated from review-service average |
| Home page grid | **Dummy** — pending a list/search endpoint on movie-service |
| User accounts | **In-memory** — pending user-service integration |

---

## Prerequisites

- Java 21
- PostgreSQL (for review-service and user-service)
- A TMDB API token (for movie-service) — get one free at https://www.themoviedb.org/settings/api

---

## Running the Services

Each service has its own Maven wrapper. Open a separate terminal for each.

### 1. movie-service

Set your TMDB token, then run:

**Windows**
```
setx TDMB_TOKEN "your_token_here"
cd movie-service
.\mvnw.cmd spring-boot:run
```

**Mac/Linux**
```
export TDMB_TOKEN=your_token_here
cd movie-service
./mvnw spring-boot:run
```

### 2. api-service
```
cd api-service

# Windows
.\mvnw.cmd spring-boot:run

# Mac/Linux
./mvnw spring-boot:run
```

### 3. review-service

Requires PostgreSQL running with a database called `review_service`.

```
cd review-service

# Windows
.\mvnw.cmd spring-boot:run

# Mac/Linux
./mvnw spring-boot:run
```

### 4. user-service

Requires PostgreSQL running with a database called `UserService` (or `user_service` depending on branch).

```
cd user-service

# Windows
.\mvnw.cmd spring-boot:run

# Mac/Linux
./mvnw spring-boot:run
```

### 5. ui-service
```
cd ui-service

# Windows
.\mvnw.cmd spring-boot:run

# Mac/Linux
./mvnw spring-boot:run
```

Then open http://localhost:8080 in your browser.

> **Note:** The first run after a clean checkout will take 2–5 minutes while Vaadin downloads Node.js and compiles the frontend bundle. Subsequent runs are fast. Use `spring-boot:run` (not `mvn clean spring-boot:run`) to preserve the dev bundle cache.

---

## Logging In

The UI currently uses in-memory accounts that are pre-seeded on startup:

| Username | Password |
|---|---|
| alice | password |
| bob | password |

You can also register a new account — it will persist for the duration of the session.

---

## Running Without Backend Services

ui-service will start and function on its own. Movie detail pages and reviews will fall back to dummy data automatically if api-service, movie-service, or review-service are not running. The home page grid always uses dummy data until a movie list endpoint is added to movie-service.

---

## What's Still Pending

- **Home page grid** — movie-service needs a `GET /movie/popular` or `/movie/search` endpoint calling TMDB discover/search
- **Persistent user accounts** — replace in-memory auth with real user-service calls; this also enables the correct user ID to be attached to submitted reviews
- **Username on reviews** — review-service needs to enrich `ReviewDTO` with usernames from user-service