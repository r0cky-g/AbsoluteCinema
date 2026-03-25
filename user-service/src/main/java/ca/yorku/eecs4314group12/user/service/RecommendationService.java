package ca.yorku.eecs4314group12.user.service;

import ca.yorku.eecs4314group12.user.client.MovieClient;
import ca.yorku.eecs4314group12.user.dto.MovieDTO;
import ca.yorku.eecs4314group12.user.dto.MoviesTrendingDTO;
import ca.yorku.eecs4314group12.user.model.Watchlist;
import ca.yorku.eecs4314group12.user.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
    private static final int MAX_RECOMMENDATIONS = 5;

    private final MovieClient movieClient;
    private final WatchlistRepository watchlistRepository;

    public RecommendationService(MovieClient movieClient, WatchlistRepository watchlistRepository) {
        this.movieClient = movieClient;
        this.watchlistRepository = watchlistRepository;
    }

    public List<MovieDTO> getRecommendedMovies(Long userId) {
        logger.info("Getting recommendations for user {}", userId);

        // Step 1: Get user's watchlist (movie IDs)
        List<Watchlist> watchlist = watchlistRepository.findByUserId(userId);

        // Step 2: Get trending movies as recommendation pool (and fallback response)
        Optional<MoviesTrendingDTO> trendingOpt = movieClient.getTrendingMovies();
        if (trendingOpt.isEmpty() || trendingOpt.get().getResults() == null) {
            logger.warn("Could not fetch trending movies for recommendations");
            return Collections.emptyList();
        }

        List<MovieDTO> trendingMovies = trendingOpt.get().getResults();
        logger.info("Fetched {} trending movies for recommendation pool", trendingMovies.size());

        if (watchlist.isEmpty()) {
            logger.info("User {} has no movies in watchlist, returning trending movies", userId);
            return trendingMovies.stream().limit(MAX_RECOMMENDATIONS).collect(Collectors.toList());
        }

        // Step 3: Fetch movie details for each watchlist movie
        List<MovieDTO> watchlistMovies = new ArrayList<>();
        Set<Integer> watchlistMovieIds = new HashSet<>();

        for (Watchlist item : watchlist) {
            watchlistMovieIds.add(item.getMovieId());
            movieClient.getMovieById(item.getMovieId())
                    .ifPresent(watchlistMovies::add);
        }

        if (watchlistMovies.isEmpty()) {
            logger.warn("Could not fetch metadata for watchlist movies for user {} (watchlist had {} items). Returning trending movies.",
                    userId, watchlist.size());
            return trendingMovies.stream()
                    .filter(movie -> !watchlistMovieIds.contains(movie.getId()))
                    .limit(MAX_RECOMMENDATIONS)
                    .collect(Collectors.toList());
        }

        logger.info("Successfully fetched {} watchlist movies for user {}", watchlistMovies.size(), userId);

        // Step 4: Count how often each genre appears on the watchlist (one per movie per genre)
        Map<String, Integer> watchlistGenreCounts = extractGenreCounts(watchlistMovies);

        if (watchlistGenreCounts.isEmpty()) {
            logger.info("No genre metadata found for user {} watchlist movies, returning trending movies", userId);
            return trendingMovies.stream()
                    .filter(movie -> !watchlistMovieIds.contains(movie.getId()))
                    .limit(MAX_RECOMMENDATIONS)
                    .collect(Collectors.toList());
        }

        logger.info("User {} watchlist genre counts: {} distinct genres", userId, watchlistGenreCounts.size());

        // Step 5: Prefer trending titles that share watchlist genres (weighted by genre frequency on watchlist)
        List<MovieDTO> notOnWatchlist = trendingMovies.stream()
                .filter(movie -> !watchlistMovieIds.contains(movie.getId()))
                .collect(Collectors.toList());

        List<GenreScoredMovie> scored = notOnWatchlist.stream()
                .map(m -> new GenreScoredMovie(m, genreMatchScore(m, watchlistGenreCounts)))
                .collect(Collectors.toList());

        List<MovieDTO> withGenreMatch = scored.stream()
                .filter(s -> s.score > 0)
                .sorted(Comparator.comparingDouble((GenreScoredMovie s) -> s.score).reversed())
                .map(s -> s.movie)
                .collect(Collectors.toList());

        LinkedHashSet<Integer> pickedIds = new LinkedHashSet<>();
        List<MovieDTO> recommendations = new ArrayList<>();

        for (MovieDTO m : withGenreMatch) {
            if (recommendations.size() >= MAX_RECOMMENDATIONS) {
                break;
            }
            if (pickedIds.add(m.getId())) {
                recommendations.add(m);
            }
        }

        if (recommendations.size() < MAX_RECOMMENDATIONS) {
            for (MovieDTO m : notOnWatchlist) {
                if (recommendations.size() >= MAX_RECOMMENDATIONS) {
                    break;
                }
                if (pickedIds.add(m.getId())) {
                    recommendations.add(m);
                }
            }
        }

        logger.info("Generated {} recommendations for user {} (genre-weighted matches first, then trending)",
                recommendations.size(), userId);

        if (recommendations.isEmpty()) {
            logger.warn("No recommendations generated for user {} - check if trending movies are available", userId);
        }

        return recommendations;
    }

    /**
     * For each genre, number of watchlist movies that include it (at most once per movie per genre).
     */
    private Map<String, Integer> extractGenreCounts(List<MovieDTO> movies) {
        Map<String, Integer> counts = new HashMap<>();
        for (MovieDTO m : movies) {
            if (m.getGenres() == null) {
                continue;
            }
            m.getGenres().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .forEach(g -> counts.merge(g, 1, Integer::sum));
        }
        return counts;
    }

    /**
     * Score = sum over each matching genre of (base weight × watchlist frequency of that genre).
     */
    private static final double GENRE_MATCH_WEIGHT = 3.0;

    private double genreMatchScore(MovieDTO movie, Map<String, Integer> watchlistGenreCounts) {
        if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
            return 0.0;
        }
        double score = 0.0;
        Set<String> seen = new HashSet<>();
        for (String g : movie.getGenres()) {
            if (g == null || !seen.add(g)) {
                continue;
            }
            Integer c = watchlistGenreCounts.get(g);
            if (c != null && c > 0) {
                score += GENRE_MATCH_WEIGHT * c;
            }
        }
        return score;
    }

    private record GenreScoredMovie(MovieDTO movie, double score) {}
}
