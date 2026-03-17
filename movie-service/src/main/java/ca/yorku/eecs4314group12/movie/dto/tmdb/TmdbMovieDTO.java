package ca.yorku.eecs4314group12.movie.dto.tmdb;

import java.util.List;

public class TmdbMovieDTO {
	
	private int id;
	private boolean adult;
	private String original_language;
	private String original_title;
	private String title;
	private String tagline;
	private String overview;
	private String release_date;
	private List<TmdbGenreDTO> genres;
	private List<Integer> genre_ids;
	private int budget;
	private int revenue;
	private int runtime;
	private String poster_path;
	private String status;
	private TmdbReleaseDatesDTO release_dates;
	private TmdbCreditsDTO credits;
	private List<TmdbCompanyDTO> production_companies;
	
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
	
	public String getRelease_date() {
		return release_date;
	}
	
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
	
	public List<TmdbGenreDTO> getGenres() {
		return genres;
	}

	public void setGenres(List<TmdbGenreDTO> genres) {
		this.genres = genres;
	}
	
	public List<Integer> getGenre_ids() {
		return genre_ids;
	}

	public void setGenre_ids(List<Integer> genre_ids) {
		this.genre_ids = genre_ids;
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
	
	public TmdbReleaseDatesDTO getRelease_dates() {
		return release_dates;
	}

	public void setRelease_dates(TmdbReleaseDatesDTO release_dates) {
		this.release_dates = release_dates;
	}
	
	public TmdbCreditsDTO getCredits() {
		return credits;
	}
	
	public void setCredits(TmdbCreditsDTO credits) {
		this.credits = credits;
	}

	public List<TmdbCompanyDTO> getProduction_companies() {
		return production_companies;
	}

	public void setProduction_companies(List<TmdbCompanyDTO> production_companies) {
		this.production_companies = production_companies;
	}
}
