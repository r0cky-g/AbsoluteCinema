package ca.yorku.eecs4314group12.api.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.*;
import reactor.core.publisher.Mono;

@Component
public class MovieClient {

    private final BaseWebClient baseWebClient;

    public MovieClient(@Qualifier("APIMovieClient") WebClient webClient) {
        this.baseWebClient = new BaseWebClient(webClient);
    }

    public Mono<TmdbMovieDTO> getMovieById(int id) {
        return baseWebClient.get("/movie/{id}", new ParameterizedTypeReference<TmdbMovieDTO>() {}, id);
    }
}
