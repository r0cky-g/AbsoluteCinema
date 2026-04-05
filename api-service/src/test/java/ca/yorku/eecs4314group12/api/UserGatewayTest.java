package ca.yorku.eecs4314group12.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserGatewayTest {

    @LocalServerPort
    private int gatewayPort;

    static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        wireMockServer.start();
        registry.add("app.user-service.url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void setup() {
        WireMock.configureFor("localhost", wireMockServer.port());

        String registerRequest = """ 
            {
                "username": "testuser", 
                "email": "test@example.com", 
                "password": "pass123", 
                "over18": true 
            } 
            """;

        String responseDTO = """
            {
                "id": 1,
                "username": "testuser",
                "email": "test@example.com",
                "emailVerified": false,
                "role": "USER",
                "likedGenres": []
            }
            """;

        // Stub the downstream user service
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/register"))
            .withRequestBody(WireMock.equalToJson(registerRequest))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(responseDTO)
            ));
    }

    @AfterEach
    void teardown() {
        wireMockServer.resetAll();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @Test
    void testRegister() {
        WebTestClient webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + gatewayPort)
                .build();

        Map<String, Object> requestBody = Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "pass123",
                "over18", true
        );

        Map<String, Object> responseBody = webTestClient.post()
                .uri("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk() // check HTTP 200
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();
        
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.get("id")).isEqualTo(1);
        assertThat(responseBody.get("username")).isEqualTo("testuser");
        assertThat(responseBody.get("email")).isEqualTo("test@example.com");
        assertThat(responseBody.get("role")).isEqualTo("USER");
        assertThat(responseBody.get("likedGenres")).asList().isEmpty();
    }
}