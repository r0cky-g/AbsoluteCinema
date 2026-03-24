package ca.yorku.eecs4314group12.movie.dto.tmdb;

public class TmdbVideoDTO {
	
	private String name;
	private String key;
	private String site;
	private boolean official;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getSite() {
		return site;
	}
	
	public void setSite(String site) {
		this.site = site;
	}

	public boolean isOfficial() {
		return official;
	}

	public void setOfficial(boolean official) {
		this.official = official;
	}
}
