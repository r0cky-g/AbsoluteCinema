package ca.yorku.eecs4314group12.api.exception;

import java.time.Instant;

public class ApiException extends RuntimeException {
    private final int status;
    private final Instant timestamp;

    public ApiException(int status, String message, Instant timestamp) {
        super(message);
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ApiException{" +
                "status=" + status +
                ", message=" + getMessage() +
                ", timestamp=" + timestamp +
                '}';
    }
}