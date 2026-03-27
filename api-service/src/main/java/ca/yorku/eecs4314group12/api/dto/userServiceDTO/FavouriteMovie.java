package ca.yorku.eecs4314group12.api.dto.userServiceDTO;

import java.time.LocalDateTime;

public class FavouriteMovie {

    private Long id;
    private Long userId;
    private Integer movieId;
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