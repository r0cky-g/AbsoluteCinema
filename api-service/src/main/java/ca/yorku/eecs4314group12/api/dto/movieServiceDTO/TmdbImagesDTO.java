package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.ArrayList;

public class TmdbImagesDTO {
	
	private ArrayList<TmdbImageDTO> backdrops;

	public ArrayList<TmdbImageDTO> getBackdrops() {
		return backdrops;
	}

	public void setBackdrops(ArrayList<TmdbImageDTO> backdrops) {
		this.backdrops = backdrops;
	}
}
