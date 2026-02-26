# Absolute Cinema (Reviews)

An EECS 4314 project.

## Services

- **movie-service** - Manages movie data
- **user-service** - Handles user accounts
- **review-service** - Manages movie reviews
- **forum-service** - Handles discussion/forum functionality
- **ui-service** - Vaadin front-end UI

## Running the UI

### Prerequisites
- Java 21
- The `mvnw.cmd` (Windows) or `mvnw` (Mac/Linux) wrapper is included in `ui-service/` — no separate Maven install needed

### Steps

1. Clone the repo and navigate to the ui-service folder:
   ```
   cd ui-service
   ```

2. Run the service:
   ```
   # Windows
   .\mvnw.cmd spring-boot:run

   # Mac/Linux
   ./mvnw spring-boot:run
   ```

3. Open your browser and go to:
   ```
   http://localhost:8080
   ```

4. Log in with one of the placeholder accounts:
   | Username | Password |
   |----------|----------|
   | alice    | password |
   | bob      | password |

5. To stop the server, press `Ctrl+C` in the terminal.

### Notes
- The UI runs in development mode by default. Do not use this for production.
- The app currently uses placeholder/dummy data. Real data will be wired in once the backend service APIs are finalized.
- If you see a Copilot overlay in the browser, it's a Vaadin dev tool and can be ignored. To disable it, add `vaadin.devtools.enabled=false` to `ui-service/src/main/resources/application.properties`.