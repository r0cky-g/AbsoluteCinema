package ca.yorku.eecs4314group12.api.service;

import org.springframework.stereotype.Service;

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
        return movieClient.getMovieById(id);
    }
}
