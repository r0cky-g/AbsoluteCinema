package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

public class TmdbVideoDTO {
	
	private String name;
	private String key;
	private String site;
	private String official;
	
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
	
	public String getOfficial() {
		return official;
	}
	
	public void setOfficial(String official) {
		this.official = official;
	}
}
