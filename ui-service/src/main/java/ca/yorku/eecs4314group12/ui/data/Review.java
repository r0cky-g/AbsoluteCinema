package ca.yorku.eecs4314group12.ui.data;

/**
 * Placeholder DTO for a review.
 * TODO: Replace with actual model once review-service API is finalized.
 */
public class Review {

    private final String movieTitle;
    private final int stars;       // 1–5
    private final String body;
    private final String datePosted;

    public Review(String movieTitle, int stars, String body, String datePosted) {
        this.movieTitle = movieTitle;
        this.stars = stars;
        this.body = body;
        this.datePosted = datePosted;
    }

    public String getMovieTitle() { return movieTitle; }
    public int    getStars()      { return stars; }
    public String getBody()       { return body; }
    public String getDatePosted() { return datePosted; }
}