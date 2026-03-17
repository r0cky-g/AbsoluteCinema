package ca.yorku.eecs4314group12.movie.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ca.yorku.eecs4314group12.movie.dto.*;
import ca.yorku.eecs4314group12.movie.service.MovieService;

@RestController
@RequestMapping("/movie")
public class MovieController {
	
	private MovieService movieService;
	
	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}
	
	@GetMapping("/{id}")
	public MovieDTO getDetails(@PathVariable int id) {
		return movieService.getDetails(id);
	}
	
	@GetMapping("/search/{name}")
	public MovieSearchDTO getDetails(@PathVariable String name) {
		return movieService.getSearch(name);
	}
	
	@GetMapping("/trending")
	public MoviesTrendingDTO getTrending() {
		return movieService.getTrending();
	}
	
	@GetMapping("/nowplaying")
	public MoviesNowPlayingDTO getNowPlaying() {
		return movieService.getNowPlaying();
	}
}
