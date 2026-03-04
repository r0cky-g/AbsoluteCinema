package ca.yorku.eecs4314group12.ui.data;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Supplies hard-coded placeholder data while backend services are being finalized.
 *
 * userScore on each Movie is derived from the average of the dummy reviews below —
 * the same calculation that will eventually come from review-service.
 *
 * TODO: Replace method bodies with WebClient calls:
 *   getMovieById()       → GET movie-service/movie/{id}
 *   getFeaturedMovies()  → GET movie-service/movie/featured
 *   getReviewsForMovie() → GET review-service/api/reviews/movie/{id}
 *   getReviewsForUser()  → GET review-service/api/reviews/user/{userId}
 */
@Service
public class DummyDataService {

    // -------------------------------------------------------------------------
    // Reviews (defined first so userScores can be computed inline below)
    // -------------------------------------------------------------------------

    private static final Map<Integer, List<Review>> MOVIE_REVIEWS = Map.of(

        550, List.of(
            new Review("Fight Club", 5, 10, "A film that changed everything",
                    "David Fincher's masterpiece. The twist is iconic, the performances are incredible, and the themes are disturbingly relevant.",
                    "2024-12-01", "FilmFanatic99", 142, false),
            new Review("Fight Club", 4, 8, "Visceral and thought-provoking",
                    "Deeply unsettling in the best way. Norton and Pitt have incredible chemistry.",
                    "2025-01-15", "CinematicSoul", 87, false),
            new Review("Fight Club", 5, 9, "Rewatched for the fifth time",
                    "Every rewatch reveals something new. The foreshadowing throughout is genius.",
                    "2025-02-20", "NightOwlReviews", 61, false),
            new Review("Fight Club", 3, 6, "Style over substance at times",
                    "Visually stunning but the pacing dragged in the second act.",
                    "2025-03-01", "CasualViewer", 12, false)
        ),

        27205, List.of(
            new Review("Inception", 5, 10, "Mind-bending perfection",
                    "Nolan at his absolute peak. The layered dream sequences are executed flawlessly.",
                    "2024-11-10", "DreamWeaver42", 198, false),
            new Review("Inception", 5, 9, "Still holds up 15 years later",
                    "Rewatched this and it's even better than I remembered.",
                    "2025-01-22", "RetroFilmBuff", 74, false),
            new Review("Inception", 4, 8, "Great but emotionally cold",
                    "Technically flawless but I found it hard to connect with the characters.",
                    "2025-02-14", "HeartOverMind", 43, false)
        ),

        238, List.of(
            new Review("The Godfather", 5, 10, "The greatest film ever made",
                    "There is nothing to add that hasn't been said. Perfection from every angle.",
                    "2024-10-05", "ClassicCinephile", 312, false),
            new Review("The Godfather", 5, 10, "Timeless for a reason",
                    "Watched this for my film class and I finally understand why everyone considers it the benchmark.",
                    "2025-01-08", "FilmStudent_YU", 95, false),
            new Review("The Godfather", 4, 8, "Slow burn worth the patience",
                    "At nearly 3 hours this demands your attention, but it rewards it.",
                    "2025-03-01", "PatienceIsAVirtue", 51, false)
        ),

        157336, List.of(
            new Review("Interstellar", 5, 10, "Science and emotion combined perfectly",
                    "Hans Zimmer's score paired with those visuals is an experience unlike any other.",
                    "2024-09-14", "StargazerX", 203, false),
            new Review("Interstellar", 4, 8, "Ambitious and mostly successful",
                    "The third act gets a bit convoluted but the emotional core carries it through.",
                    "2025-02-01", "SciFiCritic", 88, false)
        ),

        496243, List.of(
            new Review("Parasite", 5, 10, "Bong Joon-ho is a genius",
                    "The genre shift halfway through is one of the greatest cinematic moves ever pulled off.",
                    "2024-11-20", "KoreanCinemaFan", 176, false),
            new Review("Parasite", 4, 8, "Layers upon layers",
                    "A film that rewards multiple viewings. The symbolism is dense and deliberate.",
                    "2025-01-30", "DepthFinder", 64, false),
            new Review("Parasite", 3, 6, "Overhyped for me personally",
                    "Well made but I found the tonal shifts jarring rather than clever.",
                    "2025-02-28", "CounterOpinion", 9, false)
        ),

        545611, List.of(
            new Review("Everything Everywhere", 5, 10, "The most human sci-fi I've ever seen",
                    "Somehow manages to be simultaneously the most chaotic and most emotionally resonant film of the decade.",
                    "2025-01-05", "MultiVerseMe", 221, false),
            new Review("Everything Everywhere", 5, 9, "Ke Huy Quan deserved every award",
                    "The performances elevate an already incredible script. Waymond is one of cinema's great characters.",
                    "2025-02-10", "AwardsSeason", 97, false)
        ),

        155, List.of(
            new Review("The Dark Knight", 5, 10, "Heath Ledger is unmatched",
                    "The Joker is the greatest villain performance in cinema history. Full stop.",
                    "2024-08-22", "GothamReviewer", 287, false),
            new Review("The Dark Knight", 5, 10, "Transcends the superhero genre",
                    "This isn't a superhero film. It's a crime epic that happens to have Batman in it.",
                    "2024-12-15", "GenrePurist", 143, false),
            new Review("The Dark Knight", 4, 8, "Near perfect but slightly overlong",
                    "Could trim 15 minutes from the middle act but otherwise flawless.",
                    "2025-01-18", "EditSuggestions", 38, false)
        )
    );

    // -------------------------------------------------------------------------
    // Movies — userScore computed from the reviews above
    // -------------------------------------------------------------------------

    private static final List<Movie> MOVIES = List.of(

        new Movie(550, "Fight Club", "Fight Club", "en",
                "Mischief. Mayhem. Soap.",
                "An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more.",
                "1999-10-15", "Released", 139,
                List.of("Drama", "Thriller"),
                8.4,  // TMDB score
                avgRating(550), // user score — calculated below
                63_000_000, 100_853_753, "🪁",
                List.of("Brad Pitt", "Edward Norton", "Helena Bonham Carter"),
                "David Fincher", List.of("Fox 2000 Pictures", "Regency Enterprises")),

        new Movie(27205, "Inception", "Inception", "en",
                "Your mind is the scene of the crime.",
                "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets, is offered a chance to regain his old life as payment for a task considered to be impossible.",
                "2010-07-16", "Released", 148,
                List.of("Action", "Sci-Fi", "Thriller"),
                8.8,
                avgRating(27205),
                160_000_000, 836_836_967, "🎬",
                List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt", "Elliot Page"),
                "Christopher Nolan", List.of("Warner Bros.", "Legendary Pictures")),

        new Movie(238, "The Godfather", "The Godfather", "en",
                "An offer you can't refuse.",
                "Spanning the years 1945 to 1955, a chronicle of the fictional Italian-American Corleone crime family.",
                "1972-03-14", "Released", 175,
                List.of("Crime", "Drama"),
                9.2,
                avgRating(238),
                6_000_000, 245_066_411, "🎭",
                List.of("Marlon Brando", "Al Pacino", "James Caan"),
                "Francis Ford Coppola", List.of("Paramount Pictures", "Alfran Productions")),

        new Movie(157336, "Interstellar", "Interstellar", "en",
                "Mankind was born on Earth. It was never meant to die here.",
                "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel.",
                "2014-11-05", "Released", 169,
                List.of("Adventure", "Drama", "Sci-Fi"),
                8.6,
                avgRating(157336),
                165_000_000, 701_729_206, "🚀",
                List.of("Matthew McConaughey", "Anne Hathaway", "Jessica Chastain"),
                "Christopher Nolan", List.of("Paramount Pictures", "Legendary Pictures", "Syncopy")),

        new Movie(496243, "Parasite", "기생충", "ko",
                "Act like you own the place.",
                "All unemployed, Ki-taek's family takes a peculiar interest in the wealthy and glamorous Park family.",
                "2019-05-30", "Released", 132,
                List.of("Comedy", "Thriller", "Drama"),
                8.5,
                avgRating(496243),
                11_363_000, 258_710_053, "🏠",
                List.of("Song Kang-ho", "Lee Sun-kyun", "Cho Yeo-jeong"),
                "Bong Joon-ho", List.of("Barunson E&A", "CJ Entertainment")),

        new Movie(545611, "Everything Everywhere All at Once", "Everything Everywhere All at Once", "en",
                "The universe is so much bigger than you realize.",
                "An aging Chinese immigrant is swept up in an insane adventure, where she alone can save the world by exploring other universes.",
                "2022-03-25", "Released", 139,
                List.of("Action", "Adventure", "Comedy"),
                8.1,
                avgRating(545611),
                14_300_000, 73_948_299, "🥢",
                List.of("Michelle Yeoh", "Stephanie Hsu", "Ke Huy Quan"),
                "Daniel Kwan, Daniel Scheinert", List.of("A24", "AGBO")),

        new Movie(155, "The Dark Knight", "The Dark Knight", "en",
                "Why so serious?",
                "Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets.",
                "2008-07-18", "Released", 152,
                List.of("Action", "Crime", "Drama"),
                9.0,
                avgRating(155),
                185_000_000, 1_004_934_033, "🦇",
                List.of("Christian Bale", "Heath Ledger", "Aaron Eckhart"),
                "Christopher Nolan", List.of("Warner Bros.", "Legendary Pictures", "Syncopy"))
    );

    // -------------------------------------------------------------------------
    // Helper — computes average review rating for a movie from dummy data
    // TODO: replace with review-service GET /api/reviews/movie/{id}/stats
    // -------------------------------------------------------------------------

    private static double avgRating(int movieId) {
        List<Review> reviews = MOVIE_REVIEWS.getOrDefault(movieId, List.of());
        if (reviews.isEmpty()) return 0.0;
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        // Round to one decimal place
        return Math.round(avg * 10.0) / 10.0;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

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
            new Review("Inception",       5, 10, "Mind-bending perfection",   "A masterpiece of layered storytelling.",           "2024-11-03", username, 0, false),
            new Review("Parasite",        4, 8,  "Brilliant social commentary","Brilliant social commentary, slightly slow start.", "2024-10-15", username, 0, false),
            new Review("The Dark Knight", 5, 10, "Ledger is unmatched",        "Ledger's Joker is unmatched. Perfect film.",        "2024-09-22", username, 0, false)
        );
    }
}