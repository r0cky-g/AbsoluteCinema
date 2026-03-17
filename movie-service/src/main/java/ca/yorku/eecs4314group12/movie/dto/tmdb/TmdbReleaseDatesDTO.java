package ca.yorku.eecs4314group12.movie.dto.tmdb;

import java.util.List;

public class TmdbReleaseDatesDTO {
	
	private List<TmdbRegionDTO> results;

	public List<TmdbRegionDTO> getResults() {
		return results;
	}

	public void setResults(List<TmdbRegionDTO> results) {
		this.results = results;
	}
}
