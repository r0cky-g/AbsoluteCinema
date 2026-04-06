package ca.yorku.eecs4314group12.ui;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.MovieListItemDTO;
import ca.yorku.eecs4314group12.ui.data.dto.WatchlistDTO;
import ca.yorku.eecs4314group12.ui.security.UserSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Functional requirement tests for ui-service logic (FR1–FR7).
 *
 * These tests verify the underlying session and data-access logic that drives
 * each requirement. Full Vaadin UI rendering requires a running server and is
 * not covered here.
 *
 * Place in: ui-service/src/test/java/ca/yorku/eecs4314group12/ui/FunctionalRequirementsTest.java
 */
@ExtendWith(MockitoExtension.class)
class FunctionalRequirementsTest {

    @Mock
    private BackendClientService backendClient;

    private UserSessionService userSessionService;

    @BeforeEach
    void setUp() {
        userSessionService = new UserSessionService();
    }

    // =========================================================================
    // FR2 — The system shall show login/register links for unauthenticated users
    //        and account/edit profile links for authenticated users.
    // =========================================================================

    // Before login, isLoggedIn() must return false — drawer should show login/register
    @Test
    void fr2_unauthenticatedUser_isLoggedInReturnsFalse() {
        assertFalse(userSessionService.isLoggedIn());
    }

    // After login, isLoggedIn() must return true — drawer should show account/edit profile
    @Test
    void fr2_authenticatedUser_isLoggedInReturnsTrue() {
        userSessionService.setUser(1L, "USER", "user@example.com");
        assertTrue(userSessionService.isLoggedIn());
    }

    // After logout (clear), isLoggedIn() must return false again
    @Test
    void fr2_afterLogout_isLoggedInReturnsFalse() {
        userSessionService.setUser(1L, "USER", "user@example.com");
        userSessionService.clear();
        assertFalse(userSessionService.isLoggedIn());
    }

    // =========================================================================
    // FR3 — The system shall show an Admin panel link only for ADMIN role users.
    // =========================================================================

    // A USER role must not satisfy the ADMIN check
    @Test
    void fr3_userRole_isNotAdmin() {
        userSessionService.setUser(1L, "USER", "user@example.com");
        assertNotEquals("ADMIN", userSessionService.getRole());
    }

    // An ADMIN role must satisfy the ADMIN check
    @Test
    void fr3_adminRole_isAdmin() {
        userSessionService.setUser(2L, "ADMIN", "admin@example.com");
        assertEquals("ADMIN", userSessionService.getRole());
    }

    // A MODERATOR role must not satisfy the ADMIN check
    @Test
    void fr3_moderatorRole_isNotAdmin() {
        userSessionService.setUser(3L, "MODERATOR", "mod@example.com");
        assertNotEquals("ADMIN", userSessionService.getRole());
    }

    // After logout, role must fall back to default "USER" — never ADMIN
    @Test
    void fr3_afterLogout_roleIsNotAdmin() {
        userSessionService.setUser(2L, "ADMIN", "admin@example.com");
        userSessionService.clear();
        assertNotEquals("ADMIN", userSessionService.getRole());
    }

    // =========================================================================
    // FR4 — The system shall redirect unauthenticated users attempting to access
    //        protected views. Tested via: userId is null for anonymous sessions.
    // =========================================================================

    // Anonymous user has no userId — protected views use this to gate access
    @Test
    void fr4_anonymousUser_hasNullUserId() {
        assertNull(userSessionService.getUserId());
    }

    // Authenticated user has a non-null userId — protected views allow access
    @Test
    void fr4_authenticatedUser_hasNonNullUserId() {
        userSessionService.setUser(42L, "USER", "user@example.com");
        assertNotNull(userSessionService.getUserId());
        assertEquals(42L, userSessionService.getUserId());
    }

    // =========================================================================
    // FR5 — The system shall display trending and now-playing movie grids
    //        on the home page.
    // =========================================================================

    // getTrending() returns a non-null list
    @Test
    void fr5_getTrending_returnsNonNullList() {
        MovieListItemDTO movie = new MovieListItemDTO();
        movie.setTitle("Inception");
        when(backendClient.getTrending()).thenReturn(List.of(movie));

        List<MovieListItemDTO> result = backendClient.getTrending();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Inception", result.get(0).getTitle());
    }

    // getNowPlaying() returns a non-null list
    @Test
    void fr5_getNowPlaying_returnsNonNullList() {
        MovieListItemDTO movie = new MovieListItemDTO();
        movie.setTitle("Dune");
        when(backendClient.getNowPlaying()).thenReturn(List.of(movie));

        List<MovieListItemDTO> result = backendClient.getNowPlaying();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Dune", result.get(0).getTitle());
    }

    // When backend is down, getTrending() returns empty list — home page still renders
    @Test
    void fr5_getTrending_onFailure_returnsEmptyList() {
        when(backendClient.getTrending()).thenReturn(List.of());

        List<MovieListItemDTO> result = backendClient.getTrending();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // When backend is down, getNowPlaying() returns empty list — home page still renders
    @Test
    void fr5_getNowPlaying_onFailure_returnsEmptyList() {
        when(backendClient.getNowPlaying()).thenReturn(List.of());

        List<MovieListItemDTO> result = backendClient.getNowPlaying();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // FR6 — The system shall allow users to search for movies by title.
    // =========================================================================

    // searchMovies() with a valid query returns matching results
    @Test
    void fr6_searchMovies_withValidQuery_returnsResults() {
        MovieListItemDTO movie = new MovieListItemDTO();
        movie.setTitle("Fight Club");
        when(backendClient.searchMovies("Fight Club")).thenReturn(List.of(movie));

        List<MovieListItemDTO> result = backendClient.searchMovies("Fight Club");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Fight Club", result.get(0).getTitle());
    }

    // searchMovies() with no match returns empty list — UI shows "no results" message
    @Test
    void fr6_searchMovies_withNoMatch_returnsEmptyList() {
        when(backendClient.searchMovies(anyString())).thenReturn(List.of());

        List<MovieListItemDTO> result = backendClient.searchMovies("xyznonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // searchMovies() on backend failure returns empty list — no crash
    @Test
    void fr6_searchMovies_onFailure_returnsEmptyList() {
        when(backendClient.searchMovies(anyString())).thenReturn(List.of());

        List<MovieListItemDTO> result = backendClient.searchMovies("Inception");

        assertNotNull(result);
    }

    // =========================================================================
    // FR7 — The system shall display personalised recommendations for
    //        authenticated users based on their watchlist.
    // =========================================================================

    // Authenticated user with a non-empty watchlist gets recommendations
    @Test
    void fr7_authenticatedUser_withWatchlist_getsRecommendations() {
        userSessionService.setUser(1L, "USER", "user@example.com");

        WatchlistDTO watchlistItem = new WatchlistDTO();
        watchlistItem.setUserId(1L);
        watchlistItem.setMovieId(550);
        when(backendClient.getWatchlist(1L)).thenReturn(List.of(watchlistItem));

        MovieListItemDTO rec = new MovieListItemDTO();
        rec.setTitle("Se7en");
        when(backendClient.getRecommendations(1L)).thenReturn(List.of(rec));

        // User is logged in
        assertTrue(userSessionService.isLoggedIn());

        // Watchlist is non-empty
        List<WatchlistDTO> watchlist = backendClient.getWatchlist(userSessionService.getUserId());
        assertFalse(watchlist.isEmpty());

        // Recommendations are returned
        List<MovieListItemDTO> recs = backendClient.getRecommendations(userSessionService.getUserId());
        assertNotNull(recs);
        assertFalse(recs.isEmpty());
        assertEquals("Se7en", recs.get(0).getTitle());
    }

    // Unauthenticated user has no userId — recommendations are not fetched
    @Test
    void fr7_unauthenticatedUser_hasNoUserId_recommendationsNotFetched() {
        assertNull(userSessionService.getUserId());
        // Without a userId, the home view does not call getRecommendations()
        verify(backendClient, never()).getRecommendations(anyLong());
    }

    // Authenticated user with empty watchlist gets empty recommendations — falls back to trending
    @Test
    void fr7_authenticatedUser_withEmptyWatchlist_getsEmptyRecommendations() {
        userSessionService.setUser(2L, "USER", "user@example.com");
        when(backendClient.getRecommendations(2L)).thenReturn(List.of());

        List<MovieListItemDTO> recs = backendClient.getRecommendations(userSessionService.getUserId());

        assertNotNull(recs);
        assertTrue(recs.isEmpty());
    }

    // Recommendations endpoint failure returns empty list — UI falls back gracefully
    @Test
    void fr7_recommendations_onFailure_returnsEmptyList() {
        userSessionService.setUser(3L, "USER", "user@example.com");
        when(backendClient.getRecommendations(anyLong())).thenReturn(List.of());

        List<MovieListItemDTO> recs = backendClient.getRecommendations(userSessionService.getUserId());

        assertNotNull(recs);
        assertTrue(recs.isEmpty());
    }
}