package ca.yorku.eecs4314group12.movie.client;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;

class TmdbClientTests {

	private MockWebServer mockServer;
	private TmdbClient tmdbClient;
	
	@BeforeEach
	void setUp() throws IOException {
		mockServer = new MockWebServer();
		mockServer.start();
		
		WebClient webClient = WebClient.builder()
				.baseUrl(mockServer.url("/").toString())
				.build();
		
		tmdbClient = new TmdbClient(webClient, "dummy_token");
	}

	@AfterEach
	void tearDown() throws IOException {
		mockServer.shutdown();
	}

	@Test
	void test_1_GetMovieDetails() throws InterruptedException {
		String fakeJson = """
                {
                  "id": 550,
                  "original_title": "Fight Club",
                  "title": "Fight Club"
                }
                """;
		 
		mockServer.enqueue(new MockResponse()
	                .setBody(fakeJson)
	                .addHeader("Content-Type", "application/json"));
		
		TmdbMovieDTO result = tmdbClient.getMovieDetails(550);
		
		assertNotNull(result);
        assertEquals(550, result.getId());
        assertEquals("Fight Club", result.getOriginal_title());
        assertEquals("Fight Club", result.getTitle());
        
        RecordedRequest request = mockServer.takeRequest();
        assertTrue(request.getPath().contains("/movie/550"));	
	}
	
	@Test
	void test_2_GetMovieDetails() throws InterruptedException {
		String fakeJson = """
                {
                  "id": 496243,
                  "original_title": "기생충",
                  "title": "Parasite"
                }
                """;
		 
		mockServer.enqueue(new MockResponse()
	                .setBody(fakeJson)
	                .addHeader("Content-Type", "application/json"));
		
		TmdbMovieDTO result = tmdbClient.getMovieDetails(496243);
		
		assertNotNull(result);
        assertEquals(496243, result.getId());
        assertEquals("기생충", result.getOriginal_title());
        assertEquals("Parasite", result.getTitle());
        
        RecordedRequest request = mockServer.takeRequest();
        assertTrue(request.getPath().contains("/movie/496243"));	
	}
}
