package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.ArrayList;

public class TmdbRegionDTO {
	
	private String iso_3166_1;
	private ArrayList<TmdbCertifcateDTO> release_dates;
	
	public String getIso_3166_1() {
		return iso_3166_1;
	}
	
	public void setIso_3166_1(String iso_3166_1) {
		this.iso_3166_1 = iso_3166_1;
	}
	
	public ArrayList<TmdbCertifcateDTO> getRelease_dates() {
		return release_dates;
	}
	
	public void setRelease_dates(ArrayList<TmdbCertifcateDTO> release_dates) {
		this.release_dates = release_dates;
	}
}
