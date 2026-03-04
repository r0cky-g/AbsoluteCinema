package ca.yorku.eecs4314group12.movie.service;

import ca.yorku.eecs4314group12.movie.client.TmdbClient;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;
import org.springframework.stereotype.Service;


@Service
public class MovieService {
	
	private final TmdbClient tmdbClient;
	
	public MovieService(TmdbClient tmdbClient) {
		this.tmdbClient = tmdbClient;
	}
	
	public TmdbMovieDTO getDetails(int id) {
		return tmdbClient.getMovieDetails(id);
	}
}
