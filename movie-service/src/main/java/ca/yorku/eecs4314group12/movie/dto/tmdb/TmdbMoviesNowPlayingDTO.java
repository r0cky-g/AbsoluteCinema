package ca.yorku.eecs4314group12.movie.dto.tmdb;

import java.util.List;

public class TmdbMoviesNowPlayingDTO {
	
	private List<TmdbMovieDTO> results;

	public List<TmdbMovieDTO> getResults() {
		return results;
	}

	public void setResults(List<TmdbMovieDTO> results) {
		this.results = results;
	}
}
