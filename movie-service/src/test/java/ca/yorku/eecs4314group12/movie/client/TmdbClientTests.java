package ca.yorku.eecs4314group12.movie.client;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import ca.yorku.eecs4314group12.movie.dto.tmdb.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

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
	void test_1_getMovieDetails_US() throws InterruptedException {
		String fakeJson = """
			{
				"adult": false,
				"id": 550,
				"original_language": "en",
				"original_title": "Fight Club",
				"title": "Fight Club",
				"poster_path": "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
				"backdrop_path": "/xRyINp9KfMLVjRiO5nCsoRDdvvF.jpg",
				"overview": "Sample as original overview is too long.",
				"genres": [
				  {
				   	"id": 18,
				   	"name": "Drama"
				  },
				  {
				   	"id": 53,
				   	"name": "Thriller"
				  },
				  {
				   	"id": 35,
				   	"name": "Comedy"
				  }
				],
				"release_dates": {
				 "results": [
				    {
				     "iso_3166_1": "US",
				     "release_dates": [
				       {
				         "certification": "R",
					     "descriptors": [],
				         "iso_639_1": "",
				         "note": "",
				         "release_date": "1999-10-15T00:00:00.000Z",
				         "type": 3
				       }
				     ]
				    }
				 ] 
				},
				"credits": {
				 "cast": [
				   {
				     "adult": false,
				     "gender": 2,
				     "id": 819,
				     "known_for_department": "Acting",
				     "name": "Edward Norton",
				     "original_name": "Edward Norton",
				     "popularity": 5.8964,
				     "profile_path": "/8nytsqL59SFJTVYVrN72k6qkGgJ.jpg",
				     "cast_id": 4,
				     "character": "Narrator",
				     "credit_id": "52fe4250c3a36847f80149f3",
				     "order": 0
				   }
				 ],
				 "crew": [
				   {
				     "adult": false,
				     "gender": 2,
				     "id": 7764,
				     "known_for_department": "Sound",
				     "name": "Richard Hymns",
				     "original_name": "Richard Hymns",
				     "popularity": 0.6266,
				     "profile_path": "/970GjgH2nfqsnEsimqLvLYoYTQn.jpg",
				     "credit_id": "52fe4250c3a36847f8014a41",
				     "department": "Sound",
				     "job": "Sound Editor"
				   }
				 ]
				},
				"images": {
				 "backdrops": [
				   {
				     "aspect_ratio": 1.778,
					 "height": 1080,
				     "iso_3166_1": null,
				     "iso_639_1": null,
				     "file_path": "/xRyINp9KfMLVjRiO5nCsoRDdvvF.jpg",
				     "vote_average": 5.5,
				     "vote_count": 19,
				     "width": 1920
				   }
				 ]
				},
				"videos": {
				 "results": [
				   {
				     "iso_639_1": "en",
				     "iso_3166_1": "US",
				     "name": "20th Anniversary Trailer",
				     "key": "dfeUzm6KF4g",
				     "site": "YouTube",
				     "size": 1080,
				     "type": "Trailer",
				     "official": true,
				     "published_at": "2019-10-15T18:59:47.000Z",
				     "id": "64fb16fbdb4ed610343d72c3"
				   }
				 ]
				}
			}
            """;
		 
		mockServer.enqueue(new MockResponse()
	                .setBody(fakeJson)
	                .addHeader("Content-Type", "application/json"));
		
		TmdbMovieDTO result = tmdbClient.getMovieDetails(550);
		
		assertNotNull(result);
		assertFalse(result.isAdult());
        assertEquals(550, result.getId());
        assertEquals("en", result.getOriginal_language());
        assertEquals("Fight Club", result.getOriginal_title());
        assertEquals("Fight Club", result.getTitle());
        assertEquals("/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg", result.getPoster_path());
        assertEquals("/xRyINp9KfMLVjRiO5nCsoRDdvvF.jpg", result.getBackdrop_path());
        assertEquals("Sample as original overview is too long.", result.getOverview());
        assertFalse(result.getGenres().isEmpty());
        assertFalse(result.getImages().getBackdrops().isEmpty());
        assertFalse(result.getCredits().getCast().isEmpty());
        assertFalse(result.getCredits().getCrew().isEmpty());
        assertFalse(result.getVideos().getResults().isEmpty());
        assertFalse(result.getRelease_dates().getResults().isEmpty());
        
        
        RecordedRequest request = mockServer.takeRequest();
        assertTrue(request.getPath().contains("/movie/550"));	
	}
	
	@Test
	void test_2_getMovieDetails_Foreign() throws InterruptedException {
		String fakeJson = """
			{
				"adult": false,
				"id": 496243,
				"original_language": "ko",
				"original_title": "기생충",
                "title": "Parasite",
                "genres": [
				{
				  "id": 35,
				  "name": "Comedy"
				},
				{
				  "id": 53,
				  "name": "Thriller"
				},
				{
				  "id": 18,
				  "name": "Drama"
				}
				]
            }
            """;
		 
		mockServer.enqueue(new MockResponse()
	                .setBody(fakeJson)
	                .addHeader("Content-Type", "application/json"));
		
		TmdbMovieDTO result = tmdbClient.getMovieDetails(496243);
		
		assertNotNull(result);
        assertEquals(496243, result.getId());
        assertEquals("ko", result.getOriginal_language());
        assertEquals("기생충", result.getOriginal_title());
        assertEquals("Parasite", result.getTitle());
        assertFalse(result.getGenres().isEmpty());
        
        RecordedRequest request = mockServer.takeRequest();
        assertTrue(request.getPath().contains("/movie/496243"));	
	}
	
	@Test
    void test_3_getMoviesTrending() throws InterruptedException {
        String fakeJson = """
                {
                  "results": [
                    {
                      "id": 934433,
                      "title": "Scream VI",
                      "original_title": "Scream VI",
                      "genre_ids": [27, 9648, 53]
                    },
                    {
                      "id": 502356,
                      "title": "The Super Mario Bros. Movie",
                      "original_title": "The Super Mario Bros. Movie",
                      "genre_ids": [16, 12, 10751, 14, 35]
                    },
                    {
                      "id": 603692,
                      "title": "John Wick: Chapter 4",
                      "original_title": "John Wick: Chapter 4",
                      "genre_ids": [28, 53, 80]
                    }
                  ]
                }
                """;

        mockServer.enqueue(new MockResponse()
                .setBody(fakeJson)
                .addHeader("Content-Type", "application/json"));

        TmdbMoviesTrendingDTO result = tmdbClient.getMoviesTrending();

        assertNotNull(result);
        assertNotNull(result.getResults());
        assertEquals(3, result.getResults().size());

        TmdbMovieDTO first = result.getResults().get(0);
        assertEquals(934433, first.getId());
        assertEquals("Scream VI", first.getTitle());
        assertEquals("Scream VI", first.getOriginal_title());
        assertNotNull(first.getGenre_ids());

        TmdbMovieDTO second = result.getResults().get(1);
        assertEquals(502356, second.getId());
        assertEquals("The Super Mario Bros. Movie", second.getTitle());
        assertEquals("The Super Mario Bros. Movie", second.getOriginal_title());
        assertNotNull(second.getGenre_ids());
  
        TmdbMovieDTO third = result.getResults().get(2);
        assertEquals(603692, third.getId());
        assertEquals("John Wick: Chapter 4", third.getTitle());
        assertNotNull(third.getGenre_ids());


        RecordedRequest request = mockServer.takeRequest();

        assertTrue(request.getPath().contains("/trending/movie/day"));  
    }
	
	@Test
	void test_4_getMoviesNowPlaying() throws InterruptedException {
	    String fakeJson = """
	            {
	              "results": [
	                {
	                  "id": 502356,
	                  "title": "The Super Mario Bros. Movie",
	                  "original_title": "The Super Mario Bros. Movie",
	                  "genre_ids": [16, 12, 10751, 14, 35]
	                },
	                {
	                  "id": 594767,
	                  "title": "Shazam! Fury of the Gods",
	                  "original_title": "Shazam! Fury of the Gods",
	                  "genre_ids": [28, 35, 14]
	                },
	                {
	                  "id": 713704,
	                  "title": "Evil Dead Rise",
	                  "original_title": "Evil Dead Rise",
	                  "genre_ids": [27, 53]
	                }
	              ]
	            }
	            """;

	    mockServer.enqueue(new MockResponse()
	            .setBody(fakeJson)
	            .addHeader("Content-Type", "application/json"));

	    TmdbMoviesNowPlayingDTO result = tmdbClient.getMoviesNowPlaying();

	    assertNotNull(result);
	    assertNotNull(result.getResults());
	    assertEquals(3, result.getResults().size());

	    TmdbMovieDTO first = result.getResults().get(0);
	    assertEquals(502356, first.getId());
	    assertEquals("The Super Mario Bros. Movie", first.getTitle());
	    assertEquals("The Super Mario Bros. Movie", first.getOriginal_title());
	    assertNotNull(first.getGenre_ids());

	    TmdbMovieDTO second = result.getResults().get(1);
	    assertEquals(594767, second.getId());
	    assertEquals("Shazam! Fury of the Gods", second.getTitle());
	    assertEquals("Shazam! Fury of the Gods", second.getOriginal_title());
	    assertNotNull(second.getGenre_ids());

	    TmdbMovieDTO third = result.getResults().get(2);
	    assertEquals(713704, third.getId());
	    assertEquals("Evil Dead Rise", third.getTitle());
	    assertEquals("Evil Dead Rise", third.getOriginal_title());
	    assertNotNull(third.getGenre_ids());

	    RecordedRequest request = mockServer.takeRequest();

	    assertTrue(request.getPath().contains("/movie/now_playing"));
	}
	
	@Test
	void test_5_getMovieSearch() throws InterruptedException {
	    String fakeJson = """
	            {
	              "results": [
	                {
	                  "id": 550,
	                  "title": "Fight Club",
	                  "original_title": "Fight Club",
	                  "genre_ids": [18, 53, 35]
	                },
	                {
	                  "id": 289732,
	                  "title": "Zombie Fight Club",
	                  "original_title": "屍城",
	                  "genre_ids": [28, 27]
	                },
	                {
	                  "id": 323667,
	                  "title": "Florence Fight Club",
	                  "original_title": "Вставай и бейся",
	                  "genre_ids": [35, 18]
	                }
	              ]
	            }
	            """;

	    mockServer.enqueue(new MockResponse()
	            .setBody(fakeJson)
	            .addHeader("Content-Type", "application/json"));

	    TmdbMovieSearchDTO result = tmdbClient.getMovieSearch("fight club");  

	    assertNotNull(result);
	    assertNotNull(result.getResults());
	    assertEquals(3, result.getResults().size());

	    TmdbMovieDTO first = result.getResults().get(0);
	    assertEquals(550, first.getId());
	    assertEquals("Fight Club", first.getTitle());
	    assertEquals("Fight Club", first.getOriginal_title());
	    assertNotNull(first.getGenre_ids());

	    TmdbMovieDTO second = result.getResults().get(1);
	    assertEquals(289732, second.getId());
	    assertEquals("Zombie Fight Club", second.getTitle());
	    assertEquals("屍城", second.getOriginal_title());
	    assertNotNull(second.getGenre_ids());

	    TmdbMovieDTO third = result.getResults().get(2);
	    assertEquals(323667, third.getId());
	    assertEquals("Florence Fight Club", third.getTitle());
	    assertEquals("Вставай и бейся", third.getOriginal_title());
	    assertNotNull(third.getGenre_ids());

	    RecordedRequest request = mockServer.takeRequest();
	    
	    assertTrue(request.getPath().contains("/search/movie?query=fight%20club"));  
	}
}
