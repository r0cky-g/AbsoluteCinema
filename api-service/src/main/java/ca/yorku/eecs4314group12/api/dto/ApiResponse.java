package ca.yorku.eecs4314group12.api.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApiResponse {

    private String message;
   
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant timestamp;

    public ApiResponse() {
        this.timestamp = Instant.now();
    }

    public ApiResponse(String message) {
        this.message = message;
        this.timestamp = Instant.now();
    }

    public ApiResponse(String message, Instant timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}