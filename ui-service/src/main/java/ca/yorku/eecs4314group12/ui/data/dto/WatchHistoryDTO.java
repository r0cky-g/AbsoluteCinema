package ca.yorku.eecs4314group12.ui.data.dto;

import java.time.LocalDateTime;

public class WatchHistoryDTO {
    private Long id;
    private Long userId;
    private Integer movieId;
    private LocalDateTime watchedAt;

    public WatchHistoryDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }
    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }
}