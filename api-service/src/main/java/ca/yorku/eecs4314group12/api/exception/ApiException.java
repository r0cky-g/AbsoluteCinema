package ca.yorku.eecs4314group12.api.exception;

import org.springframework.http.HttpHeaders;

public class ApiException extends RuntimeException {

    private final int status;
    private final HttpHeaders headers;

    public ApiException(int status, String message, HttpHeaders headers) {
        super(message);
        this.status = status;
        this.headers = headers;
    }

    public int getStatus() { 
        return status;
    }
    public HttpHeaders getHeaders() { 
        return headers;
    }
}