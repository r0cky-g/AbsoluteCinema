package ca.yorku.eecs4314group12.movie.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ca.yorku.eecs4314group12.movie.client.*;
import ca.yorku.eecs4314group12.movie.dto.*;
import ca.yorku.eecs4314group12.movie.dto.tmdb.*;
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
				.filter(m -> m.getOverview() != null && !m.getOverview().isEmpty())
				.orElseGet(() -> callTmdbForDetailsAndSaveMovie(id));
		return movMap.toMovieDTO(movie);
	}
	
	public MovieSearchDTO getSearch(String name) {
		List<Movie> results = movRepo.findByTitle(name);
		
		if(results.size() < 20) {
			return callTmdbForSearchAndSaveResults(name);
		}
		
		return movMap.toMovieSearchDTO(results);
	}
	
	public MoviesTrendingDTO getTrending() {
		return callTmdbForTrending();
	}
	
	public MoviesNowPlayingDTO getNowPlaying() {
		return callTmdbForNowPlaying();
	}
	
	private Movie callTmdbForDetailsAndSaveMovie(int id) {
		try {
			TmdbMovieDTO movieData = tmdbClient.getMovieDetails(id);
			Movie movie = movMap.toMovie(movieData);
			movRepo.save(movie);
			return movie;
		} catch(WebClientResponseException.NotFound e) {
			throw new MovieNotFoundException(id);
		}
	}
	
	private MovieSearchDTO callTmdbForSearchAndSaveResults(String name) {
		TmdbMovieSearchDTO tmdbSearchDTO = tmdbClient.getSearch(name);
		MovieSearchDTO searchDTO = movMap.toMovieSearchDTO(tmdbSearchDTO);
		saveMovieFromSearchResults(searchDTO);
		return searchDTO;
	}
	
	private MoviesTrendingDTO callTmdbForTrending() {
		TmdbMoviesTrendingDTO trendingData = tmdbClient.getMoviesTrending();
		return movMap.toMoviesTrendingDTO(trendingData);
	}
	
	private MoviesNowPlayingDTO callTmdbForNowPlaying() {
		TmdbMoviesNowPlayingDTO nowPlayingData = tmdbClient.getMoviesNowPlaying();
		return movMap.toMoviesNowPlayingDTO(nowPlayingData);
	}
	
	private void saveMovieFromSearchResults(MovieSearchDTO searchDTO) {
		for(MovieDTO movieDTO : searchDTO.getResults()) {
			if(!movRepo.existsById(movieDTO.getId())) {
				Movie movie = movMap.toMovie(movieDTO);
				movRepo.save(movie);
			}
		}
	}
}
