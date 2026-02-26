package ca.yorku.eecs4314group12.ui.data;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Supplies hard-coded placeholder data for the UI while the backend APIs are
 * being finalized.
 *
 * TODO: Replace each method body with a RestTemplate / WebClient call to the
 *       appropriate microservice once the REST contracts are agreed upon:
 *         - getFeaturedMovies()  → GET movie-service/api/movies/featured
 *         - getReviewsForUser()  → GET review-service/api/reviews?user={username}
 */
@Service
public class DummyDataService {

    public List<Movie> getFeaturedMovies() {
        return List.of(
            new Movie("Inception",          "2010", "Sci-Fi / Thriller", 4.8, "🎬"),
            new Movie("The Godfather",       "1972", "Crime / Drama",     4.9, "🎭"),
            new Movie("Interstellar",        "2014", "Sci-Fi / Drama",    4.7, "🚀"),
            new Movie("Parasite",            "2019", "Thriller / Drama",  4.6, "🏠"),
            new Movie("Everything Everywhere All At Once", "2022", "Action / Comedy", 4.5, "🥢"),
            new Movie("The Dark Knight",     "2008", "Action / Crime",    4.9, "🦇")
        );
    }

    public List<Review> getReviewsForUser(String username) {
        // Dummy reviews attributed to the logged-in user
        return List.of(
            new Review("Inception",      5, "A masterpiece of layered storytelling.", "2024-11-03"),
            new Review("Parasite",       4, "Brilliant social commentary, slightly slow start.", "2024-10-15"),
            new Review("The Dark Knight",5, "Ledger's Joker is unmatched. Perfect film.", "2024-09-22")
        );
    }
}