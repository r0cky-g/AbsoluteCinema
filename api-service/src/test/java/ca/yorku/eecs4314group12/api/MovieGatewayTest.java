package ca.yorku.eecs4314group12.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesNowPlayingDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesTrendingDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieGatewayTest {

    @LocalServerPort
    int port;

    private WebClient gateway;

    @BeforeEach
    void setup() {
        gateway = WebClient.create("http://localhost:" + port + "/api");
    }

    @Test
    void testMovieGetById() {
        int id1 = (int)( Math.random()*1000);
        int id2 = (int)( Math.random()*1000);

        MovieDTO movie1 = gateway.get()
            .uri("/movie/{id1}", id1)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<MovieDTO>() {})
            .block();

        MovieDTO movie2 = gateway.get()
            .uri("/movie/{id2}", id2)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<MovieDTO>() {})
            .block();

        assertTrue(movie1 != null || movie2 != null, "Both movies are null, expected at least one to exist");
    }

    @Test
    void testMovieGetByName() {
        String name = "alien";

        MovieDTO movie = gateway.get()
            .uri("/movie/search/{name}", name)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<MovieDTO>() {})
            .block();

        assertNotNull(movie, "No alien movie");
    }

    @Test
    void testMovieTrending() {
        MoviesTrendingDTO movies = gateway.get()
            .uri("/movie/trending")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<MoviesTrendingDTO>() {})
            .block();

        assertNotNull(movies, "Movie list should contain at least one entry");
    }

    @Test
    void TestMovieNowPlaying() {
        MoviesNowPlayingDTO movies = gateway.get()
            .uri("/movie/nowplaying")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<MoviesNowPlayingDTO>() {})
            .block();

        assertNotNull(movies, "Movie list should contain at least one entry");
    }
}