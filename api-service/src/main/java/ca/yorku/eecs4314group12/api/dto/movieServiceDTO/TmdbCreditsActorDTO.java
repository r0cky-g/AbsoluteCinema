package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

public class TmdbCreditsActorDTO {
	
	private String original_name;
	private String character;
	private String profile_path;

	public String getOriginal_name() {
		return original_name;
	}
	
	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}
	
	public String getCharacter() {
		return character;
	}
	
	public void setCharacter(String character) {
		this.character = character;
	}

	public String getProfile_path() {
		return profile_path;
	}

	public void setProfile_path(String profile_path) {
		this.profile_path = profile_path;
	}
}
