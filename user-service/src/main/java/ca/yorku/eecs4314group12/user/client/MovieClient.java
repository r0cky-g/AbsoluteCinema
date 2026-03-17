package ca.yorku.eecs4314group12.user.client;

import ca.yorku.eecs4314group12.user.dto.MovieDTO;
import ca.yorku.eecs4314group12.user.dto.MoviesTrendingDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Component
public class MovieClient {

    private final WebClient webClient;

    public MovieClient(@Qualifier("movieServiceClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<MovieDTO> getMovieById(int id) {
        try {
            MovieDTO movie = webClient.get()
                    .uri("/movie/{id}", id)
                    .retrieve()
                    .bodyToMono(MovieDTO.class)
                    .block();
            return Optional.ofNullable(movie);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<MoviesTrendingDTO> getTrendingMovies() {
        try {
            MoviesTrendingDTO trending = webClient.get()
                    .uri("/movie/trending")
                    .retrieve()
                    .bodyToMono(MoviesTrendingDTO.class)
                    .block();
            return Optional.ofNullable(trending);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
