package ca.yorku.eecs4314group12.movie.service;

import ca.yorku.eecs4314group12.movie.client.TmdbClient;
import ca.yorku.eecs4314group12.movie.document.Movie;
import ca.yorku.eecs4314group12.movie.dto.MovieDTO;
import ca.yorku.eecs4314group12.movie.dto.MoviesTrendingDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbMoviesTrendingDTO;
import ca.yorku.eecs4314group12.movie.exception.MovieNotFoundException;
import ca.yorku.eecs4314group12.movie.mapper.MovieMapper;
import ca.yorku.eecs4314group12.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Service
public class MovieService {
	
	private final TmdbClient tmdbClient;
	private final MovieRepository movRepo;
	private final MovieMapper movMap;
	
	public MovieService(TmdbClient tmdbClient, MovieRepository movRepo, MovieMapper movMap) {
		this.tmdbClient = tmdbClient;
		this.movRepo = movRepo;
		this.movMap = movMap;
	}
	
	public MovieDTO getDetails(int id) {
		Movie movie = movRepo.findById(id)
				.filter(m -> !m.getOverview().isEmpty())
				.orElseGet(() -> callTmdbForMovieDetailsAndSave(id));
		
		return movMap.toMovieDTO(movie);
	}
	
	public MoviesTrendingDTO getTrending() {
		return callTmdbForTrending();
	}
	
	private Movie callTmdbForMovieDetailsAndSave(int id) {
		try {
			TmdbMovieDTO movieData = tmdbClient.getMovieDetails(id);
			Movie movie = movMap.toMovie(movieData);
			movRepo.save(movie);
			return movie;
		} catch(WebClientResponseException.NotFound e) {
			throw new MovieNotFoundException(id);
		}
	}
	
	private MoviesTrendingDTO callTmdbForTrending() {
		TmdbMoviesTrendingDTO trending = tmdbClient.getMoviesTrending();
		return movMap.toMoviesTrendingDTO(trending);
	}
}
