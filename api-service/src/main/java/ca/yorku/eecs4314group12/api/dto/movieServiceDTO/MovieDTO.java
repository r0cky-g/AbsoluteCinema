package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.List;

public class MovieDTO {
	
	private int id;
	private boolean adult;
	private String original_language;
	private String original_title;
	private String title;
	private List<String> genres;
	private String age_rating;
	private String release_date;
	private String tagline;
	private String overview;
	private int budget;
	private int revenue;
	private int runtime;
	private String backdrop_path;
	private String poster_path;
	private String status;
	private List<String> images;
	private List<String> videos;
	private List<ActorDTO> cast;
	private List<CrewMemberDTO> crew;
	private List<String> production_companies;
	
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
	
	public List<String> getGenres() {
		return genres;
	}
	
	public void setGenres(List<String> genres) {
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
	
	public String getBackdrop_path() {
		return backdrop_path;
	}

	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public List<String> getVideos() {
		return videos;
	}

	public void setVideos(List<String> videos) {
		this.videos = videos;
	}

	public List<ActorDTO> getCast() {
		return cast;
	}
	
	public void setCast(List<ActorDTO> cast) {
		this.cast = cast;
	}
	
	public List<CrewMemberDTO> getCrew() {
		return crew;
	}
	
	public void setCrew(List<CrewMemberDTO> crew) {
		this.crew = crew;
	}
	
	public List<String> getProduction_companies() {
		return production_companies;
	}
	
	public void setProduction_companies(List<String> production_companies) {
		this.production_companies = production_companies;
	}	
}