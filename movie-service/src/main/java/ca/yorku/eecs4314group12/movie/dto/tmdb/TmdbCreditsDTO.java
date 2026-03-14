package ca.yorku.eecs4314group12.movie.dto.tmdb;

import java.util.List;

public class TmdbCreditsDTO {
	
	List<TmdbCreditsActorDTO> cast;
	List<TmdbCreditsCrewDTO> crew;
	
	public List<TmdbCreditsActorDTO> getCast() {
		return cast;
	}
	
	public void setCast(List<TmdbCreditsActorDTO> cast) {
		this.cast = cast;
	}
	
	public List<TmdbCreditsCrewDTO> getCrew() {
		return crew;
	}
	
	public void setCrew(List<TmdbCreditsCrewDTO> crew) {
		this.crew = crew;
	}
}
