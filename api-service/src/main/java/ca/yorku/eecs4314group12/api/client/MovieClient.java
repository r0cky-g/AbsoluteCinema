package ca.yorku.eecs4314group12.api.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieSearchDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesNowPlayingDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesTrendingDTO;
import reactor.core.publisher.Mono;

@Component
public class MovieClient {

    private final BaseWebClient baseWebClient;

    public MovieClient(@Qualifier("APIMovieClient") WebClient webClient) {
        this.baseWebClient = new BaseWebClient(webClient);
    }

    public Mono<ResponseEntity<MovieDTO>> getDetails(int id) {
        return baseWebClient.get("/movie/{id}", new ParameterizedTypeReference<MovieDTO>() {}, id);
    }

    public Mono<ResponseEntity<MovieSearchDTO>> getDetails(String name) {
        return baseWebClient.get("/movie/search/{name}", new ParameterizedTypeReference<MovieSearchDTO>() {}, name);
    }

    public Mono<ResponseEntity<MoviesTrendingDTO>> getTrending() {
        return baseWebClient.get("/movie/trending", new ParameterizedTypeReference<MoviesTrendingDTO>() {});
    }

    public Mono<ResponseEntity<MoviesNowPlayingDTO>> getNowPlaying() {
        return baseWebClient.get("/movie/nowplaying", new ParameterizedTypeReference<MoviesNowPlayingDTO>() {});
    }
}