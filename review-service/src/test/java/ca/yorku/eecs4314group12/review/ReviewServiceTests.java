package ca.yorku.eecs4314group12.review;

import ca.yorku.eecs4314group12.review.dto.ReviewDTO;
import ca.yorku.eecs4314group12.review.model.Review;
import ca.yorku.eecs4314group12.review.repository.ReviewRepository;
import ca.yorku.eecs4314group12.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewDTO testReviewDTO;
    private Review testReview;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
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
    void testCreateReview_Success() {
        when(reviewRepository.existsByUserIdAndMovieIdAndIsDeletedFalse(1L, 550L))
                .thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewDTO result = reviewService.createReview(testReviewDTO);

        assertNotNull(result);
        assertEquals(9, result.getRating());
        assertEquals("Great movie", result.getTitle());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReview_DuplicateReview_ThrowsException() {
        when(reviewRepository.existsByUserIdAndMovieIdAndIsDeletedFalse(1L, 550L))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            reviewService.createReview(testReviewDTO);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testGetReviewsByMovie_ReturnsReviews() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByMovieIdAndIsDeletedFalseOrderByCreatedAtDesc(550L))
                .thenReturn(reviews);

        List<ReviewDTO> result = reviewService.getReviewsByMovie(550L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(9, result.get(0).getRating());
    }

    @Test
    void testGetReviewById_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        ReviewDTO result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(9, result.getRating());
    }

    @Test
    void testGetReviewById_NotFound_ThrowsException() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getReviewById(999L);
        });
    }

    @Test
    void testUpdateReview_Success() {
        ReviewDTO updateDTO = new ReviewDTO();
        updateDTO.setUserId(1L);
        updateDTO.setRating(10);
        updateDTO.setTitle("Even better on rewatch");
        updateDTO.setContent("Masterpiece!");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewDTO result = reviewService.updateReview(1L, updateDTO);

        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testUpdateReview_WrongUser_ThrowsException() {
        ReviewDTO updateDTO = new ReviewDTO();
        updateDTO.setUserId(999L);
        updateDTO.setRating(10);
        updateDTO.setTitle("Hacking attempt");
        updateDTO.setContent("Should fail!");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        assertThrows(IllegalStateException.class, () -> {
            reviewService.updateReview(1L, updateDTO);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        reviewService.deleteReview(1L, 1L, "USER");

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testDeleteReview_WrongUser_ThrowsException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        assertThrows(IllegalStateException.class, () -> {
            reviewService.deleteReview(1L, 999L, "USER");
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testDeleteReview_ModeratorCannotDeleteOthersReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        assertThrows(IllegalStateException.class, () ->
                reviewService.deleteReview(1L, 999L, "MODERATOR"));

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testDeleteReview_ModeratorCanDeleteOwnReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        reviewService.deleteReview(1L, 1L, "MODERATOR");

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testDeleteReview_AdminCanDeleteAnyReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        reviewService.deleteReview(1L, 999L, "ADMIN");

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testGetAverageRating_ReturnsCorrectAverage() {
        when(reviewRepository.calculateAverageRating(550L)).thenReturn(8.5);

        Double result = reviewService.getAverageRating(550L);

        assertEquals(8.5, result);
    }

    @Test
    void testGetAverageRating_NoReviews_ReturnsZero() {
        when(reviewRepository.calculateAverageRating(550L)).thenReturn(null);

        Double result = reviewService.getAverageRating(550L);

        assertEquals(0.0, result);
    }

    @Test
    void testMarkAsHelpful_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewDTO result = reviewService.markAsHelpful(1L);

        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}
