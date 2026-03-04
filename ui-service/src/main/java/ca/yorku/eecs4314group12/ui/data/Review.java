package ca.yorku.eecs4314group12.ui.data;

/**
 * Placeholder DTO for a review, shaped to match review-service ReviewDTO.
 * TODO: Replace with WebClient call to review-service GET /api/reviews/movie/{id}
 */
public class Review {

    private final String movieTitle;
    private final int stars;          // 1–5 display stars
    private final int rating;         // raw 1–10 matching review-service
    private final String title;
    private final String body;
    private final String datePosted;
    private final String username;
    private final int helpfulCount;
    private final boolean isSpoiler;

    /** Legacy 4-arg constructor — keeps AccountView working unchanged. */
    public Review(String movieTitle, int stars, String body, String datePosted) {
        this(movieTitle, stars, stars * 2, movieTitle + " Review",
                body, datePosted, "Anonymous", 0, false);
    }

    public Review(String movieTitle, int stars, int rating, String title,
                  String body, String datePosted, String username,
                  int helpfulCount, boolean isSpoiler) {
        this.movieTitle = movieTitle;
        this.stars = stars;
        this.rating = rating;
        this.title = title;
        this.body = body;
        this.datePosted = datePosted;
        this.username = username;
        this.helpfulCount = helpfulCount;
        this.isSpoiler = isSpoiler;
    }

    public String getMovieTitle()   { return movieTitle; }
    public int    getStars()        { return stars; }
    public int    getRating()       { return rating; }
    public String getTitle()        { return title; }
    public String getBody()         { return body; }
    public String getDatePosted()   { return datePosted; }
    public String getUsername()     { return username; }
    public int    getHelpfulCount() { return helpfulCount; }
    public boolean isSpoiler()      { return isSpoiler; }
}