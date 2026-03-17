package ca.yorku.eecs4314group12.ui.data;

import java.util.List;

/**
 * Placeholder DTO for a movie, shaped to match TmdbMovieDTO from movie-service.
 *
 * Two scores are tracked separately:
 *   tmdbScore  — vote_average straight from TMDB API (0–10)
 *   userScore  — average of our users' reviews from review-service (0–10)
 *               Calculated by DummyDataService from dummy reviews.
 *               TODO: replace with WebClient GET review-service/api/reviews/movie/{id}/stats
 *
 * TODO: Replace full class with a WebClient call to movie-service GET /movie/{id}
 */
public class Movie {

    private final int id;
    private final String title;
    private final String originalTitle;
    private final String originalLanguage;
    private final String tagline;
    private final String overview;
    private final String releaseDate;
    private final String status;
    private final int runtime;
    private final List<String> genres;
    private final double tmdbScore;   // TMDB vote_average 0–10
    private final double userScore;   // Our review-service average 0–10 (0 = no reviews yet)
    private final int budget;
    private final int revenue;
    private final String posterEmoji;
    private final List<String> cast;
    private final String director;
    private final List<String> productionCompanies;

    public Movie(int id, String title, String originalTitle, String originalLanguage,
                 String tagline, String overview, String releaseDate, String status,
                 int runtime, List<String> genres, double tmdbScore, double userScore,
                 int budget, int revenue, String posterEmoji,
                 List<String> cast, String director, List<String> productionCompanies) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.tagline = tagline;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.status = status;
        this.runtime = runtime;
        this.genres = genres;
        this.tmdbScore = tmdbScore;
        this.userScore = userScore;
        this.budget = budget;
        this.revenue = revenue;
        this.posterEmoji = posterEmoji;
        this.cast = cast;
        this.director = director;
        this.productionCompanies = productionCompanies;
    }

    /** Legacy 5-arg constructor — keeps existing code that uses it from breaking. */
    public Movie(String title, String year, String genre, double rating, String posterEmoji) {
        this(0, title, title, "en", "", "", year + "-01-01", "Released",
                0, List.of(genre), rating, 0.0, 0, 0, posterEmoji,
                List.of(), "Unknown", List.of());
    }

    public int getId()                           { return id; }
    public String getTitle()                     { return title; }
    public String getOriginalTitle()             { return originalTitle; }
    public String getOriginalLanguage()          { return originalLanguage; }
    public String getTagline()                   { return tagline; }
    public String getOverview()                  { return overview; }
    public String getReleaseDate()               { return releaseDate; }
    public String getStatus()                    { return status; }
    public int getRuntime()                      { return runtime; }
    public List<String> getGenres()              { return genres; }
    public double getTmdbScore()                 { return tmdbScore; }
    public double getUserScore()                 { return userScore; }
    public int getBudget()                       { return budget; }
    public int getRevenue()                      { return revenue; }
    public String getPosterEmoji()               { return posterEmoji; }
    public List<String> getCast()                { return cast; }
    public String getDirector()                  { return director; }
    public List<String> getProductionCompanies() { return productionCompanies; }

    /** Best available score for card display — user score if reviews exist, else TMDB. */
    public double getDisplayRating() {
        return userScore > 0 ? userScore : tmdbScore;
    }

    public String getRuntimeFormatted() {
        if (runtime <= 0) return "N/A";
        return runtime / 60 + "h " + runtime % 60 + "m";
    }

    /** First genre / joined genres for compact display. */
    public String getGenre() {
        return genres.isEmpty() ? "" : String.join(" / ", genres);
    }

    public String getYear() {
        return releaseDate != null && releaseDate.length() >= 4
                ? releaseDate.substring(0, 4) : "";
    }
}