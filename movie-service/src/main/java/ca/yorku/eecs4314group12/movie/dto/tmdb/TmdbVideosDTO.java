package ca.yorku.eecs4314group12.movie.dto.tmdb;

import java.util.List;

public class TmdbVideosDTO {
	
	List<TmdbVideoDTO> results;

	public List<TmdbVideoDTO> getResults() {
		return results;
	}

	public void setResults(List<TmdbVideoDTO> results) {
		this.results = results;
	}
}
