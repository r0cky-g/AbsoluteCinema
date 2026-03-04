package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.ArrayList;

public class TmdbCreditsDTO {
	
	ArrayList<TmdbCreditsActorDTO> cast;
	ArrayList<TmdbCreditsCrewDTO> crew;
	
	public ArrayList<TmdbCreditsActorDTO> getCast() {
		return cast;
	}
	
	public void setCast(ArrayList<TmdbCreditsActorDTO> cast) {
		this.cast = cast;
	}
	
	public ArrayList<TmdbCreditsCrewDTO> getCrew() {
		return crew;
	}
	
	public void setCrew(ArrayList<TmdbCreditsCrewDTO> crew) {
		this.crew = crew;
	}
}
