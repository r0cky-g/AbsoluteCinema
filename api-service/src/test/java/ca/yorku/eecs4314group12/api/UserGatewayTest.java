package ca.yorku.eecs4314group12.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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

        // ----------------- USER STUBS -----------------
        String registerRequest = """
            { "username": "testuser", "email": "test@example.com", "password": "pass123", "over18": true }
            """;
        String registerResponse = """
            { "id": 1, "username": "testuser", "email": "test@example.com", "emailVerified": false, "role": "USER", "likedGenres": [] }
            """;
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/register"))
                .withRequestBody(WireMock.equalToJson(registerRequest))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(registerResponse)));

        String loginRequest = """
            { "identifier": "testuser", "password": "pass123" }
            """;
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/login"))
                .withRequestBody(WireMock.equalToJson(loginRequest))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(registerResponse)));

        wireMockServer.stubFor(WireMock.post(WireMock.urlPathMatching("/user/1/verify"))
                .withQueryParam("code", WireMock.equalTo("123456"))
                .willReturn(WireMock.aResponse().withStatus(200).withBody("Email verified successfully")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/user"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[" + registerResponse + "]")));

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/user/1"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(registerResponse)));

        String updateRequest = """
            {"username": "updateduser", "email": "updated@example.com", "password": "newpass123", "dob": "2000-01-01", "over18": true, "likedGenres": ["Action","Drama"]}
            """;
        String updateResponse = """
            {"id": 1, "username": "updateduser", "email": "updated@example.com", "emailVerified": false, "role": "USER", "likedGenres": ["Action","Drama"]}
            """;
        wireMockServer.stubFor(WireMock.put(WireMock.urlEqualTo("/user/1"))
                .withRequestBody(WireMock.equalToJson(updateRequest))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(updateResponse)));

        wireMockServer.stubFor(WireMock.delete(WireMock.urlPathEqualTo("/user/1"))
                .willReturn(WireMock.aResponse().withStatus(204)));

        // ----------------- ADMIN STUBS -----------------
        String adminRequest = """
            { "adminIdentifier": "admin1" }
            """;
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/1/promote-moderator"))
                .withRequestBody(WireMock.equalToJson(adminRequest))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(registerResponse)));
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/1/demote-moderator"))
                .withRequestBody(WireMock.equalToJson(adminRequest))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(registerResponse)));

        // ----------------- WATCHLIST STUBS -----------------
        String watchlistResponse = """
            { "id": 1, "userId": 1, "movieId": 101, "addedAt": "2026-04-05T17:10:00" }
            """;
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/1/watchlist/101"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(watchlistResponse)));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/user/1/watchlist"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[" + watchlistResponse + "]")));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/user/1/watchlist/101"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("true")));
        wireMockServer.stubFor(WireMock.delete(WireMock.urlEqualTo("/user/1/watchlist/101"))
                .willReturn(WireMock.aResponse().withStatus(204)));

        // ----------------- HISTORY STUBS -----------------
        String historyResponse = """
            { "id": 1, "userId": 1, "movieId": 101, "watchedAt": "2026-04-05T17:10:00" }
            """;
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/1/history/101"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(historyResponse)));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/user/1/history"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[" + historyResponse + "]")));
        wireMockServer.stubFor(WireMock.delete(WireMock.urlEqualTo("/user/1/history/101"))
                .willReturn(WireMock.aResponse().withStatus(204)));

        // ----------------- FAVOURITES STUBS -----------------
        String favouriteResponse = """
            { "id": 1, "userId": 1, "movieId": 101, "addedAt": "2026-04-05T17:10:00" }
            """;
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/user/1/favourites/101"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(favouriteResponse)));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/user/1/favourites"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[" + favouriteResponse + "]")));
        wireMockServer.stubFor(WireMock.delete(WireMock.urlEqualTo("/user/1/favourites/101"))
                .willReturn(WireMock.aResponse().withStatus(204)));

        // ----------------- RECOMMENDATIONS STUBS -----------------
        String movieDTOResponse = """
            { "id": 101, "adult": false, "original_language": "en", "original_title": "Original Movie", "title": "Original Movie",
              "genres": ["Action","Adventure"], "age_rating": "PG-13", "release_date": "2026-01-01", "tagline": "An epic adventure",
              "overview": "Test overview", "budget": 100000000, "revenue": 500000000, "runtime": 120, "poster_path": "/poster.jpg",
              "backdrop_path": "/backdrop.jpg", "status": "Released", "cast": [], "crew": [], "production_companies": ["Studio"],
              "images": [], "videos": [] }
            """;
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/user/1/recommendations"))
                .willReturn(WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[" + movieDTOResponse + "]")));
    }

    @AfterEach
    void teardown() {
        wireMockServer.resetAll();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    private WebTestClient client() {
        return WebTestClient.bindToServer().baseUrl("http://localhost:" + gatewayPort).build();
    }

    // ----------------- TESTS -----------------
    @Test void testRegister() {
        Map<String, Object> request = Map.of("username","testuser","email","test@example.com","password","pass123","over18",true);
        Map<String, Object> response = client().post().uri("/api/user/register").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request).exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(response.get("username")).isEqualTo("testuser");
    }

    @Test void testLogin() {
        Map<String,Object> request = Map.of("identifier","testuser","password","pass123");
        Map<String,Object> response = client().post().uri("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request).exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(response.get("username")).isEqualTo("testuser");
    }

    @Test void testVerifyEmail() {
        String result = client().post().uri(uri -> uri.path("/api/user/1/verify").queryParam("code","123456").build())
                .exchange().expectStatus().isOk().expectBody(String.class).returnResult().getResponseBody();
        assertThat(result).isEqualTo("Email verified successfully");
    }

    @Test void testGetUsers() {
        List<Map> users = client().get().uri("/api/user").exchange().expectStatus().isOk().expectBodyList(Map.class).returnResult().getResponseBody();
        assertThat(users).hasSize(1);
    }

    @Test void testGetUserById() {
        Map user = client().get().uri("/api/user/1").exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(user.get("id")).isEqualTo(1);
    }

    @Test void testUpdateUser() {
        Map<String,Object> request = Map.of("username","updateduser", "email", "updated@example.com", "password", "newpass123", "dob", "2000-01-01", "over18", true, "likedGenres", List.of("Action", "Drama"));
        Map<String,Object> response = client().put().uri("/api/user/1").contentType(MediaType.APPLICATION_JSON).bodyValue(request)
                .exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(response.get("username")).isEqualTo("updateduser");
    }

    @Test void testDeleteUser() {
        client().delete().uri("/api/user/1").exchange().expectStatus().isNoContent();
    }

    @Test void testPromoteDemoteModerator() {
        Map<String,Object> request = Map.of("adminIdentifier","admin1");
        Map promote = client().post().uri("/api/user/1/promote-moderator").contentType(MediaType.APPLICATION_JSON).bodyValue(request)
                .exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(promote.get("username")).isEqualTo("testuser");

        Map demote = client().post().uri("/api/user/1/demote-moderator").contentType(MediaType.APPLICATION_JSON).bodyValue(request)
                .exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(demote.get("username")).isEqualTo("testuser");
    }

    @Test void testWatchlistEndpoints() {
        Map<String,Object> add = client().post().uri("/api/user/1/watchlist/101").exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(add.get("movieId")).isEqualTo(101);

        List<Map> list = client().get().uri("/api/user/1/watchlist").exchange().expectStatus().isOk().expectBodyList(Map.class).returnResult().getResponseBody();
        assertThat(list).hasSize(1);

        Boolean inList = client().get().uri("/api/user/1/watchlist/101").exchange().expectStatus().isOk().expectBody(Boolean.class).returnResult().getResponseBody();
        assertThat(inList).isTrue();

        client().delete().uri("/api/user/1/watchlist/101").exchange().expectStatus().isNoContent();
    }

    @Test void testHistoryEndpoints() {
        Map add = client().post().uri("/api/user/1/history/101").exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(add.get("movieId")).isEqualTo(101);

        List<Map> list = client().get().uri("/api/user/1/history").exchange().expectStatus().isOk().expectBodyList(Map.class).returnResult().getResponseBody();
        assertThat(list).hasSize(1);

        client().delete().uri("/api/user/1/history/101").exchange().expectStatus().isNoContent();
    }

    @Test void testFavouritesEndpoints() {
        Map add = client().post().uri("/api/user/1/favourites/101").exchange().expectStatus().isOk().expectBody(Map.class).returnResult().getResponseBody();
        assertThat(add.get("movieId")).isEqualTo(101);

        List<Map> list = client().get().uri("/api/user/1/favourites").exchange().expectStatus().isOk().expectBodyList(Map.class).returnResult().getResponseBody();
        assertThat(list).hasSize(1);

        client().delete().uri("/api/user/1/favourites/101").exchange().expectStatus().isNoContent();
    }

    @Test void testRecommendations() {
        List<Map> recs = client().get().uri("/api/user/1/recommendations").exchange().expectStatus().isOk().expectBodyList(Map.class).returnResult().getResponseBody();
        assertThat(recs).hasSize(1);
        assertThat(recs.get(0).get("id")).isEqualTo(101);
    }
}