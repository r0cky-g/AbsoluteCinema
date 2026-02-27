package ca.yorku.eecs4314group12.movie.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;
import ca.yorku.eecs4314group12.movie.service.MovieService;

@RestController
@RequestMapping("/movie")
public class MovieController {
	
	private MovieService movieService;
	
	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}
	
	@GetMapping("/{id}")
	public TmdbMovieDTO getMovieDetails(@PathVariable int id) {
		return movieService.getDetails(id);
	}

}
