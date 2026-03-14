package ca.yorku.eecs4314group12.movie.dto;

import java.util.List;

public class TmdbRegionDTO {
	
	private String iso_3166_1;
	private List<TmdbCertificateDTO> release_dates;
	
	public String getIso_3166_1() {
		return iso_3166_1;
	}
	
	public void setIso_3166_1(String iso_3166_1) {
		this.iso_3166_1 = iso_3166_1;
	}
	
	public List<TmdbCertificateDTO> getRelease_dates() {
		return release_dates;
	}
	
	public void setRelease_dates(List<TmdbCertificateDTO> release_dates) {
		this.release_dates = release_dates;
	}
}
