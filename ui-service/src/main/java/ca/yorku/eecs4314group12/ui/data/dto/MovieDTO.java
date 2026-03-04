package ca.yorku.eecs4314group12.ui.data.dto;

import java.util.List;

/**
 * Mirrors the TmdbMovieDTO returned by api-service GET /api/movie/{id}.
 * Field names match the JSON keys TMDB returns (snake_case).
 */
public class MovieDTO {

    private int id;
    private String title;
    private String original_title;
    private String original_language;
    private String tagline;
    private String overview;
    private String poster_path;
    private String release_date;
    private String status;
    private int runtime;
    private double vote_average;
    private int vote_count;
    private int budget;
    private int revenue;
    private List<GenreDTO> genres;
    private CreditsDTO credits;
    private List<CompanyDTO> production_companies;

    // --- Nested types ---

    public static class GenreDTO {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class CompanyDTO {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class CreditsDTO {
        private List<CastMemberDTO> cast;
        private List<CrewMemberDTO> crew;
        public List<CastMemberDTO> getCast() { return cast; }
        public void setCast(List<CastMemberDTO> cast) { this.cast = cast; }
        public List<CrewMemberDTO> getCrew() { return crew; }
        public void setCrew(List<CrewMemberDTO> crew) { this.crew = crew; }
    }

    public static class CastMemberDTO {
        private String original_name;
        private String character;
        private String profile_path;
        public String getOriginal_name() { return original_name; }
        public void setOriginal_name(String n) { this.original_name = n; }
        public String getCharacter() { return character; }
        public void setCharacter(String c) { this.character = c; }
        public String getProfile_path() { return profile_path; }
        public void setProfile_path(String p) { this.profile_path = p; }
    }

    public static class CrewMemberDTO {
        private String original_name;
        private String job;
        private String department;
        public String getOriginal_name() { return original_name; }
        public void setOriginal_name(String n) { this.original_name = n; }
        public String getJob() { return job; }
        public void setJob(String j) { this.job = j; }
        public String getDepartment() { return department; }
        public void setDepartment(String d) { this.department = d; }
    }

    // --- Getters & setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOriginal_title() { return original_title; }
    public void setOriginal_title(String t) { this.original_title = t; }

    public String getOriginal_language() { return original_language; }
    public void setOriginal_language(String l) { this.original_language = l; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getPoster_path() { return poster_path; }
    public void setPoster_path(String p) { this.poster_path = p; }

    public String getRelease_date() { return release_date; }
    public void setRelease_date(String d) { this.release_date = d; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRuntime() { return runtime; }
    public void setRuntime(int runtime) { this.runtime = runtime; }

    public double getVote_average() { return vote_average; }
    public void setVote_average(double v) { this.vote_average = v; }

    public int getVote_count() { return vote_count; }
    public void setVote_count(int v) { this.vote_count = v; }

    public int getBudget() { return budget; }
    public void setBudget(int budget) { this.budget = budget; }

    public int getRevenue() { return revenue; }
    public void setRevenue(int revenue) { this.revenue = revenue; }

    public List<GenreDTO> getGenres() { return genres; }
    public void setGenres(List<GenreDTO> genres) { this.genres = genres; }

    public CreditsDTO getCredits() { return credits; }
    public void setCredits(CreditsDTO credits) { this.credits = credits; }

    public List<CompanyDTO> getProduction_companies() { return production_companies; }
    public void setProduction_companies(List<CompanyDTO> c) { this.production_companies = c; }

    // --- Convenience helpers used by views ---

    public String getYear() {
        return release_date != null && release_date.length() >= 4
                ? release_date.substring(0, 4) : "N/A";
    }

    public String getRuntimeFormatted() {
        if (runtime <= 0) return "N/A";
        return runtime / 60 + "h " + runtime % 60 + "m";
    }

    public String getDirector() {
        if (credits == null || credits.getCrew() == null) return "Unknown";
        return credits.getCrew().stream()
                .filter(c -> "Director".equals(c.getJob()))
                .map(CrewMemberDTO::getOriginal_name)
                .findFirst()
                .orElse("Unknown");
    }

    public List<String> getGenreNames() {
        if (genres == null) return List.of();
        return genres.stream().map(GenreDTO::getName).toList();
    }

    public List<String> getTopCast(int limit) {
        if (credits == null || credits.getCast() == null) return List.of();
        return credits.getCast().stream()
                .limit(limit)
                .map(CastMemberDTO::getOriginal_name)
                .toList();
    }

    public List<String> getCompanyNames() {
        if (production_companies == null) return List.of();
        return production_companies.stream().map(CompanyDTO::getName).toList();
    }
}