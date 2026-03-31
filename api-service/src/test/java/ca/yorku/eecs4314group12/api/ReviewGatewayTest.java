package ca.yorku.eecs4314group12.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewGatewayTest {

    @LocalServerPort
    int port;

    private WebClient gateway;

    @BeforeEach
    void setup() {
        gateway = WebClient.create("http://localhost:" + port + "/api");
    }

    @Test
    void testReview() {

        int id = 407;
        System.out.println("id: " + id);

        Map<String, Object> review = Map.of(
            "id", id,
            "userId", 1,
            "movieId", 550,
            "rating", 8,
            "title", "test review",
            "content", "test review body",
            "isSpoiler", false,
            "helpfulCount", 1
        );

        Map<String, Object> reviewUpdate = Map.of(
            "id", id,
            "userId", 1,
            "movieId", 550,
            "rating", 8,
            "title", "test review - update",
            "content", "test review body",
            "isSpoiler", false,
            "helpfulCount", 1
        );

        // create review
        Map<String, Object> postResp = gateway.post()
            .uri("/reviews")
            .bodyValue(review)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();
        
        assertNotNull(postResp);
        assertEquals(true, postResp.get("success"));

        // get review by movie
        Map<String, Object> getByMovieResp = gateway.get()
            .uri("/reviews/movie/{movieId}", 550)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        assertNotNull(getByMovieResp);
        assertEquals(true, getByMovieResp.get("success"));

        // get review by user
        Map<String, Object> getByUserResp = gateway.get()
            .uri("/reviews/user/{userId}", 1)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        assertNotNull(getByUserResp);
        assertEquals(true, getByUserResp.get("success"));

        // get review by id
        Map<String, Object> getByIdResp = gateway.get()
            .uri("/reviews/{id}", id)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        assertNotNull(getByIdResp);
        assertEquals(true, getByIdResp.get("success"));

        // update review
        Map<String, Object> updateResp = gateway.put()
            .uri("/reviews/{id}", id)
            .bodyValue(reviewUpdate)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        assertNotNull(updateResp);
        assertEquals(true, updateResp.get("success"));

        // delete review
        Map<String, Object> deleteResp = gateway.delete()
            .uri("/reviews/{id}?userId=1", id)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        assertNotNull(deleteResp);
        assertEquals(true, deleteResp.get("success"));
    }
}