package ca.yorku.eecs4314group12.ui.data.dto;

import java.util.List;

/**
 * Matches movie-service's MovieDTO returned by list endpoints:
 *   GET /movie/trending
 *   GET /movie/nowplaying
 *   GET /movie/search/{name}
 *
 * Same flat structure as MovieDTO — now includes poster_path and profile_path.
 */
public class MovieListItemDTO {

    private int id;
    private String title;
    private String original_title;
    private String original_language;
    private String tagline;
    private String overview;
    private String poster_path;
    private String release_date;
    private String age_rating;
    private String status;
    private int runtime;
    private int budget;
    private int revenue;
    private boolean adult;
    private List<String> genres;
    private List<String> production_companies;
    private List<ActorItemDTO> cast;
    private List<CrewItemDTO> crew;

    // ---- Nested types ----

    public static class ActorItemDTO {
        private String original_name;
        private String name;
        private String character;
        private String profile_path;

        public String getOriginal_name() { return original_name; }
        public void setOriginal_name(String v) { this.original_name = v; }
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getCharacter() { return character; }
        public void setCharacter(String v) { this.character = v; }
        public String getProfile_path() { return profile_path; }
        public void setProfile_path(String v) { this.profile_path = v; }
    }

    public static class CrewItemDTO {
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

    // ---- Getters & setters ----

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getOriginal_title() { return original_title; }
    public void setOriginal_title(String v) { this.original_title = v; }
    public String getOriginal_language() { return original_language; }
    public void setOriginal_language(String v) { this.original_language = v; }
    public String getTagline() { return tagline; }
    public void setTagline(String v) { this.tagline = v; }
    public String getOverview() { return overview; }
    public void setOverview(String v) { this.overview = v; }
    public String getPoster_path() { return poster_path; }
    public void setPoster_path(String v) { this.poster_path = v; }
    public String getRelease_date() { return release_date; }
    public void setRelease_date(String v) { this.release_date = v; }
    public String getAge_rating() { return age_rating; }
    public void setAge_rating(String v) { this.age_rating = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public int getRuntime() { return runtime; }
    public void setRuntime(int v) { this.runtime = v; }
    public int getBudget() { return budget; }
    public void setBudget(int v) { this.budget = v; }
    public int getRevenue() { return revenue; }
    public void setRevenue(int v) { this.revenue = v; }
    public boolean isAdult() { return adult; }
    public void setAdult(boolean v) { this.adult = v; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> v) { this.genres = v; }
    public List<String> getProduction_companies() { return production_companies; }
    public void setProduction_companies(List<String> v) { this.production_companies = v; }
    public List<ActorItemDTO> getCast() { return cast; }
    public void setCast(List<ActorItemDTO> v) { this.cast = v; }
    public List<CrewItemDTO> getCrew() { return crew; }
    public void setCrew(List<CrewItemDTO> v) { this.crew = v; }

    // ---- Convenience helpers ----

    public String getYear() {
        return release_date != null && release_date.length() >= 4
                ? release_date.substring(0, 4) : "";
    }

    public String getDirector() {
        if (crew == null) return "Unknown";
        return crew.stream()
                .filter(c -> "Director".equals(c.getJob()))
                .map(CrewItemDTO::getOriginal_name)
                .findFirst()
                .orElse("Unknown");
    }
}