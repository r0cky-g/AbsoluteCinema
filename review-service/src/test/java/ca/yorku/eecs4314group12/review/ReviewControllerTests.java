package ca.yorku.eecs4314group12.review;

import ca.yorku.eecs4314group12.review.controller.ReviewController;
import ca.yorku.eecs4314group12.review.dto.ReviewDTO;
import ca.yorku.eecs4314group12.review.service.ReviewService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ReviewService reviewService;

    private ReviewDTO testReviewDTO;

    @BeforeEach
    void setUp() {
        testReviewDTO = new ReviewDTO();
        testReviewDTO.setId(1L);
        testReviewDTO.setUserId(1L);
        testReviewDTO.setMovieId(550L);
        testReviewDTO.setRating(9);
        testReviewDTO.setTitle("Great movie");
        testReviewDTO.setContent("Fight Club is amazing!");
        testReviewDTO.setIsSpoiler(false);
        testReviewDTO.setHelpfulCount(0);
    }

    @Test
    void createReview_Success() throws Exception {
        when(reviewService.createReview(any(ReviewDTO.class))).thenReturn(testReviewDTO);

        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReviewDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(9))
                .andExpect(jsonPath("$.data.title").value("Great movie"));
    }

    @Test
    void createReview_DuplicateReview_ReturnsConflict() throws Exception {
        when(reviewService.createReview(any(ReviewDTO.class)))
                .thenThrow(new IllegalStateException("User has already reviewed this movie"));

        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReviewDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createReview_InvalidData_ReturnsBadRequest() throws Exception {
        testReviewDTO.setRating(11);

        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReviewDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReviewsByMovie_ReturnsReviews() throws Exception {
        List<ReviewDTO> reviews = Arrays.asList(testReviewDTO);
        when(reviewService.getReviewsByMovie(550L)).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/movie/550"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].rating").value(9));
    }

    @Test
    void getReviewsByUser_ReturnsReviews() throws Exception {
        List<ReviewDTO> reviews = Arrays.asList(testReviewDTO);
        when(reviewService.getReviewsByUser(1L)).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(1));
    }

    @Test
    void getReviewById_Success() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(testReviewDTO);

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.rating").value(9));
    }

    @Test
    void getReviewById_NotFound() throws Exception {
        when(reviewService.getReviewById(999L))
                .thenThrow(new IllegalArgumentException("Review not found with ID: 999"));

        mockMvc.perform(get("/api/reviews/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateReview_Success() throws Exception {
        testReviewDTO.setRating(10);
        testReviewDTO.setTitle("Even better on rewatch");
        when(reviewService.updateReview(eq(1L), any(ReviewDTO.class))).thenReturn(testReviewDTO);

        mockMvc.perform(put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(10));
    }

    @Test
    void updateReview_WrongUser_ReturnsForbidden() throws Exception {
        when(reviewService.updateReview(eq(1L), any(ReviewDTO.class)))
                .thenThrow(new IllegalStateException("User can only update their own reviews"));

        mockMvc.perform(put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReviewDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteReview_Success() throws Exception {
        mockMvc.perform(delete("/api/reviews/1")
                .param("userId", "1")
                .param("userRole", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteReview_WrongUser_ReturnsForbidden() throws Exception {
        doThrow(new IllegalStateException("User can only delete their own reviews, or must be a moderator/admin"))
            .when(reviewService).deleteReview(1L, 999L, "USER");

        mockMvc.perform(delete("/api/reviews/1")
                .param("userId", "999")
                .param("userRole", "USER"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getMovieStats_ReturnsStatistics() throws Exception {
        when(reviewService.getAverageRating(550L)).thenReturn(8.5);
        when(reviewService.getReviewCount(550L)).thenReturn(42L);

        mockMvc.perform(get("/api/reviews/movie/550/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.movieId").value(550))
                .andExpect(jsonPath("$.data.averageRating").value(8.5))
                .andExpect(jsonPath("$.data.reviewCount").value(42));
    }

    @Test
    void markAsHelpful_Success() throws Exception {
        testReviewDTO.setHelpfulCount(1);
        when(reviewService.markAsHelpful(1L)).thenReturn(testReviewDTO);

        mockMvc.perform(post("/api/reviews/1/helpful"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.helpfulCount").value(1));
    }
}
