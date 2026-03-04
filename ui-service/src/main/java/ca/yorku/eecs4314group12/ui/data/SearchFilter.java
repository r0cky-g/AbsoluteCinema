package ca.yorku.eecs4314group12.ui.data;

/**
 * Immutable value object capturing the current state of the movie search filters.
 *
 * Used by MovieSearchService to filter the movie list, and by HomeView to
 * bind UI controls to filter values.
 */
public class SearchFilter {

    private final String query;           // free-text: title, director, cast, overview
    private final String genre;           // exact genre match, or "" for any
    private final String yearFrom;        // inclusive, e.g. "1990", or "" for no lower bound
    private final String yearTo;          // inclusive, or "" for no upper bound
    private final double minUserScore;    // minimum user review average (0–10); 0 = no filter
    private final double minTmdbScore;    // minimum TMDB score (0–10); 0 = no filter
    private final String language;        // ISO 639-1 code, e.g. "en", or "" for any
    private final String sortBy;          // "title", "year", "userScore", "tmdbScore", "runtime"

    public SearchFilter(String query, String genre, String yearFrom, String yearTo,
                        double minUserScore, double minTmdbScore,
                        String language, String sortBy) {
        this.query        = query        == null ? "" : query.trim().toLowerCase();
        this.genre        = genre        == null ? "" : genre.trim();
        this.yearFrom     = yearFrom     == null ? "" : yearFrom.trim();
        this.yearTo       = yearTo       == null ? "" : yearTo.trim();
        this.minUserScore = minUserScore;
        this.minTmdbScore = minTmdbScore;
        this.language     = language     == null ? "" : language.trim();
        this.sortBy       = sortBy       == null ? "title" : sortBy.trim();
    }

    /** Returns a blank filter — shows all movies, sorted by title. */
    public static SearchFilter empty() {
        return new SearchFilter("", "", "", "", 0, 0, "", "title");
    }

    public boolean isEmpty() {
        return query.isBlank() && genre.isBlank() && yearFrom.isBlank()
                && yearTo.isBlank() && minUserScore <= 0 && minTmdbScore <= 0
                && language.isBlank();
    }

    public String getQuery()          { return query; }
    public String getGenre()          { return genre; }
    public String getYearFrom()       { return yearFrom; }
    public String getYearTo()         { return yearTo; }
    public double getMinUserScore()   { return minUserScore; }
    public double getMinTmdbScore()   { return minTmdbScore; }
    public String getLanguage()       { return language; }
    public String getSortBy()         { return sortBy; }
}