package ca.yorku.eecs4314group12.ui.data;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DummyDataService {

    private static final Map<Integer, List<Review>> MOVIE_REVIEWS = Map.of(

        1, List.of(
            new Review("[DUMMY] Dummy Movie One", 5, 10, "[DUMMY] Great dummy review",
                    "[DUMMY] This is placeholder review content. Replace with real review-service data.",
                    "2099-01-01", "[DUMMY] DummyReviewer1", 0, false),
            new Review("[DUMMY] Dummy Movie One", 3, 6, "[DUMMY] Another dummy review",
                    "[DUMMY] This is placeholder review content. Replace with real review-service data.",
                    "2099-01-02", "[DUMMY] DummyReviewer2", 0, false)
        ),

        2, List.of(
            new Review("[DUMMY] Dummy Movie Two", 4, 8, "[DUMMY] Dummy review for movie two",
                    "[DUMMY] This is placeholder review content. Replace with real review-service data.",
                    "2099-01-03", "[DUMMY] DummyReviewer3", 0, false)
        )
    );

    private static final List<Movie> MOVIES = List.of(

        new Movie(1, "[DUMMY] Dummy Movie One", "[DUMMY] Dummy Movie One", "xx",
                "[DUMMY] This is a dummy tagline",
                "[DUMMY] This is dummy movie data from DummyDataService. Will be replaced by movie-service.",
                "2099-01-01", "DUMMY", 999,
                List.of("[DUMMY] Genre A", "[DUMMY] Genre B"),
                0.0, avgRating(1), 0, 0, "⚠️",
                List.of("[DUMMY] Actor One", "[DUMMY] Actor Two"),
                "[DUMMY] Director", List.of("[DUMMY] Production Co")),

        new Movie(2, "[DUMMY] Dummy Movie Two", "[DUMMY] Dummy Movie Two", "xx",
                "[DUMMY] This is a dummy tagline",
                "[DUMMY] This is dummy movie data from DummyDataService. Will be replaced by movie-service.",
                "2099-01-02", "DUMMY", 999,
                List.of("[DUMMY] Genre C"),
                0.0, avgRating(2), 0, 0, "⚠️",
                List.of("[DUMMY] Actor Three"),
                "[DUMMY] Director", List.of("[DUMMY] Production Co")),

        new Movie(3, "[DUMMY] Dummy Movie Three", "[DUMMY] Dummy Movie Three", "xx",
                "[DUMMY] This is a dummy tagline",
                "[DUMMY] This is dummy movie data from DummyDataService. Will be replaced by movie-service.",
                "2099-01-03", "DUMMY", 999,
                List.of("[DUMMY] Genre D"),
                0.0, avgRating(3), 0, 0, "⚠️",
                List.of("[DUMMY] Actor Four"),
                "[DUMMY] Director", List.of("[DUMMY] Production Co"))
    );

    private static double avgRating(int movieId) {
        List<Review> reviews = MOVIE_REVIEWS.getOrDefault(movieId, List.of());
        if (reviews.isEmpty()) return 0.0;
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        return Math.round(avg * 10.0) / 10.0;
    }

    public List<Movie> getFeaturedMovies() {
        return MOVIES;
    }

    public Optional<Movie> getMovieById(int id) {
        return MOVIES.stream().filter(m -> m.getId() == id).findFirst();
    }

    public List<Review> getReviewsForMovie(int movieId) {
        return MOVIE_REVIEWS.getOrDefault(movieId, List.of());
    }

    public List<Review> getReviewsForUser(String username) {
        return List.of(
            new Review("[DUMMY] Dummy Movie One",   3, 6,  "[DUMMY] Dummy review title",
                    "[DUMMY] Placeholder review data. Replace with real review-service data.",
                    "2099-01-01", username, 0, false),
            new Review("[DUMMY] Dummy Movie Two",   4, 8,  "[DUMMY] Dummy review title",
                    "[DUMMY] Placeholder review data. Replace with real review-service data.",
                    "2099-01-02", username, 0, false),
            new Review("[DUMMY] Dummy Movie Three", 5, 10, "[DUMMY] Dummy review title",
                    "[DUMMY] Placeholder review data. Replace with real review-service data.",
                    "2099-01-03", username, 0, false)
        );
    }
}