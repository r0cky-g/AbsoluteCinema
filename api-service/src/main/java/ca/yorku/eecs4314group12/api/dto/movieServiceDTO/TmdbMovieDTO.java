package ca.yorku.eecs4314group12.api.dto.movieServiceDTO;

import java.util.List;

/**
 * Mirrors movie-service's MovieDTO exactly.
 * Updated to match the new flat structure after MongoDB caching was added.
 */
public class TmdbMovieDTO {

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
    private String status;
    private List<ActorDTO> cast;
    private List<CrewMemberDTO> crew;
    private List<String> production_companies;

    public static class ActorDTO {
        private String original_name;
        private String name;
        private String character;

        public String getOriginal_name() { return original_name; }
        public void setOriginal_name(String v) { this.original_name = v; }
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getCharacter() { return character; }
        public void setCharacter(String v) { this.character = v; }
    }

    public static class CrewMemberDTO {
        private String original_name;
        private String name;
        private String department;
        private String job;

        public String getOriginal_name() { return original_name; }
        public void setOriginal_name(String v) { this.original_name = v; }
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getDepartment() { return department; }
        public void setDepartment(String v) { this.department = v; }
        public String getJob() { return job; }
        public void setJob(String v) { this.job = v; }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public boolean isAdult() { return adult; }
    public void setAdult(boolean adult) { this.adult = adult; }
    public String getOriginal_language() { return original_language; }
    public void setOriginal_language(String v) { this.original_language = v; }
    public String getOriginal_title() { return original_title; }
    public void setOriginal_title(String v) { this.original_title = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> v) { this.genres = v; }
    public String getAge_rating() { return age_rating; }
    public void setAge_rating(String v) { this.age_rating = v; }
    public String getRelease_date() { return release_date; }
    public void setRelease_date(String v) { this.release_date = v; }
    public String getTagline() { return tagline; }
    public void setTagline(String v) { this.tagline = v; }
    public String getOverview() { return overview; }
    public void setOverview(String v) { this.overview = v; }
    public int getBudget() { return budget; }
    public void setBudget(int v) { this.budget = v; }
    public int getRevenue() { return revenue; }
    public void setRevenue(int v) { this.revenue = v; }
    public int getRuntime() { return runtime; }
    public void setRuntime(int v) { this.runtime = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public List<ActorDTO> getCast() { return cast; }
    public void setCast(List<ActorDTO> v) { this.cast = v; }
    public List<CrewMemberDTO> getCrew() { return crew; }
    public void setCrew(List<CrewMemberDTO> v) { this.crew = v; }
    public List<String> getProduction_companies() { return production_companies; }
    public void setProduction_companies(List<String> v) { this.production_companies = v; }
}