package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.List;

public class MovieSearchDTO {
	
	private List<MovieDTO> results;

	public List<MovieDTO> getResults() {
		return results;
	}

	public void setResults(List<MovieDTO> results) {
		this.results = results;
	}
}
