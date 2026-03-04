package ca.yorku.eecs4314group12.ui.data;

import java.util.List;

/**
 * Placeholder DTO for a movie, shaped to match TmdbMovieDTO from movie-service.
 * TODO: Replace with a WebClient call to movie-service GET /movie/{id}
 */
public class Movie {

    private final int id;
    private final String title;
    private final String originalTitle;
    private final String tagline;
    private final String overview;
    private final String releaseDate;
    private final String status;
    private final int runtime;          // minutes
    private final List<String> genres;
    private final double rating;        // 0–10 scale matching TMDB
    private final int budget;
    private final int revenue;
    private final String posterEmoji;   // placeholder until real TMDB poster images are wired in
    private final List<String> cast;    // top-billed actor names
    private final String director;
    private final List<String> productionCompanies;

    public Movie(int id, String title, String originalTitle, String tagline,
                 String overview, String releaseDate, String status,
                 int runtime, List<String> genres, double rating,
                 int budget, int revenue, String posterEmoji,
                 List<String> cast, String director,
                 List<String> productionCompanies) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.tagline = tagline;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.status = status;
        this.runtime = runtime;
        this.genres = genres;
        this.rating = rating;
        this.budget = budget;
        this.revenue = revenue;
        this.posterEmoji = posterEmoji;
        this.cast = cast;
        this.director = director;
        this.productionCompanies = productionCompanies;
    }

    // Legacy constructor used by HomeView cards (keeps backward compat)
    public Movie(String title, String year, String genre, double rating, String posterEmoji) {
        this(0, title, title, "", "", year + "-01-01", "Released",
                0, List.of(genre), rating, 0, 0, posterEmoji,
                List.of(), "Unknown", List.of());
    }

    public int getId()                          { return id; }
    public String getTitle()                    { return title; }
    public String getOriginalTitle()            { return originalTitle; }
    public String getTagline()                  { return tagline; }
    public String getOverview()                 { return overview; }
    public String getReleaseDate()              { return releaseDate; }
    public String getStatus()                   { return status; }
    public int getRuntime()                     { return runtime; }
    public List<String> getGenres()             { return genres; }
    public double getRating()                   { return rating; }
    public int getBudget()                      { return budget; }
    public int getRevenue()                     { return revenue; }
    public String getPosterEmoji()              { return posterEmoji; }
    public List<String> getCast()               { return cast; }
    public String getDirector()                 { return director; }
    public List<String> getProductionCompanies(){ return productionCompanies; }

    // Convenience — returns "2h 28m" style string
    public String getRuntimeFormatted() {
        if (runtime <= 0) return "N/A";
        return runtime / 60 + "h " + runtime % 60 + "m";
    }

    // Convenience — first genre only for compact cards
    public String getGenre() {
        return genres.isEmpty() ? "" : String.join(" / ", genres);
    }

    // Convenience — release year only
    public String getYear() {
        return releaseDate != null && releaseDate.length() >= 4
                ? releaseDate.substring(0, 4) : "";
    }
}