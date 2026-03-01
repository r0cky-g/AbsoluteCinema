package ca.yorku.eecs4314group12.api.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* outbound error handler
     * standardized error response
     * allows sending json with error response
     *
     * example:
     * catch (WebClientResponseException ex) {
     *     throw new ResponseStatusException(ex.getStatusCode(), "error message");
     * }
     * 
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> WebServiceException(ResponseStatusException ex) {

        System.out.println("service error: " + ex.getStatusCode() + " " + ex.getMessage());

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "status", ex.getStatusCode().value(),
                        "error", ex.getReason()
                ));
    }

    /* inbound error handler
     * converts client errors into standardized error response
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> WebClientException(WebClientResponseException ex) {
        
        System.out.println("webClient error: " + ex.getStatusCode() + " " + ex.getMessage());
        
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "status", ex.getStatusCode().value(),
                        "error", ex.getResponseBodyAsString().isEmpty() ? ex.getStatusText() : ex.getResponseBodyAsString()
                ));
    }

    /* generic server error response
     * standardized error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> GenericException(Exception ex) {

        System.out.println("Generic exception: " + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", 500,
                        "error", "Internal server error"
                ));
    }
}
