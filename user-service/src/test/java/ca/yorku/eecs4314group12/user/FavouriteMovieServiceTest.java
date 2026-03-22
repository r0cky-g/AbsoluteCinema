package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.model.FavouriteMovie;
import ca.yorku.eecs4314group12.user.repository.FavouriteMovieRepository;
import ca.yorku.eecs4314group12.user.service.FavouriteMovieService;
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
class FavouriteMovieServiceTest {

    @Mock
    private FavouriteMovieRepository favouriteMovieRepository;

    @InjectMocks
    private FavouriteMovieService favouriteMovieService;

    private Long userId;
    private Integer movieId;
    private FavouriteMovie entry;

    @BeforeEach
    void setUp() {
        userId = 1L;
        movieId = 550;
        entry = new FavouriteMovie(userId, movieId);
        entry.setId(1L);
    }

    @Test
    void testAddFavourite_NewEntry() {
        when(favouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        when(favouriteMovieRepository.save(any(FavouriteMovie.class))).thenReturn(entry);

        FavouriteMovie result = favouriteMovieService.addFavourite(userId, movieId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(movieId, result.getMovieId());
        verify(favouriteMovieRepository, times(1)).save(any(FavouriteMovie.class));
    }

    @Test
    void testAddFavourite_AlreadyExists_ThrowsConflict() {
        when(favouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                favouriteMovieService.addFavourite(userId, movieId));

        verify(favouriteMovieRepository, never()).save(any());
    }

    @Test
    void testGetUserFavourites_ReturnsList() {
        FavouriteMovie entry2 = new FavouriteMovie(userId, 100);
        entry2.setId(2L);

        when(favouriteMovieRepository.findByUserIdOrderByAddedAtDesc(userId))
                .thenReturn(Arrays.asList(entry, entry2));

        List<FavouriteMovie> result = favouriteMovieService.getUserFavourites(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(favouriteMovieRepository, times(1)).findByUserIdOrderByAddedAtDesc(userId);
    }

    @Test
    void testGetUserFavourites_EmptyList() {
        when(favouriteMovieRepository.findByUserIdOrderByAddedAtDesc(userId))
                .thenReturn(Collections.emptyList());

        List<FavouriteMovie> result = favouriteMovieService.getUserFavourites(userId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testRemoveFavourite_Success() {
        when(favouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(true);

        favouriteMovieService.removeFavourite(userId, movieId);

        verify(favouriteMovieRepository, times(1)).deleteByUserIdAndMovieId(userId, movieId);
    }

    @Test
    void testRemoveFavourite_NotFound_ThrowsException() {
        when(favouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () ->
                favouriteMovieService.removeFavourite(userId, movieId));

        verify(favouriteMovieRepository, never()).deleteByUserIdAndMovieId(any(), any());
    }

    @Test
    void testAddFavourite_SetsAddedAt() {
        when(favouriteMovieRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        when(favouriteMovieRepository.save(any(FavouriteMovie.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FavouriteMovie result = favouriteMovieService.addFavourite(userId, movieId);

        assertNotNull(result.getAddedAt());
    }
}
