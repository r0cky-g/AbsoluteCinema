package ca.yorku.eecs4314group12.review;

import ca.yorku.eecs4314group12.review.dto.ReviewDTO;
import ca.yorku.eecs4314group12.review.model.Review;
import ca.yorku.eecs4314group12.review.repository.ReviewRepository;
import ca.yorku.eecs4314group12.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Not tested yet, just putting some for now

@ExtendWith(MockitoExtension.class)
class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewDTO testReviewDTO;
    private Review testReview;

    @BeforeEach
    void setUp() {
        // Setup test data
        testReviewDTO = new ReviewDTO();
        testReviewDTO.setUserId(1L);
        testReviewDTO.setMovieId(550L);
        testReviewDTO.setRating(9);
        testReviewDTO.setTitle("Great movie");
        testReviewDTO.setContent("Fight Club is amazing!");
        testReviewDTO.setIsSpoiler(false);

        testReview = new Review(1L, 550L, 9, "Great movie", "Fight Club is amazing!");
        testReview.setId(1L);
    }

    @Test
    void createReview_Success() {
        // Given
        when(reviewRepository.existsByUserIdAndMovieIdAndIsDeletedFalse(1L, 550L))
                .thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewDTO result = reviewService.createReview(testReviewDTO);

        // Then
        assertNotNull(result);
        assertEquals(9, result.getRating());
        assertEquals("Great movie", result.getTitle());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void createReview_DuplicateReview_ThrowsException() {
        // Given
        when(reviewRepository.existsByUserIdAndMovieIdAndIsDeletedFalse(1L, 550L))
                .thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            reviewService.createReview(testReviewDTO);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getReviewsByMovie_ReturnsReviews() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByMovieIdAndIsDeletedFalseOrderByCreatedAtDesc(550L))
                .thenReturn(reviews);

        // When
        List<ReviewDTO> result = reviewService.getReviewsByMovie(550L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(9, result.get(0).getRating());
    }

    @Test
    void getReviewById_Success() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        ReviewDTO result = reviewService.getReviewById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(9, result.getRating());
    }

    @Test
    void getReviewById_NotFound_ThrowsException() {
        // Given
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getReviewById(999L);
        });
    }

    @Test
    void updateReview_Success() {
        // Given
        ReviewDTO updateDTO = new ReviewDTO();
        updateDTO.setUserId(1L);
        updateDTO.setRating(10);
        updateDTO.setTitle("Even better on rewatch");
        updateDTO.setContent("Masterpiece!");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewDTO result = reviewService.updateReview(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void updateReview_WrongUser_ThrowsException() {
        // Given
        ReviewDTO updateDTO = new ReviewDTO();
        updateDTO.setUserId(999L); // Different user
        updateDTO.setRating(10);
        updateDTO.setTitle("Hacking attempt");
        updateDTO.setContent("Should fail!");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            reviewService.updateReview(1L, updateDTO);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_Success() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        reviewService.deleteReview(1L, 1L);

        // Then
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void deleteReview_WrongUser_ThrowsException() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            reviewService.deleteReview(1L, 999L);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getAverageRating_ReturnsCorrectAverage() {
        // Given
        when(reviewRepository.calculateAverageRating(550L)).thenReturn(8.5);

        // When
        Double result = reviewService.getAverageRating(550L);

        // Then
        assertEquals(8.5, result);
    }

    @Test
    void getAverageRating_NoReviews_ReturnsZero() {
        // Given
        when(reviewRepository.calculateAverageRating(550L)).thenReturn(null);

        // When
        Double result = reviewService.getAverageRating(550L);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void markAsHelpful_Success() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewDTO result = reviewService.markAsHelpful(1L);

        // Then
        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}
