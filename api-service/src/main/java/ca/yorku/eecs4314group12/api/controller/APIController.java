package ca.yorku.eecs4314group12.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.TmdbMovieDTO;
import ca.yorku.eecs4314group12.api.service.MovieService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class APIController {

    private final MovieService movieService;
    // private final ReviewClient reviewClient;
    // private final UserClient userClient;

    public APIController(MovieService movieService) {
        this.movieService = movieService;
        // this.userClient = userClient;
        // this.reviewClient = reviewClient;
    }

    @GetMapping("/movie/{id}")
    public Mono<TmdbMovieDTO> getMovieDetails(@PathVariable int id) {
        return movieService.getMovieById(id);
    }
}