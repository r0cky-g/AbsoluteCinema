package ca.yorku.eecs4314group12.ui.data;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Supplies hard-coded placeholder data for the UI while the backend APIs are
 * being finalized.
 *
 * TODO: Replace each method body with a WebClient call to the appropriate
 *       microservice once the REST contracts are agreed upon:
 *         - getMovieById()       → GET movie-service/movie/{id}
 *         - getFeaturedMovies()  → GET movie-service/movie/featured (or curated list)
 *         - getReviewsForMovie() → GET review-service/api/reviews/movie/{id}
 *         - getReviewsForUser()  → GET review-service/api/reviews/user/{userId}
 */
@Service
public class DummyDataService {

    // -------------------------------------------------------------------------
    // Movie data
    // -------------------------------------------------------------------------

    private static final List<Movie> MOVIES = List.of(

        new Movie(550, "Fight Club", "Fight Club",
                "Mischief. Mayhem. Soap.",
                "An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more.",
                "1999-10-15", "Released", 139,
                List.of("Drama", "Thriller"), 8.8,
                63_000_000, 100_853_753, "🪁",
                List.of("Brad Pitt", "Edward Norton", "Helena Bonham Carter"),
                "David Fincher", List.of("Fox 2000 Pictures", "Regency Enterprises")),

        new Movie(27205, "Inception", "Inception",
                "Your mind is the scene of the crime.",
                "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets, is offered a chance to regain his old life as payment for a task considered to be impossible.",
                "2010-07-16", "Released", 148,
                List.of("Action", "Sci-Fi", "Thriller"), 8.8,
                160_000_000, 836_836_967, "🎬",
                List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt", "Elliot Page"),
                "Christopher Nolan", List.of("Warner Bros.", "Legendary Pictures")),

        new Movie(238, "The Godfather", "The Godfather",
                "An offer you can't refuse.",
                "Spanning the years 1945 to 1955, a chronicle of the fictional Italian-American Corleone crime family. When organized crime patriarch, Vito Corleone barely survives an attempt on his life, his youngest son, Michael steps in to take care of the would-be killers, launching a campaign of bloody revenge.",
                "1972-03-14", "Released", 175,
                List.of("Crime", "Drama"), 9.2,
                6_000_000, 245_066_411, "🎭",
                List.of("Marlon Brando", "Al Pacino", "James Caan"),
                "Francis Ford Coppola", List.of("Paramount Pictures", "Alfran Productions")),

        new Movie(157336, "Interstellar", "Interstellar",
                "Mankind was born on Earth. It was never meant to die here.",
                "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
                "2014-11-05", "Released", 169,
                List.of("Adventure", "Drama", "Sci-Fi"), 8.6,
                165_000_000, 701_729_206, "🚀",
                List.of("Matthew McConaughey", "Anne Hathaway", "Jessica Chastain"),
                "Christopher Nolan", List.of("Paramount Pictures", "Legendary Pictures", "Syncopy")),

        new Movie(496243, "Parasite", "기생충",
                "Act like you own the place.",
                "All unemployed, Ki-taek's family takes a peculiar interest in the wealthy and glamorous Park family. After his son Ki-woo is recommended for a tutoring job, the two families are drawn into an unexpected relationship.",
                "2019-05-30", "Released", 132,
                List.of("Comedy", "Thriller", "Drama"), 8.5,
                11_363_000, 258_710_053, "🏠",
                List.of("Song Kang-ho", "Lee Sun-kyun", "Cho Yeo-jeong"),
                "Bong Joon-ho", List.of("Barunson E&A", "CJ Entertainment")),

        new Movie(545611, "Everything Everywhere All at Once", "Everything Everywhere All at Once",
                "The universe is so much bigger than you realize.",
                "An aging Chinese immigrant is swept up in an insane adventure, where she alone can save the world by exploring other universes connecting with the lives she could have led.",
                "2022-03-25", "Released", 139,
                List.of("Action", "Adventure", "Comedy"), 8.1,
                14_300_000, 73_948_299, "🥢",
                List.of("Michelle Yeoh", "Stephanie Hsu", "Ke Huy Quan"),
                "Daniel Kwan, Daniel Scheinert", List.of("A24", "AGBO")),

        new Movie(155, "The Dark Knight", "The Dark Knight",
                "Why so serious?",
                "Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets. But soon a dark menace arises: a nihilistic criminal mastermind known as the Joker.",
                "2008-07-18", "Released", 152,
                List.of("Action", "Crime", "Drama"), 9.0,
                185_000_000, 1_004_934_033, "🦇",
                List.of("Christian Bale", "Heath Ledger", "Aaron Eckhart"),
                "Christopher Nolan", List.of("Warner Bros.", "Legendary Pictures", "Syncopy"))
    );

    private static final Map<Integer, List<Review>> MOVIE_REVIEWS = Map.of(

        550, List.of(
            new Review("Fight Club", 5, 10, "A film that changed everything",
                    "David Fincher's masterpiece. The twist is iconic, the performances are incredible, and the themes are disturbingly relevant. Brad Pitt has never been better.",
                    "2024-12-01", "FilmFanatic99", 142, false),
            new Review("Fight Club", 4, 8, "Visceral and thought-provoking",
                    "Deeply unsettling in the best way. The cinematography alone is worth the watch. Norton and Pitt have incredible chemistry.",
                    "2025-01-15", "CinematicSoul", 87, false),
            new Review("Fight Club", 5, 9, "Rewatched for the fifth time",
                    "Every rewatch reveals something new. The foreshadowing throughout is genius. Fincher hid clues in plain sight from the very first frame.",
                    "2025-02-20", "NightOwlReviews", 61, false),
            new Review("Fight Club", 3, 6, "Style over substance at times",
                    "Visually stunning but I found the pacing dragged in the second act. Still worth watching for the performances.",
                    "2025-03-01", "CasualViewer", 12, false)
        ),

        27205, List.of(
            new Review("Inception", 5, 10, "Mind-bending perfection",
                    "Nolan at his absolute peak. The layered dream sequences are executed flawlessly and Hans Zimmer's score is unforgettable. A genuine modern classic.",
                    "2024-11-10", "DreamWeaver42", 198, false),
            new Review("Inception", 5, 9, "Still holds up 15 years later",
                    "Rewatched this and it's even better than I remembered. The practical effects mixed with CGI still look incredible.",
                    "2025-01-22", "RetroFilmBuff", 74, false),
            new Review("Inception", 4, 8, "Great but emotionally cold",
                    "Technically flawless but I found it hard to connect with the characters. The DiCaprio and Page relationship needed more time.",
                    "2025-02-14", "HeartOverMind", 43, false)
        ),

        238, List.of(
            new Review("The Godfather", 5, 10, "The greatest film ever made",
                    "There is nothing to add that hasn't been said. Brando, Pacino, Coppola — perfection from every angle. Cinema at its highest form.",
                    "2024-10-05", "ClassicCinephile", 312, false),
            new Review("The Godfather", 5, 10, "Timeless for a reason",
                    "Watched this for my film class and I finally understand why everyone considers it the benchmark. The quiet moments are as powerful as the violent ones.",
                    "2025-01-08", "FilmStudent_YU", 95, false),
            new Review("The Godfather", 4, 8, "Slow burn worth the patience",
                    "At nearly 3 hours this demands your attention, but it rewards it. The dinner table scenes alone are masterclasses in tension.",
                    "2025-03-01", "PatienceIsAVirtue", 51, false)
        )
    );

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public List<Movie> getFeaturedMovies() {
        return MOVIES;
    }

    public Optional<Movie> getMovieById(int id) {
        return MOVIES.stream().filter(m -> m.getId() == id).findFirst();
    }

    /**
     * Returns reviews for a movie.
     * Top recommended = highest helpfulCount.
     * Most recent = sorted by datePosted descending (last 3).
     */
    public List<Review> getReviewsForMovie(int movieId) {
        return MOVIE_REVIEWS.getOrDefault(movieId, List.of());
    }

    public List<Review> getReviewsForUser(String username) {
        return List.of(
            new Review("Inception",       5, 10, "Mind-bending perfection",   "A masterpiece of layered storytelling.",           "2024-11-03", username, 0, false),
            new Review("Parasite",        4, 8,  "Brilliant social commentary","Brilliant social commentary, slightly slow start.", "2024-10-15", username, 0, false),
            new Review("The Dark Knight", 5, 10, "Ledger is unmatched",        "Ledger's Joker is unmatched. Perfect film.",        "2024-09-22", username, 0, false)
        );
    }
}