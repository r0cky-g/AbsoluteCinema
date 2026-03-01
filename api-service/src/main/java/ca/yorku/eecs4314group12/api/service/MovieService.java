package ca.yorku.eecs4314group12.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ca.yorku.eecs4314group12.api.client.MovieClient;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.TmdbMovieDTO;
import reactor.core.publisher.Mono;

@Service
public class MovieService {
    
    private final MovieClient movieClient;

    public MovieService(MovieClient movieClient) {
        this.movieClient = movieClient;
    }

    public Mono<TmdbMovieDTO> getMovieById(int id) {
        return movieClient.getMovieById(id)
            .switchIfEmpty(Mono.error(
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "movie not found")));
    }
}
