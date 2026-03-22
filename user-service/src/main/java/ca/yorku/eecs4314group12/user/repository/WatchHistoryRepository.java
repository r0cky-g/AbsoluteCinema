package ca.yorku.eecs4314group12.user.repository;

import ca.yorku.eecs4314group12.user.model.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    List<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId);

    void deleteByUserIdAndMovieId(Long userId, Integer movieId);

    boolean existsByUserIdAndMovieId(Long userId, Integer movieId);
}
