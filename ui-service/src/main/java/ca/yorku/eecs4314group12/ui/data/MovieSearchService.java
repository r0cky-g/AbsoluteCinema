package ca.yorku.eecs4314group12.ui.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * Applies a {@link SearchFilter} to the movie catalogue and returns the
 * matching movies in the requested sort order.
 *
 * All filter logic lives here so HomeView stays clean.
 *
 * TODO: Once movie-service and review-service are wired up, replace the
 *       DummyDataService.getFeaturedMovies() call here with a WebClient
 *       query that passes filter params server-side for efficiency.
 */
@Service
public class MovieSearchService {

    private final DummyDataService dataService;

    public MovieSearchService(DummyDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Returns movies that match every active filter, sorted as requested.
     * An empty filter returns the full catalogue.
     */
    public List<Movie> search(SearchFilter filter) {
        return dataService.getFeaturedMovies().stream()
                .filter(m -> matchesQuery(m, filter.getQuery()))
                .filter(m -> matchesGenre(m, filter.getGenre()))
                .filter(m -> matchesYearFrom(m, filter.getYearFrom()))
                .filter(m -> matchesYearTo(m, filter.getYearTo()))
                .filter(m -> matchesMinUserScore(m, filter.getMinUserScore()))
                .filter(m -> matchesMinTmdbScore(m, filter.getMinTmdbScore()))
                .filter(m -> matchesLanguage(m, filter.getLanguage()))
                .sorted(comparatorFor(filter.getSortBy()))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Individual predicates
    // -------------------------------------------------------------------------

    /**
     * Free-text search across: title, original title, director, cast members,
     * overview, tagline, production companies, and release year.
     */
    private boolean matchesQuery(Movie m, String q) {
        if (q == null || q.isBlank()) return true;
        String lq = q.toLowerCase();
        if (m.getTitle().toLowerCase().contains(lq))            return true;
        if (m.getOriginalTitle().toLowerCase().contains(lq))    return true;
        if (m.getDirector().toLowerCase().contains(lq))         return true;
        if (m.getTagline().toLowerCase().contains(lq))          return true;
        if (m.getOverview().toLowerCase().contains(lq))         return true;
        if (m.getYear().contains(lq))                           return true;
        for (String actor : m.getCast())
            if (actor.toLowerCase().contains(lq)) return true;
        for (String company : m.getProductionCompanies())
            if (company.toLowerCase().contains(lq)) return true;
        for (String genre : m.getGenres())
            if (genre.toLowerCase().contains(lq)) return true;
        return false;
    }

    private boolean matchesGenre(Movie m, String genre) {
        if (genre == null || genre.isBlank()) return true;
        return m.getGenres().stream()
                .anyMatch(g -> g.equalsIgnoreCase(genre));
    }

    private boolean matchesYearFrom(Movie m, String yearFrom) {
        if (yearFrom == null || yearFrom.isBlank()) return true;
        try {
            return Integer.parseInt(m.getYear()) >= Integer.parseInt(yearFrom);
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean matchesYearTo(Movie m, String yearTo) {
        if (yearTo == null || yearTo.isBlank()) return true;
        try {
            return Integer.parseInt(m.getYear()) <= Integer.parseInt(yearTo);
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /** Filters by our users' average review score. */
    private boolean matchesMinUserScore(Movie m, double min) {
        if (min <= 0) return true;
        return m.getUserScore() >= min;
    }

    /** Filters by TMDB's vote_average score. */
    private boolean matchesMinTmdbScore(Movie m, double min) {
        if (min <= 0) return true;
        return m.getTmdbScore() >= min;
    }

    private boolean matchesLanguage(Movie m, String language) {
        if (language == null || language.isBlank()) return true;
        return m.getOriginalLanguage().equalsIgnoreCase(language);
    }

    // -------------------------------------------------------------------------
    // Sort
    // -------------------------------------------------------------------------

    private Comparator<Movie> comparatorFor(String sortBy) {
        return switch (sortBy) {
            case "year"       -> Comparator.comparing(Movie::getYear).reversed();
            case "userScore"  -> Comparator.comparingDouble(Movie::getUserScore).reversed();
            case "tmdbScore"  -> Comparator.comparingDouble(Movie::getTmdbScore).reversed();
            case "runtime"    -> Comparator.comparingInt(Movie::getRuntime).reversed();
            default           -> Comparator.comparing(Movie::getTitle);
        };
    }
}