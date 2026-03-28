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

        List<Watchlist> watchlist = watchlistRepository.findByUserId(userId);
        Set<Integer> watchlistMovieIds = watchlist.stream()
                .map(Watchlist::getMovieId)
                .collect(Collectors.toSet());

        if (watchlist.isEmpty()) {
            logger.info("User {} has no watchlist titles; returning top {} trending", userId, MAX_RECOMMENDATIONS);
            return topTrendingExcluding(Collections.emptySet());
        }

        List<MovieDTO> watchlistMovies = new ArrayList<>();
        for (Watchlist item : watchlist) {
            movieClient.getMovieById(item.getMovieId())
                    .ifPresent(watchlistMovies::add);
        }

        if (watchlistMovies.isEmpty()) {
            logger.warn("Could not fetch metadata for any watchlist movie for user {} ({} ids); using trending fallback",
                    userId, watchlist.size());
            return topTrendingExcluding(watchlistMovieIds);
        }

        logger.info("Successfully fetched {} watchlist movies for user {}", watchlistMovies.size(), userId);

        Set<String> preferredGenres = extractGenres(watchlistMovies);
        Set<String> preferredActors = extractActors(watchlistMovies);
        Set<String> preferredDirectors = extractDirectors(watchlistMovies);

        logger.info("User {} preferences from watchlist - Genres: {}, Actors: {}, Directors: {}",
                userId, preferredGenres.size(), preferredActors.size(), preferredDirectors.size());

        Optional<MoviesTrendingDTO> trendingOpt = movieClient.getTrendingMovies();
        if (trendingOpt.isEmpty() || trendingOpt.get().getResults() == null) {
            logger.warn("Trending unavailable for scoring user {}; using trending fallback", userId);
            return topTrendingExcluding(watchlistMovieIds);
        }

        List<MovieDTO> trendingMovies = trendingOpt.get().getResults();
        logger.info("Fetched {} trending movies for recommendation pool", trendingMovies.size());

        // Step 5: Score and rank movies based on similarity to watchlist
        List<MovieScore> scoredMovies = trendingMovies.stream()
                .filter(movie -> !watchlistMovieIds.contains(movie.getId())) // Exclude already in watchlist
                .map(movie -> scoreMovie(movie, preferredGenres, preferredActors, preferredDirectors))
                .sorted((a, b) -> Double.compare(b.score, a.score)) // Sort by score descending
                .limit(MAX_RECOMMENDATIONS)
                .collect(Collectors.toList());

        List<MovieDTO> recommendations = scoredMovies.stream()
                .map(score -> score.movie)
                .collect(Collectors.toList());

        // If we have fewer than MAX_RECOMMENDATIONS, fill with top trending movies (even if score is 0)
        if (recommendations.size() < MAX_RECOMMENDATIONS) {
            int remaining = MAX_RECOMMENDATIONS - recommendations.size();
            Set<Integer> alreadyRecommended = recommendations.stream()
                    .map(MovieDTO::getId)
                    .collect(Collectors.toSet());
            
            List<MovieDTO> additionalMovies = trendingMovies.stream()
                    .filter(movie -> !watchlistMovieIds.contains(movie.getId()))
                    .filter(movie -> !alreadyRecommended.contains(movie.getId()))
                    .limit(remaining)
                    .collect(Collectors.toList());
            
            recommendations.addAll(additionalMovies);
        }

        logger.info("Generated {} recommendations for user {} based on watchlist (preferences: genres={}, actors={}, directors={})", 
                recommendations.size(), userId, preferredGenres.size(), preferredActors.size(), preferredDirectors.size());
        
        if (recommendations.isEmpty()) {
            logger.warn("Personalised pool empty for user {}; using top trending", userId);
            return topTrendingExcluding(watchlistMovieIds);
        }

        return recommendations;
    }

    /**
     * Up to {@link #MAX_RECOMMENDATIONS} trending movies, preserving TMDB order, excluding watchlist IDs.
     */
    private List<MovieDTO> topTrendingExcluding(Set<Integer> excludeIds) {
        Set<Integer> exclude = excludeIds != null ? excludeIds : Collections.emptySet();
        Optional<MoviesTrendingDTO> trendingOpt = movieClient.getTrendingMovies();
        if (trendingOpt.isEmpty() || trendingOpt.get().getResults() == null) {
            logger.warn("Could not load trending movies for default recommendations");
            return Collections.emptyList();
        }
        return trendingOpt.get().getResults().stream()
                .filter(movie -> movie != null && !exclude.contains(movie.getId()))
                .limit(MAX_RECOMMENDATIONS)
                .collect(Collectors.toList());
    }

    private Set<String> extractGenres(List<MovieDTO> movies) {
        return movies.stream()
                .filter(m -> m.getGenres() != null)
                .flatMap(m -> m.getGenres().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> extractActors(List<MovieDTO> movies) {
        return movies.stream()
                .filter(m -> m.getCast() != null)
                .flatMap(m -> m.getCast().stream())
                .filter(Objects::nonNull)
                .map(actor -> actor.getName())
                .filter(Objects::nonNull)
                .limit(20) // Top 20 actors
                .collect(Collectors.toSet());
    }

    private Set<String> extractDirectors(List<MovieDTO> movies) {
        return movies.stream()
                .filter(m -> m.getCrew() != null)
                .flatMap(m -> m.getCrew().stream())
                .filter(Objects::nonNull)
                .filter(crew -> "Director".equalsIgnoreCase(crew.getJob()))
                .map(crew -> crew.getName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private MovieScore scoreMovie(MovieDTO movie, Set<String> preferredGenres, 
                                   Set<String> preferredActors, Set<String> preferredDirectors) {
        double score = 0.0;

        // Genre matching (weight: 3.0 per matching genre)
        if (movie.getGenres() != null) {
            long genreMatches = movie.getGenres().stream()
                    .filter(Objects::nonNull)
                    .filter(preferredGenres::contains)
                    .count();
            score += genreMatches * 3.0;
        }

        // Actor matching (weight: 2.0 per matching actor)
        if (movie.getCast() != null) {
            long actorMatches = movie.getCast().stream()
                    .filter(Objects::nonNull)
                    .map(actor -> actor.getName())
                    .filter(Objects::nonNull)
                    .filter(preferredActors::contains)
                    .count();
            score += actorMatches * 2.0;
        }

        // Director matching (weight: 4.0 per matching director)
        if (movie.getCrew() != null) {
            long directorMatches = movie.getCrew().stream()
                    .filter(Objects::nonNull)
                    .filter(crew -> "Director".equalsIgnoreCase(crew.getJob()))
                    .map(crew -> crew.getName())
                    .filter(Objects::nonNull)
                    .filter(preferredDirectors::contains)
                    .count();
            score += directorMatches * 4.0;
        }

        return new MovieScore(movie, score);
    }

    private static class MovieScore {
        final MovieDTO movie;
        final double score;

        MovieScore(MovieDTO movie, double score) {
            this.movie = movie;
            this.score = score;
        }
    }
}
