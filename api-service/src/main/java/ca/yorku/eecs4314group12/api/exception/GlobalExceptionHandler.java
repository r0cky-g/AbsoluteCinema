package ca.yorku.eecs4314group12.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import ca.yorku.eecs4314group12.api.dto.ApiResponse;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* inbound error handler
     * converts client errors into standardized error response
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<?> handleWebClientException(WebClientResponseException ex) {

        String rawBody = ex.getResponseBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        ApiResponse<?> body;

        try {
            body = mapper.readValue(rawBody, new TypeReference<ApiResponse<?>>() {});
        } catch (Exception e) {
            body = ApiResponse.error(rawBody.isEmpty() ? ex.getStatusText() : rawBody);
        }

        return ResponseEntity
            .status(ex.getStatusCode())
            .body(body);
    }
}
