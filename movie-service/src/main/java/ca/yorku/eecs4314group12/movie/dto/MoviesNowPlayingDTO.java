package ca.yorku.eecs4314group12.movie.dto;

import java.util.List;

public class MoviesNowPlayingDTO {
	
	private List<MovieDTO> results;

	public List<MovieDTO> getResults() {
		return results;
	}

	public void setResults(List<MovieDTO> results) {
		this.results = results;
	}
}
