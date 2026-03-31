package ca.yorku.eecs4314group12.ui;

import ca.yorku.eecs4314group12.ui.data.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UI-layer DTOs — verifies getters/setters and null-safety.
 * Place in: ui-service/src/test/java/ca/yorku/eecs4314group12/ui/DtoFieldTest.java
 */
class DtoFieldTest {

    // -------------------------------------------------------------------------
    // ForumCommentDTO
    // -------------------------------------------------------------------------

    @Test
    void forumCommentDTO_gettersAndSetters_workCorrectly() {
        ForumCommentDTO dto = new ForumCommentDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(10L);
        dto.setPostId(5L);
        dto.setUserId(99L);
        dto.setContent("Great movie!");
        dto.setCreatedAt(now);

        assertEquals(10L, dto.getId());
        assertEquals(5L, dto.getPostId());
        assertEquals(99L, dto.getUserId());
        assertEquals("Great movie!", dto.getContent());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void forumCommentDTO_allFieldsNull_doesNotThrow() {
        ForumCommentDTO dto = new ForumCommentDTO();
        assertNull(dto.getId());
        assertNull(dto.getPostId());
        assertNull(dto.getUserId());
        assertNull(dto.getContent());
        assertNull(dto.getCreatedAt());
    }

    // -------------------------------------------------------------------------
    // ForumPostDTO
    // -------------------------------------------------------------------------

    @Test
    void forumPostDTO_gettersAndSetters_workCorrectly() {
        ForumPostDTO dto = new ForumPostDTO();
        dto.setId(1L);
        dto.setTitle("Best Horror Films");
        dto.setContent("Here are my picks...");
        dto.setUserId(7L);

        assertEquals(1L, dto.getId());
        assertEquals("Best Horror Films", dto.getTitle());
        assertEquals("Here are my picks...", dto.getContent());
        assertEquals(7L, dto.getUserId());
    }

    @Test
    void forumPostDTO_nullUserId_doesNotThrow() {
        ForumPostDTO dto = new ForumPostDTO();
        dto.setUserId(null);
        assertNull(dto.getUserId());
    }

    // -------------------------------------------------------------------------
    // UserResponseDTO
    // -------------------------------------------------------------------------

    @Test
    void userResponseDTO_gettersAndSetters_workCorrectly() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(1L);
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setEmailVerified(true);
        dto.setRole("USER");
        dto.setLikedGenres(Set.of("Horror", "Comedy"));

        assertEquals(1L, dto.getId());
        assertEquals("alice", dto.getUsername());
        assertEquals("alice@example.com", dto.getEmail());
        assertTrue(dto.isEmailVerified());
        assertEquals("USER", dto.getRole());
        assertTrue(dto.getLikedGenres().contains("Horror"));
    }

    @Test
    void userResponseDTO_emailNotVerified_returnsFalse() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmailVerified(false);
        assertFalse(dto.isEmailVerified());
    }

    @Test
    void userResponseDTO_nullLikedGenres_doesNotThrow() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setLikedGenres(null);
        assertNull(dto.getLikedGenres());
    }

    // -------------------------------------------------------------------------
    // WatchlistDTO
    // -------------------------------------------------------------------------

    @Test
    void watchlistDTO_gettersAndSetters_workCorrectly() {
        WatchlistDTO dto = new WatchlistDTO();
        dto.setUserId(1L);
        dto.setMovieId(550);

        assertEquals(1L, dto.getUserId());
        assertEquals(550, dto.getMovieId());
    }

    @Test
    void watchlistDTO_defaultValues_areNull() {
        WatchlistDTO dto = new WatchlistDTO();
        assertNull(dto.getUserId());
        assertNull(dto.getMovieId());
    }

    // -------------------------------------------------------------------------
    // ReviewDTO
    // -------------------------------------------------------------------------

    @Test
    void reviewDTO_gettersAndSetters_workCorrectly() {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(1L);
        dto.setUserId(10L);
        dto.setMovieId(550L);
        dto.setRating(8);
        dto.setTitle("Brilliant film");
        dto.setContent("One of the best I have seen.");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getUserId());
        assertEquals(550L, dto.getMovieId());
        assertEquals(8, dto.getRating());
        assertEquals("Brilliant film", dto.getTitle());
        assertEquals("One of the best I have seen.", dto.getContent());
    }

    @Test
    void reviewDTO_nullContent_doesNotThrow() {
        ReviewDTO dto = new ReviewDTO();
        dto.setContent(null);
        assertNull(dto.getContent());
    }
}