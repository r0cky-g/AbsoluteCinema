package ca.yorku.eecs4314group12.user.service;

import ca.yorku.eecs4314group12.user.model.FavouriteMovie;
import ca.yorku.eecs4314group12.user.repository.FavouriteMovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Service handling favourite movie business logic
@Service
public class FavouriteMovieService {

    private final FavouriteMovieRepository favouriteMovieRepository;

    public FavouriteMovieService(FavouriteMovieRepository favouriteMovieRepository) {
        this.favouriteMovieRepository = favouriteMovieRepository;
    }

    // Add a movie to favourites; throws 409 if already favourited
    public FavouriteMovie addFavourite(Long userId, Integer movieId) {
        if (favouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Movie already in favourites");
        }
        return favouriteMovieRepository.save(new FavouriteMovie(userId, movieId));
    }

    // Get all favourited movies for a user
    public List<FavouriteMovie> getUserFavourites(Long userId) {
        return favouriteMovieRepository.findByUserIdOrderByAddedAtDesc(userId);
    }

    // Remove a movie from favourites; throws 404 if not found
    public void removeFavourite(Long userId, Integer movieId) {
        if (!favouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found in favourites");
        }
        favouriteMovieRepository.deleteByUserIdAndMovieId(userId, movieId);
    }
}
