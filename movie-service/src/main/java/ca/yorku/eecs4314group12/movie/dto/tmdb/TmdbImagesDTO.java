package ca.yorku.eecs4314group12.movie.dto.tmdb;

import java.util.List;

public class TmdbImagesDTO {
	
	private List<TmdbImageDTO> backdrops;

	public List<TmdbImageDTO> getBackdrops() {
		return backdrops;
	}

	public void setBackdrops(List<TmdbImageDTO> backdrops) {
		this.backdrops = backdrops;
	}
}
