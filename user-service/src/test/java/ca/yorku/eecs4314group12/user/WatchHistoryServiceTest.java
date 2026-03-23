package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.model.WatchHistory;
import ca.yorku.eecs4314group12.user.repository.WatchHistoryRepository;
import ca.yorku.eecs4314group12.user.service.WatchHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchHistoryServiceTest {

    @Mock
    private WatchHistoryRepository watchHistoryRepository;

    @InjectMocks
    private WatchHistoryService watchHistoryService;

    private Long userId;
    private Integer movieId;
    private WatchHistory entry;

    @BeforeEach
    void setUp() {
        userId = 1L;
        movieId = 550;
        entry = new WatchHistory(userId, movieId);
        entry.setId(1L);
    }

    @Test
    void testAddToHistory_NewEntry() {
        when(watchHistoryRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        when(watchHistoryRepository.save(any(WatchHistory.class))).thenReturn(entry);

        WatchHistory result = watchHistoryService.addToHistory(userId, movieId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(movieId, result.getMovieId());
        verify(watchHistoryRepository, never()).deleteByUserIdAndMovieId(any(), any());
        verify(watchHistoryRepository, times(1)).save(any(WatchHistory.class));
    }

    @Test
    void testAddToHistory_ExistingEntry_UpdatesTimestamp() {
        when(watchHistoryRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(true);
        when(watchHistoryRepository.save(any(WatchHistory.class))).thenReturn(entry);

        WatchHistory result = watchHistoryService.addToHistory(userId, movieId);

        assertNotNull(result);
        verify(watchHistoryRepository, times(1)).deleteByUserIdAndMovieId(userId, movieId);
        verify(watchHistoryRepository, times(1)).save(any(WatchHistory.class));
    }

    @Test
    void testGetUserHistory_ReturnsList() {
        WatchHistory entry2 = new WatchHistory(userId, 100);
        entry2.setId(2L);

        when(watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(userId))
                .thenReturn(Arrays.asList(entry, entry2));

        List<WatchHistory> result = watchHistoryService.getUserHistory(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(watchHistoryRepository, times(1)).findByUserIdOrderByWatchedAtDesc(userId);
    }

    @Test
    void testGetUserHistory_EmptyList() {
        when(watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(userId))
                .thenReturn(Collections.emptyList());

        List<WatchHistory> result = watchHistoryService.getUserHistory(userId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testRemoveFromHistory_Success() {
        when(watchHistoryRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(true);

        watchHistoryService.removeFromHistory(userId, movieId);

        verify(watchHistoryRepository, times(1)).deleteByUserIdAndMovieId(userId, movieId);
    }

    @Test
    void testRemoveFromHistory_NotFound_ThrowsException() {
        when(watchHistoryRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () ->
                watchHistoryService.removeFromHistory(userId, movieId));

        verify(watchHistoryRepository, never()).deleteByUserIdAndMovieId(any(), any());
    }

    @Test
    void testAddToHistory_SetsWatchedAt() {
        when(watchHistoryRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        when(watchHistoryRepository.save(any(WatchHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WatchHistory result = watchHistoryService.addToHistory(userId, movieId);

        assertNotNull(result.getWatchedAt());
    }
}
