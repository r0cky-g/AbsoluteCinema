package ca.yorku.eecs4314group12.api.dto.reviewServiceDTO;

import java.time.LocalDateTime;

/**
 * Standard API Response wrapper
 * Provides consistent response structure across all endpoints
 */
public class ReviewApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    private String error;
}