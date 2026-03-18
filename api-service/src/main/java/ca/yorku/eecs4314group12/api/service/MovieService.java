package ca.yorku.eecs4314group12.api.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ca.yorku.eecs4314group12.api.client.MovieClient;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieDTO;
import reactor.core.publisher.Mono;

@Service
public class MovieService {
    
    private final MovieClient movieClient;

    public MovieService(MovieClient movieClient) {
        this.movieClient = movieClient;
    }

    public Mono<ResponseEntity<MovieDTO>> getDetails(int id) {
        return movieClient.getDetails(id);
    }
}
