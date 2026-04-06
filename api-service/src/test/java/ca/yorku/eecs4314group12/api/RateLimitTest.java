package ca.yorku.eecs4314group12.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RateLimitTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @Test
    // @Disabled("rate-limiting disabled for endpoint testing")
    void testRateLimitExceeded() {
        // Build client against the running test server
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        // First 20 requests should succeed
        for (int i = 0; i < 20; i++) {
            webTestClient.get()
                    .uri("/api/test")
                    .exchange()
                    .expectStatus().isOk();
        }

        // 21st request should be rate-limited
        webTestClient.get()
                .uri("/api/test")
                .exchange()
                .expectStatus().isEqualTo(429);
    }
}