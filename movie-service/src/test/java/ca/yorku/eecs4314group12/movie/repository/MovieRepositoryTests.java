package ca.yorku.eecs4314group12.movie.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import ca.yorku.eecs4314group12.movie.document.Movie;

@DataMongoTest
@Testcontainers
class MovieRepositoryTests {

	@SuppressWarnings("resource")
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo")
			.withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
	        .withEnv("MONGO_INITDB_ROOT_PASSWORD", "example");
	
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
	    registry.add("spring.mongodb.uri", () ->
	        "mongodb://root:example@" + mongoDBContainer.getHost() + ":" + mongoDBContainer.getFirstMappedPort()
	    );
	}
	
	@Autowired
	private MovieRepository movRepo;
	
	@BeforeEach
	void setUp() {
		movRepo.deleteAll();
	}
	
	@Test
	void test_1_saveMovie() {
		Movie movie = new Movie();
		movie.setId(650);
		movie.setTitle("Fight Test");
		
		movRepo.save(movie);
		
		Movie result = movRepo.findById(650).orElseThrow();
		
		assertEquals(movie.getId(), result.getId());
	}
	
	@Test
	void test_2_findMoviesByTitle_caseInsensitive_keyword() {
		Movie movie1 = new Movie();
		movie1.setId(550);
		movie1.setTitle("Fight Club");
		Movie movie2 = new Movie();
		movie2.setId(240);
		movie2.setTitle("Fight or Flight");
		Movie movie3 = new Movie();
		movie3.setId(130);
		movie3.setTitle("The Best Fight");
		Movie movie4 = new Movie();
		movie4.setId(450);
		movie4.setTitle("How to Train Your Dragon");
		Movie movie5 = new Movie();
		movie5.setId(670);
		movie5.setTitle("Spider-Man");
		
		movRepo.save(movie1);
		movRepo.save(movie2);
		movRepo.save(movie3);
		movRepo.save(movie4);
		movRepo.save(movie5);
		
		List<Movie> results = movRepo.findByTitle("fight");
		
		assertEquals(3, results.size());
		assertEquals("Fight Club", results.get(0).getTitle());
		assertEquals("Fight or Flight", results.get(1).getTitle());
		assertEquals("The Best Fight", results.get(2).getTitle());
	}
}
