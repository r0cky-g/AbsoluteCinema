package ca.yorku.eecs4314group12.movie.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ca.yorku.eecs4314group12.movie.client.TmdbClient;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;

@ExtendWith(MockitoExtension.class)
class MovieServiceTests {
	
	@Mock
	private TmdbClient tmdbClient;
	
	@InjectMocks
	private MovieService movieService;

	@Test
	void test_1_GetDetails() {
		TmdbMovieDTO fakeResponse = new TmdbMovieDTO();
		fakeResponse.setId(550);
		fakeResponse.setOriginal_title("Fight Club");
		fakeResponse.setTitle("Fight Club");
		
		when(tmdbClient.getMovieDetails(550)).thenReturn(fakeResponse);
		
		TmdbMovieDTO result = movieService.getDetails(550);
		
		assertNotNull(result);
		assertEquals(550, result.getId());
		assertEquals("Fight Club", result.getOriginal_title());
		assertEquals("Fight Club", result.getTitle());
	}
	
	@Test
	void test_2_GetDetails() {
		TmdbMovieDTO fakeResponse = new TmdbMovieDTO();
		fakeResponse.setId(496243);
		fakeResponse.setOriginal_title("기생충");
		fakeResponse.setTitle("Parasite");
		
		when(tmdbClient.getMovieDetails(496243)).thenReturn(fakeResponse);
		
		TmdbMovieDTO result = movieService.getDetails(496243);
		
		assertNotNull(result);
		assertEquals(496243, result.getId());
		assertEquals("기생충", result.getOriginal_title());
		assertEquals("Parasite", result.getTitle());
	}
}
