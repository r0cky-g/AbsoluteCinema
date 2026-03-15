package ca.yorku.eecs4314group12.user.dto;

import java.util.List;

public class MovieDTO {
    private int id;
    private String title;
    private List<String> genres;
    private String release_date;
    private String overview;
    private String poster_path;
    private List<ActorDTO> cast;
    private List<CrewMemberDTO> crew;
    private List<String> production_companies;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
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
