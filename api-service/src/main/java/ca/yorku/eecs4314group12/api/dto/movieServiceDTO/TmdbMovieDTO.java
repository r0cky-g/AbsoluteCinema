package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.ArrayList;

public class TmdbMovieDTO {
	
	private int id;
	private boolean adult;
	private String original_language;
	private String original_title;
	private String title;
	private String tagline;
	private String overview;
	private String poster_path;
	private String release_date;
	private ArrayList<TmdbGenreDTO> genres;
	private int budget;
	private int revenue;
	private int runtime;
	private String status;
	private TmdbReleaseDates release_dates;
	private TmdbImagesDTO images;
	private TmdbVideosDTO videos;
	private TmdbCreditsDTO credits;
	private ArrayList<TmdbCompanyDTO> production_companies;
	
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
	
	public String getPoster_path() {
		return poster_path;
	}
	
	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}
	
	public String getRelease_date() {
		return release_date;
	}
	
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
	
	public ArrayList<TmdbGenreDTO> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<TmdbGenreDTO> genres) {
		this.genres = genres;
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
	
	public TmdbReleaseDates getRelease_dates() {
		return release_dates;
	}

	public void setRelease_dates(TmdbReleaseDates release_dates) {
		this.release_dates = release_dates;
	}

	public TmdbImagesDTO getImages() {
		return images;
	}
	
	public void setImages(TmdbImagesDTO images) {
		this.images = images;
	}
	
	public TmdbVideosDTO getVideos() {
		return videos;
	}
	
	public void setVideos(TmdbVideosDTO videos) {
		this.videos = videos;
	}
	
	public TmdbCreditsDTO getCredits() {
		return credits;
	}
	
	public void setCredits(TmdbCreditsDTO credits) {
		this.credits = credits;
	}

	public ArrayList<TmdbCompanyDTO> getProduction_companies() {
		return production_companies;
	}

	public void setProduction_companies(ArrayList<TmdbCompanyDTO> production_companies) {
		this.production_companies = production_companies;
	}
}
