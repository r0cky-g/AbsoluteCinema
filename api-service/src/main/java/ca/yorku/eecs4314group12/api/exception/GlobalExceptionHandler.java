package ca.yorku.eecs4314group12.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ca.yorku.eecs4314group12.api.dto.ApiErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getMessage(),
                ex.getTimestamp()
        );
        // Use HttpStatus based on code, or fallback to INTERNAL_SERVER_ERROR
        HttpStatus status = HttpStatus.resolve(ex.getStatus());
        if (status == null || ex.getStatus() < 100 || ex.getStatus() >= 600) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<ApiErrorResponse>(error, status);
    }
}