package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.client.MovieClient;
import ca.yorku.eecs4314group12.user.dto.MovieDTO;
import ca.yorku.eecs4314group12.user.dto.MoviesTrendingDTO;
import ca.yorku.eecs4314group12.user.model.Watchlist;
import ca.yorku.eecs4314group12.user.repository.WatchlistRepository;
import ca.yorku.eecs4314group12.user.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private MovieClient movieClient;

    @Mock
    private WatchlistRepository watchlistRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private final long userId = 42L;

    @BeforeEach
    void setUp() {
        lenient().when(watchlistRepository.findByUserId(userId)).thenReturn(List.of());
    }

    @Test
    void emptyWatchlist_returnsFirstFiveTrending() {
        List<MovieDTO> trending = IntStream.rangeClosed(1, 10)
                .mapToObj(this::simpleMovie)
                .toList();
        when(movieClient.getTrendingMovies()).thenReturn(Optional.of(wrapTrending(trending)));

        List<MovieDTO> result = recommendationService.getRecommendedMovies(userId);

        assertEquals(5, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(5, result.get(4).getId());
        verify(movieClient, times(1)).getTrendingMovies();
        verify(movieClient, never()).getMovieById(anyInt());
    }

    @Test
    void watchlistWithNoMetadata_fallsBackToTrendingExcludingWatchlistIds() {
        Watchlist w = new Watchlist(userId, 100);
        when(watchlistRepository.findByUserId(userId)).thenReturn(List.of(w));
        when(movieClient.getMovieById(100)).thenReturn(Optional.empty());

        List<MovieDTO> trending = IntStream.rangeClosed(1, 6)
                .mapToObj(this::simpleMovie)
                .toList();
        when(movieClient.getTrendingMovies()).thenReturn(Optional.of(wrapTrending(trending)));

        List<MovieDTO> result = recommendationService.getRecommendedMovies(userId);

        assertEquals(5, result.size());
        assertTrue(result.stream().noneMatch(m -> m.getId() == 100));
        verify(movieClient, times(1)).getTrendingMovies();
    }

    @Test
    void personalized_prefersMoviesMatchingWatchlistGenres() {
        Watchlist w = new Watchlist(userId, 99);
        when(watchlistRepository.findByUserId(userId)).thenReturn(List.of(w));

        MovieDTO watchlistMovie = movieWithGenres(99, List.of("Sci-Fi"));
        when(movieClient.getMovieById(99)).thenReturn(Optional.of(watchlistMovie));

        MovieDTO strongMatch = movieWithGenres(1, List.of("Sci-Fi"));
        MovieDTO weakMatch = movieWithGenres(2, List.of("Romance"));
        List<MovieDTO> trending = new ArrayList<>();
        trending.add(weakMatch);
        trending.add(strongMatch);
        when(movieClient.getTrendingMovies()).thenReturn(Optional.of(wrapTrending(trending)));

        List<MovieDTO> result = recommendationService.getRecommendedMovies(userId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getId(), "Higher genre overlap should rank first");
    }

    @Test
    void personalized_whenTrendingMissingForScoring_usesTrendingFallback() {
        Watchlist w = new Watchlist(userId, 10);
        when(watchlistRepository.findByUserId(userId)).thenReturn(List.of(w));
        when(movieClient.getMovieById(10)).thenReturn(Optional.of(simpleMovie(10)));

        when(movieClient.getTrendingMovies())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(wrapTrending(List.of(simpleMovie(20), simpleMovie(21)))));

        List<MovieDTO> result = recommendationService.getRecommendedMovies(userId);

        assertEquals(2, result.size());
        verify(movieClient, times(2)).getTrendingMovies();
    }

    @Test
    void emptyWatchlist_whenTrendingUnavailable_returnsEmpty() {
        when(movieClient.getTrendingMovies()).thenReturn(Optional.empty());

        List<MovieDTO> result = recommendationService.getRecommendedMovies(userId);

        assertTrue(result.isEmpty());
    }

    private MoviesTrendingDTO wrapTrending(List<MovieDTO> movies) {
        MoviesTrendingDTO dto = new MoviesTrendingDTO();
        dto.setResults(movies);
        return dto;
    }

    private MovieDTO simpleMovie(int id) {
        MovieDTO m = new MovieDTO();
        m.setId(id);
        m.setTitle("Movie " + id);
        return m;
    }

    private MovieDTO movieWithGenres(int id, List<String> genres) {
        MovieDTO m = simpleMovie(id);
        m.setGenres(genres);
        return m;
    }
}
