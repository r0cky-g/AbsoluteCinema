package ca.yorku.eecs4314group12.api.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.TmdbMovieDTO;
import reactor.core.publisher.Mono;

@Component
public class MovieClient {

    private final WebClient movieClient;

    public MovieClient(@Qualifier("APIMovieClient") WebClient movieClient) {
        this.movieClient = movieClient;
    }

    public Mono<TmdbMovieDTO> getMovieById(int id) {
        return movieClient.get()
                .uri("/" + id)
                .retrieve()
                .bodyToMono(TmdbMovieDTO.class);
    }
}
