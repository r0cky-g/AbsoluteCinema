package ca.yorku.eecs4314group12.api.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieSearchDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesNowPlayingDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesTrendingDTO;
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
    public Mono<ResponseEntity<MovieDTO>> getMovieDetails(@PathVariable int id) {
        return movieService.getDetails(id);
    }

    @GetMapping("/movie/search/{name}")
    public Mono<ResponseEntity<MovieSearchDTO>> searchMovie(@PathVariable String name) {
        return movieService.getDetails(name);
    }

    @GetMapping("/movie/trending")
    public Mono<ResponseEntity<MoviesTrendingDTO>> getMovieTrending() {
        return movieService.getTrending();
    }

    @GetMapping("/movie/nowplaying")
    public Mono<ResponseEntity<MoviesNowPlayingDTO>> getMovieNowPlaying() {
        return movieService.getNowPlaying();
    }

    

    @GetMapping("/test")
    public ResponseEntity<String> testmethod() {
        ResponseEntity<String> response = new ResponseEntity<String>("gateway is working", HttpStatusCode.valueOf(200));
        return response;
    }
}