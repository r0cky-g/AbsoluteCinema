package ca.yorku.eecs4314group12.api.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.TmdbMovieDTO;
import reactor.core.publisher.Mono;

@Component
public class MovieClient {

    private final WebClient webClient;

    public MovieClient(@Qualifier("APIMovieClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<TmdbMovieDTO> getMovieById(int id) {
        return webClient.get()
                .uri("/" + id)
                .retrieve()
                .bodyToMono(TmdbMovieDTO.class);
    }
}
