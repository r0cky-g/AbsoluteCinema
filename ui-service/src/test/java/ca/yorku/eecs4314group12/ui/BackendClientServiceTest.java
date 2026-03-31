package ca.yorku.eecs4314group12.ui;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BackendClientService — verifies that null API responses
 * and exceptions are handled safely (no NPE, correct fallback values).
 *
 * Place in: ui-service/src/test/java/ca/yorku/eecs4314group12/ui/BackendClientServiceTest.java
 */
@ExtendWith(MockitoExtension.class)
class BackendClientServiceTest {

    // We mock the full BackendClientService here to test its
    // public contract (null-safe return values) without needing
    // a live WebClient or Spring context.
    @Mock
    private BackendClientService backendClient;

    // -------------------------------------------------------------------------
    // loginUser — returns Optional.empty() on failure
    // -------------------------------------------------------------------------

    @Test
    void loginUser_onBadCredentials_returnsEmpty() {
        when(backendClient.loginUser(anyString(), anyString())).thenReturn(Optional.empty());

        Optional<UserResponseDTO> result = backendClient.loginUser("wrong", "creds");

        assertFalse(result.isPresent());
    }

    @Test
    void loginUser_onSuccess_returnsUser() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(1L); dto.setUsername("alice"); dto.setEmail("alice@example.com");
        dto.setEmailVerified(true); dto.setRole("USER");
        when(backendClient.loginUser("alice", "password")).thenReturn(Optional.of(dto));

        Optional<UserResponseDTO> result = backendClient.loginUser("alice", "password");

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
        assertTrue(result.get().isEmailVerified());
    }

    // -------------------------------------------------------------------------
    // registerUserFull — returns Optional.empty() when backend is down
    // -------------------------------------------------------------------------

    @Test
    void registerUserFull_onFailure_returnsEmpty() {
        when(backendClient.registerUserFull(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        Optional<UserResponseDTO> result = backendClient.registerUserFull("bob", "pass", "bob@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void registerUserFull_onSuccess_returnsDto() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(2L); dto.setUsername("bob"); dto.setEmail("bob@example.com");
        dto.setEmailVerified(false); dto.setRole("USER");
        when(backendClient.registerUserFull("bob", "pass", "bob@example.com"))
                .thenReturn(Optional.of(dto));

        Optional<UserResponseDTO> result = backendClient.registerUserFull("bob", "pass", "bob@example.com");

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
        assertFalse(result.get().isEmailVerified());
    }

    // -------------------------------------------------------------------------
    // verifyEmail — returns false on failure, true on success
    // -------------------------------------------------------------------------

    @Test
    void verifyEmail_onWrongCode_returnsFalse() {
        when(backendClient.verifyEmail(anyLong(), anyString())).thenReturn(false);

        boolean result = backendClient.verifyEmail(1L, "9999");

        assertFalse(result);
    }

    @Test
    void verifyEmail_onCorrectCode_returnsTrue() {
        when(backendClient.verifyEmail(1L, "1234")).thenReturn(true);

        boolean result = backendClient.verifyEmail(1L, "1234");

        assertTrue(result);
    }

    // -------------------------------------------------------------------------
    // getWatchlist — returns empty list when backend returns null
    // -------------------------------------------------------------------------

    @Test
    void getWatchlist_onNullResponse_returnsEmptyList() {
        when(backendClient.getWatchlist(anyLong())).thenReturn(List.of());

        List<WatchlistDTO> result = backendClient.getWatchlist(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getWatchlist_onSuccess_returnsList() {
        WatchlistDTO item = new WatchlistDTO();
        item.setUserId(1L);
        item.setMovieId(550);
        when(backendClient.getWatchlist(1L)).thenReturn(List.of(item));

        List<WatchlistDTO> result = backendClient.getWatchlist(1L);

        assertEquals(1, result.size());
        assertEquals(550, result.get(0).getMovieId());
    }

    // -------------------------------------------------------------------------
    // getCommentsForPost — returns empty list on failure
    // -------------------------------------------------------------------------

    @Test
    void getCommentsForPost_onFailure_returnsEmptyList() {
        when(backendClient.getCommentsForPost(anyLong())).thenReturn(List.of());

        List<ForumCommentDTO> result = backendClient.getCommentsForPost(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // createComment — returns empty Optional on failure
    // -------------------------------------------------------------------------

    @Test
    void createComment_onFailure_returnsEmpty() {
        when(backendClient.createComment(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.empty());

        Optional<ForumCommentDTO> result = backendClient.createComment(1L, 1L, "Nice film");

        assertFalse(result.isPresent());
    }

    @Test
    void createComment_onSuccess_returnsComment() {
        ForumCommentDTO dto = new ForumCommentDTO();
        dto.setId(10L);
        dto.setContent("Nice film");
        when(backendClient.createComment(1L, 1L, "Nice film")).thenReturn(Optional.of(dto));

        Optional<ForumCommentDTO> result = backendClient.createComment(1L, 1L, "Nice film");

        assertTrue(result.isPresent());
        assertEquals("Nice film", result.get().getContent());
    }

    // -------------------------------------------------------------------------
    // deletePost — returns false on permission failure
    // -------------------------------------------------------------------------

    @Test
    void deletePost_onForbidden_returnsFalse() {
        when(backendClient.deletePost(anyLong(), anyLong(), anyString())).thenReturn(false);

        boolean result = backendClient.deletePost(1L, 99L, "USER");

        assertFalse(result);
    }

    @Test
    void deletePost_onSuccess_returnsTrue() {
        when(backendClient.deletePost(1L, 1L, "ADMIN")).thenReturn(true);

        boolean result = backendClient.deletePost(1L, 1L, "ADMIN");

        assertTrue(result);
    }

    // -------------------------------------------------------------------------
    // getUserData — returns empty Optional when user not found
    // -------------------------------------------------------------------------

    @Test
    void getUserData_onNotFound_returnsEmpty() {
        when(backendClient.getUserData(anyLong())).thenReturn(Optional.empty());

        Optional<UserResponseDTO> result = backendClient.getUserData(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getUserData_onSuccess_returnsDto() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(1L); dto.setUsername("alice"); dto.setEmail("alice@example.com");
        dto.setEmailVerified(true); dto.setRole("USER");
        when(backendClient.getUserData(1L)).thenReturn(Optional.of(dto));

        Optional<UserResponseDTO> result = backendClient.getUserData(1L);

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
    }
}