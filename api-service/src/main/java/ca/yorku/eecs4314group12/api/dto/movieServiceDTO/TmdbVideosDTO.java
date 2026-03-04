package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.ArrayList;

public class TmdbVideosDTO {
	
	ArrayList<TmdbVideoDTO> results;

	public ArrayList<TmdbVideoDTO> getResults() {
		return results;
	}

	public void setResults(ArrayList<TmdbVideoDTO> results) {
		this.results = results;
	}
}
