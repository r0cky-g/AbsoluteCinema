package ca.yorku.eecs4314group12.movie.dto;

import java.util.ArrayList;

public class MovieDTO {
	private int id;
	private boolean adult;
	private String original_language;
	private String original_title;
	private String title;
	private ArrayList<String> genres;
	private String age_rating;
	private String release_date;
	private String tagline;
	private String overview;
	private int budget;
	private int revenue;
	private int runtime;
	private String status;
	private ArrayList<ActorDTO> cast;
	private ArrayList<CrewMemberDTO> crew;
	private ArrayList<String> production_companies;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isAdult() {
		return adult;
	}
	
	public void setAdult(boolean adult) {
		this.adult = adult;
	}
	
	public String getOriginal_language() {
		return original_language;
	}
	
	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}
	
	public String getOriginal_title() {
		return original_title;
	}
	
	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public ArrayList<String> getGenres() {
		return genres;
	}
	
	public void setGenres(ArrayList<String> genres) {
		this.genres = genres;
	}
	
	public String getAge_rating() {
		return age_rating;
	}
	
	public void setAge_rating(String age_rating) {
		this.age_rating = age_rating;
	}
	
	public String getRelease_date() {
		return release_date;
	}
	
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
	
	public String getTagline() {
		return tagline;
	}
	
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}
	
	public String getOverview() {
		return overview;
	}
	
	public void setOverview(String overview) {
		this.overview = overview;
	}
	
	public int getBudget() {
		return budget;
	}
	
	public void setBudget(int budget) {
		this.budget = budget;
	}
	
	public int getRevenue() {
		return revenue;
	}
	
	public void setRevenue(int revenue) {
		this.revenue = revenue;
	}
	
	public int getRuntime() {
		return runtime;
	}
	
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public ArrayList<ActorDTO> getCast() {
		return cast;
	}
	
	public void setCast(ArrayList<ActorDTO> cast) {
		this.cast = cast;
	}
	
	public ArrayList<CrewMemberDTO> getCrew() {
		return crew;
	}
	
	public void setCrew(ArrayList<CrewMemberDTO> crew) {
		this.crew = crew;
	}
	
	public ArrayList<String> getProduction_companies() {
		return production_companies;
	}
	
	public void setProduction_companies(ArrayList<String> production_companies) {
		this.production_companies = production_companies;
	}	
}
