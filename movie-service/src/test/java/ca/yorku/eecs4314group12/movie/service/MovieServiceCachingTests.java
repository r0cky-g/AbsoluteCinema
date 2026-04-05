package ca.yorku.eecs4314group12.movie.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ca.yorku.eecs4314group12.movie.client.TmdbClient;
import ca.yorku.eecs4314group12.movie.dto.tmdb.TmdbMovieDTO;
import ca.yorku.eecs4314group12.movie.dto.tmdb.TmdbMoviesNowPlayingDTO;
import ca.yorku.eecs4314group12.movie.dto.tmdb.TmdbMoviesTrendingDTO;
import ca.yorku.eecs4314group12.movie.repository.MovieRepository;

@SpringBootTest
@EnableCaching
@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
class MovieServiceCachingTests {
	
	@MockitoBean
	private MovieRepository movieRepo;
	
	@MockitoBean
	private TmdbClient tmdbClient;

    @Autowired
    private MovieService movieService;

    @Autowired
    CacheManager cacheManager;
    
    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames()
                .forEach(name -> cacheManager.getCache(name).clear());
        
        TmdbMovieDTO movie1 = new TmdbMovieDTO();
        TmdbMovieDTO movie2 = new TmdbMovieDTO();
        TmdbMovieDTO movie3 = new TmdbMovieDTO();
        TmdbMovieDTO movie4 = new TmdbMovieDTO();
        
        movie1.setId(1);
        movie2.setId(2);
        movie3.setId(3);
        movie4.setId(4);
        movie1.setGenre_ids(List.of(1,2));
        movie2.setGenre_ids(List.of(1,2));
        movie3.setGenre_ids(List.of(1,2));
        movie4.setGenre_ids(List.of(1,2));
        
        TmdbMoviesTrendingDTO trending = new TmdbMoviesTrendingDTO();
        TmdbMoviesNowPlayingDTO nowPlaying = new TmdbMoviesNowPlayingDTO();
        
        trending.setResults(List.of(movie1, movie2));
        nowPlaying.setResults(List.of(movie3, movie4));
        
        when(tmdbClient.getMoviesTrending()).thenReturn(trending);
        when(tmdbClient.getMoviesNowPlaying()).thenReturn(nowPlaying);
    }

    @Test
    void test_1_getMoviesTrendingCache() {
        movieService.getTrending();
        movieService.getTrending();

        verify(tmdbClient).getMoviesTrending();
        verifyNoMoreInteractions(tmdbClient);
    }

    @Test
    void test_2_getMoviesNowPlayingCache() {
        movieService.getNowPlaying();
        movieService.getNowPlaying();

        verify(tmdbClient).getMoviesNowPlaying();
        verifyNoMoreInteractions(tmdbClient);
    }

    @Test
    void test_3_getMoviesTrendingAndMoviesNowPlayingCaches() {
        movieService.getTrending();
        movieService.getTrending();
        movieService.getNowPlaying();
        movieService.getNowPlaying();

        verify(tmdbClient).getMoviesTrending();
        verify(tmdbClient).getMoviesNowPlaying();
        verifyNoMoreInteractions(tmdbClient);
    }
}