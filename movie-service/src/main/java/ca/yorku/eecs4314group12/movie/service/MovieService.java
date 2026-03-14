package ca.yorku.eecs4314group12.movie.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ca.yorku.eecs4314group12.movie.client.*;
import ca.yorku.eecs4314group12.movie.dto.*;
import ca.yorku.eecs4314group12.movie.exception.*;
import ca.yorku.eecs4314group12.movie.mapper.MovieMapper;
import ca.yorku.eecs4314group12.movie.repository.MovieRepository;
import ca.yorku.eecs4314group12.movie.document.Movie;

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
				.orElseGet(() -> callTmdbForDetails(id));
		saveMovieInCache(movie);
		return movMap.toMovieDTO(movie);
	}
	
	public MoviesTrendingDTO getTrending() {
		return callTmdbForTrending();
	}
	
	public MoviesNowPlayingDTO getNowPlaying() {
		return callTmdbForNowPlaying();
	}
	
	private void saveMovieInCache(Movie movie) {
		movRepo.save(movie);
	}
	
	private Movie callTmdbForDetails(int id) {
		try {
			TmdbMovieDTO movieData = tmdbClient.getMovieDetails(id);
			Movie movie = movMap.toMovie(movieData);
			return movie;
		} catch(WebClientResponseException.NotFound e) {
			throw new MovieNotFoundException(id);
		}
	}
	
	private MoviesTrendingDTO callTmdbForTrending() {
		TmdbMoviesTrendingDTO trending = tmdbClient.getMoviesTrending();
		return movMap.toMoviesTrendingDTO(trending);
	}
	
	private MoviesNowPlayingDTO callTmdbForNowPlaying() {
		TmdbMoviesNowPlayingDTO nowPlaying = tmdbClient.getMoviesNowPlaying();
		return movMap.toMoviesNowPlayingDTO(nowPlaying);
	}
}
