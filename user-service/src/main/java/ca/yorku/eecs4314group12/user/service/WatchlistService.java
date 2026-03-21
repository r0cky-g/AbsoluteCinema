package ca.yorku.eecs4314group12.user.service;

import ca.yorku.eecs4314group12.user.model.Watchlist;
import ca.yorku.eecs4314group12.user.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public Watchlist addToWatchlist(Long userId, Integer movieId) {
        if (watchlistRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Movie already in watchlist");
        }

        Watchlist watchlist = new Watchlist(userId, movieId);
        return watchlistRepository.save(watchlist);
    }

    public void removeFromWatchlist(Long userId, Integer movieId) {
        if (!watchlistRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Movie not found in watchlist");
        }

        watchlistRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    public List<Watchlist> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId);
    }

    public boolean isInWatchlist(Long userId, Integer movieId) {
        return watchlistRepository.existsByUserIdAndMovieId(userId, movieId);
    }
}
