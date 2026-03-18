package ca.yorku.eecs4314group12.api.dto.userServiceDTO;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "user_watchlist")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    public Watchlist() {
        this.addedAt = LocalDateTime.now();
    }

    public Watchlist(Long userId, Integer movieId) {
        this.userId = userId;
        this.movieId = movieId;
        this.addedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
