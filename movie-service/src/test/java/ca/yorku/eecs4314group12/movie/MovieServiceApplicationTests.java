package ca.yorku.eecs4314group12.movie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

@SpringBootTest
@Testcontainers
class MovieServiceApplicationTests {
	
	@SuppressWarnings("resource")
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo")
			.withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
	        .withEnv("MONGO_INITDB_ROOT_PASSWORD", "example")
	        .withReuse(true);
	
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
	    registry.add("spring.mongodb.uri", () ->
	        "mongodb://root:example@" + mongoDBContainer.getHost() + ":" + mongoDBContainer.getFirstMappedPort()
	    );
	}

	@Test
	void contextLoads() {
	}

}
