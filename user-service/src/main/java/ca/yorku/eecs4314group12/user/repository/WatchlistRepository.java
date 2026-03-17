package ca.yorku.eecs4314group12.user.repository;

import ca.yorku.eecs4314group12.user.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserId(Long userId);

    Optional<Watchlist> findByUserIdAndMovieId(Long userId, Integer movieId);

    boolean existsByUserIdAndMovieId(Long userId, Integer movieId);

    void deleteByUserIdAndMovieId(Long userId, Integer movieId);
}
