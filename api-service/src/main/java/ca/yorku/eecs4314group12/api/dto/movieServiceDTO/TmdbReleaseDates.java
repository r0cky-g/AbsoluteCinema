package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.ArrayList;

public class TmdbReleaseDates {
	
	private ArrayList<TmdbRegionDTO> results;

	public ArrayList<TmdbRegionDTO> getResults() {
		return results;
	}

	public void setResults(ArrayList<TmdbRegionDTO> results) {
		this.results = results;
	}
}
