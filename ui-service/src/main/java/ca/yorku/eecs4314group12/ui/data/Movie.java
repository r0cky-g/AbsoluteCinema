package ca.yorku.eecs4314group12.ui.data;

/**
 * Placeholder DTO for a movie.
 * TODO: Replace with actual model once movie-service API is finalized.
 */
public class Movie {

    private final String title;
    private final String year;
    private final String genre;
    private final double rating;
    private final String posterEmoji; // placeholder until real poster images are available

    public Movie(String title, String year, String genre, double rating, String posterEmoji) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
        this.posterEmoji = posterEmoji;
    }

    public String getTitle()       { return title; }
    public String getYear()        { return year; }
    public String getGenre()       { return genre; }
    public double getRating()      { return rating; }
    public String getPosterEmoji() { return posterEmoji; }
}