package ca.yorku.eecs4314group12.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ca.yorku.eecs4314group12.api.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse> handleApiException(ApiException ex) {
        ApiResponse error = new ApiResponse(
                ex.getMessage(),
                ex.getTimestamp()
        );
        HttpStatus status = HttpStatus.resolve(ex.getStatus());
        if (status == null || ex.getStatus() < 100 || ex.getStatus() >= 600) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(error, status);
    }
}