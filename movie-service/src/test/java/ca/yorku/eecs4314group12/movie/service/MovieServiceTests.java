package ca.yorku.eecs4314group12.movie.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ca.yorku.eecs4314group12.movie.client.TmdbClient;
import ca.yorku.eecs4314group12.movie.document.Movie;
import ca.yorku.eecs4314group12.movie.dto.*;
import ca.yorku.eecs4314group12.movie.dto.tmdb.*;
import ca.yorku.eecs4314group12.movie.mapper.MovieMapper;
import ca.yorku.eecs4314group12.movie.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceTests {
	
	@Mock
    private MovieRepository movRepo;

    @Mock
    private MovieMapper movMap;

    @Mock
    private TmdbClient tmdbClient;

    @InjectMocks
    private MovieService movieService;

    @Test
    void test_1_getDetails_MovieFound() {
        Movie movie = new Movie();
        movie.setId(550);
        movie.setTitle("Fight Club");
        movie.setOverview("An insomniac and a soap salesman form an underground fight club.");

        MovieDTO expectedMovieDTO = new MovieDTO();
        expectedMovieDTO.setId(550);
        expectedMovieDTO.setTitle("Fight Club");
        expectedMovieDTO.setOverview("An insomniac and a soap salesman form an underground fight club.");

        when(movRepo.findById(550)).thenReturn(Optional.of(movie));
        when(movMap.toMovieDTO(movie)).thenReturn(expectedMovieDTO);

        MovieDTO result = movieService.getDetails(550);

        assertNotNull(result);
        assertEquals(550, result.getId());
        assertEquals("Fight Club", result.getTitle());
        assertEquals("An insomniac and a soap salesman form an underground fight club.", result.getOverview());

        verify(movRepo, times(1)).findById(550);
        verify(movMap, times(1)).toMovieDTO(movie);
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void test_2_getDetails_MovieNotFound() {
        TmdbMovieDTO tmdbMovie = new TmdbMovieDTO();
        tmdbMovie.setId(550);
        tmdbMovie.setTitle("Fight Club");
        tmdbMovie.setOverview("An insomniac and a soap salesman form an underground fight club.");
        
        Movie movie = new Movie();
        movie.setId(550);
        movie.setTitle("Fight Club");
        movie.setOverview("An insomniac and a soap salesman form an underground fight club.");

        MovieDTO expectedMovieDTO = new MovieDTO();
        expectedMovieDTO.setId(550);
        expectedMovieDTO.setTitle("Fight Club");
        expectedMovieDTO.setOverview("An insomniac and a soap salesman form an underground fight club.");

        when(movRepo.findById(550)).thenReturn(Optional.empty());
        when(tmdbClient.getMovieDetails(550)).thenReturn(tmdbMovie);
        when(movMap.toMovie(tmdbMovie)).thenReturn(movie);
        when(movRepo.save(movie)).thenReturn(movie);
        when(movMap.toMovieDTO(movie)).thenReturn(expectedMovieDTO);

        MovieDTO result = movieService.getDetails(550);

        assertNotNull(result);
        assertEquals(550, result.getId());
        assertEquals("Fight Club", result.getTitle());
        assertEquals("An insomniac and a soap salesman form an underground fight club.", result.getOverview());

        verify(movRepo, times(1)).findById(550);
        verify(tmdbClient, times(1)).getMovieDetails(550);
        verify(movMap, times(1)).toMovie(tmdbMovie);
        verify(movRepo, times(1)).save(movie);
        verify(movMap, times(1)).toMovieDTO(movie);
    }

    @Test
    void test_3_getDetails_MovieFound_NoOverview() {
        Movie movieNoOverview = new Movie();
        movieNoOverview.setId(550);
        movieNoOverview.setTitle("Fight Club");
        movieNoOverview.setOverview("");  

        TmdbMovieDTO tmdbMovie = new TmdbMovieDTO();
        tmdbMovie.setId(550);
        tmdbMovie.setTitle("Fight Club");
        tmdbMovie.setOverview("An insomniac and a soap salesman form an underground fight club.");
        
        Movie movie = new Movie();
        movie.setId(550);
        movie.setTitle("Fight Club");
        movie.setOverview("An insomniac and a soap salesman form an underground fight club.");

        MovieDTO expectedMovieDTO = new MovieDTO();
        expectedMovieDTO.setId(550);
        expectedMovieDTO.setTitle("Fight Club");
        expectedMovieDTO.setOverview("An insomniac and a soap salesman form an underground fight club.");

        when(movRepo.findById(550)).thenReturn(Optional.of(movieNoOverview));
        when(tmdbClient.getMovieDetails(550)).thenReturn(tmdbMovie); 
        when(movMap.toMovie(tmdbMovie)).thenReturn(movie);
        when(movRepo.save(movie)).thenReturn(movie);
        when(movMap.toMovieDTO(movie)).thenReturn(expectedMovieDTO);

        MovieDTO result = movieService.getDetails(550);

        assertNotNull(result);
        assertEquals(550, result.getId());
        assertEquals("Fight Club", result.getTitle());
        assertEquals("An insomniac and a soap salesman form an underground fight club.", result.getOverview());

        verify(movRepo, times(1)).findById(550);
        verify(tmdbClient, times(1)).getMovieDetails(550);
        verify(movMap, times(1)).toMovie(tmdbMovie);
        verify(movRepo, times(1)).save(movie);
        verify(movMap, times(1)).toMovieDTO(movie);
    }

    @Test
    void test_4_getSearch_RepoHasEnoughResults() {
        List<Movie> cachedMovies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Movie m = new Movie();
            m.setId(i);
            m.setTitle("Fight Club " + i);
            cachedMovies.add(m);
        }
        
        List<MovieDTO> cachedMoviesDTO = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            MovieDTO m = new MovieDTO();
            m.setId(i);
            m.setTitle("Fight Club " + i);
            cachedMoviesDTO.add(m);
        }

        MovieSearchDTO expectedMovieSearchDTO = new MovieSearchDTO();
        expectedMovieSearchDTO.setResults(cachedMoviesDTO);

        when(movRepo.findByTitle("Fight Club")).thenReturn(cachedMovies);
        when(movMap.toMovieSearchDTO(cachedMovies)).thenReturn(expectedMovieSearchDTO);

        MovieSearchDTO result = movieService.getSearch("Fight Club");

        assertNotNull(result);
        assertEquals(20, result.getResults().size());

        verify(movRepo, times(1)).findByTitle("Fight Club");
        verify(movMap, times(1)).toMovieSearchDTO(cachedMovies);
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void test_5_getSearch_RepoDoesNotHaveEnoughResults() {
        List<Movie> partialResults = List.of(new Movie(), new Movie());  

        TmdbMovieSearchDTO tmdbDTO = new TmdbMovieSearchDTO();
        tmdbDTO.setResults(List.of(new TmdbMovieDTO()));
        
        MovieSearchDTO expectedSearchDTO = new MovieSearchDTO();
        expectedSearchDTO.setResults(List.of(new MovieDTO()));

        when(movRepo.findByTitle("Fight Club")).thenReturn(partialResults);
        when(tmdbClient.getMovieSearch("Fight Club")).thenReturn(tmdbDTO); 
        when(movMap.toMovieSearchDTO(tmdbDTO)).thenReturn(expectedSearchDTO);

        MovieSearchDTO result = movieService.getSearch("Fight Club");

        assertNotNull(result);

        verify(movRepo, times(1)).findByTitle("Fight Club");
        verify(tmdbClient, times(1)).getMovieSearch("Fight Club");  
    }

    @Test
    void test_6_getTrending() {
        TmdbMoviesTrendingDTO tmdbDTO = new TmdbMoviesTrendingDTO();
        tmdbDTO.setResults(List.of(new TmdbMovieDTO()));
        
        MoviesTrendingDTO expectedTrendingDTO = new MoviesTrendingDTO();
        expectedTrendingDTO.setResults(List.of(new MovieDTO()));

        when(tmdbClient.getMoviesTrending()).thenReturn(tmdbDTO); 
        when(movMap.toMoviesTrendingDTO(tmdbDTO)).thenReturn(expectedTrendingDTO);

        MoviesTrendingDTO result = movieService.getTrending();

        assertNotNull(result);

        verify(tmdbClient, times(1)).getMoviesTrending();  
        verifyNoInteractions(movRepo);
    }

    @Test
    void test_7_getNowPlaying() {
        TmdbMoviesNowPlayingDTO tmdbDTO = new TmdbMoviesNowPlayingDTO();
        tmdbDTO.setResults(List.of(new TmdbMovieDTO()));
        
        MoviesNowPlayingDTO expectedNowPlayingDTO = new MoviesNowPlayingDTO();
        expectedNowPlayingDTO.setResults(List.of(new MovieDTO()));

        when(tmdbClient.getMoviesNowPlaying()).thenReturn(tmdbDTO);
        when(movMap.toMoviesNowPlayingDTO(tmdbDTO)).thenReturn(expectedNowPlayingDTO);

        MoviesNowPlayingDTO result = movieService.getNowPlaying();

        assertNotNull(result);

        verify(tmdbClient, times(1)).getMoviesNowPlaying();
        verifyNoInteractions(movRepo);
    }
}
