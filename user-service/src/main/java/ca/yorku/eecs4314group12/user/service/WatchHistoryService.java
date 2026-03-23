package ca.yorku.eecs4314group12.user.service;

import ca.yorku.eecs4314group12.user.model.WatchHistory;
import ca.yorku.eecs4314group12.user.repository.WatchHistoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;

    public WatchHistoryService(WatchHistoryRepository watchHistoryRepository) {
        this.watchHistoryRepository = watchHistoryRepository;
    }

    public WatchHistory addToHistory(Long userId, Integer movieId) {
        if (watchHistoryRepository.existsByUserIdAndMovieId(userId, movieId)) {
            watchHistoryRepository.deleteByUserIdAndMovieId(userId, movieId);
        }
        return watchHistoryRepository.save(new WatchHistory(userId, movieId));
    }

    public List<WatchHistory> getUserHistory(Long userId) {
        return watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(userId);
    }

    public void removeFromHistory(Long userId, Integer movieId) {
        if (!watchHistoryRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found in watch history");
        }
        watchHistoryRepository.deleteByUserIdAndMovieId(userId, movieId);
    }
}
