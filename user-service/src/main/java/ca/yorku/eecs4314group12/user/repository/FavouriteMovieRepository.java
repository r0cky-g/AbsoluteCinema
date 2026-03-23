package ca.yorku.eecs4314group12.user.repository;

import ca.yorku.eecs4314group12.user.model.FavouriteMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository for favourite movie persistence operations
public interface FavouriteMovieRepository extends JpaRepository<FavouriteMovie, Long> {
    // Get all favourites for a user, most recently added first
    List<FavouriteMovie> findByUserIdOrderByAddedAtDesc(Long userId);
    // Check if a movie is already in the user's favourites
    boolean existsByUserIdAndMovieId(Long userId, Integer movieId);
    // Remove a specific movie from the user's favourites
    void deleteByUserIdAndMovieId(Long userId, Integer movieId);
}
