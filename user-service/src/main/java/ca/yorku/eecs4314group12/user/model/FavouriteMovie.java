package ca.yorku.eecs4314group12.user.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entity representing a user's favourite movie
@Entity
@Table(name = "user_favourite_movies")
public class FavouriteMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who favourited the movie
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // TMDB movie ID
    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    // Timestamp when the movie was added to favourites
    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    public FavouriteMovie() {
        this.addedAt = LocalDateTime.now();
    }

    public FavouriteMovie(Long userId, Integer movieId) {
        this.userId = userId;
        this.movieId = movieId;
        this.addedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
