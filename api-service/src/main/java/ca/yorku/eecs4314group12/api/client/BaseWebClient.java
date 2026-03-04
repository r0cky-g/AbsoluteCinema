package ca.yorku.eecs4314group12.api.client;

import java.time.Instant;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.ApiResponse;
import ca.yorku.eecs4314group12.api.exception.ApiException;
import reactor.core.publisher.Mono;

public class BaseWebClient {

    private final WebClient webClient;

    public BaseWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> Mono<T> get(String uri, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {
        return webClient.get()
                .uri(uri, uriVariables)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        // 2xx: parse as T
                        return response.bodyToMono(typeRef)
                                .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                                );
                    } else {
                        // non-2xx: parse as ErrorResponse
                        return response.bodyToMono(ApiResponse.class)
                                .flatMap(error -> Mono.<T>error(new ApiException(
                                        response.statusCode().value(),
                                        error.getMessage(),
                                        error.getTimestamp()
                                )))
                                .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                                );
                    }
                });
    }

    public <T> Mono<T> delete(String uri, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {
        return webClient.get()
                .uri(uri, uriVariables)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        // 2xx: parse as T
                        return response.bodyToMono(typeRef)
                                .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                                );
                    } else {
                        // non-2xx: parse as ErrorResponse
                        return response.bodyToMono(ApiResponse.class)
                                .flatMap(error -> Mono.<T>error(new ApiException(
                                        response.statusCode().value(),
                                        error.getMessage(),
                                        error.getTimestamp()
                                )))
                                .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                                );
                    }
                });
    }

    public <T, B> Mono<T> put(String uri, B requestBody, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {
    return webClient.put()
            .uri(uri, uriVariables)
            .bodyValue(requestBody)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    // 2xx: parse as T
                    return response.bodyToMono(typeRef)
                            .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                            );
                } else {
                    // non-2xx: parse as ApiErrorResponse
                    return response.bodyToMono(ApiResponse.class)
                            .flatMap(error -> Mono.<T>error(new ApiException(
                                    response.statusCode().value(),
                                    error.getMessage(),
                                    error.getTimestamp()
                            )))
                            .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                            );
                }
            });
    }

    public <T, B> Mono<T> post(String uri, B requestBody, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {
    return webClient.post()
            .uri(uri, uriVariables)
            .bodyValue(requestBody)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    // 2xx: parse as T
                    return response.bodyToMono(typeRef)
                            .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                            );
                } else {
                    // non-2xx: parse as ApiErrorResponse
                    return response.bodyToMono(ApiResponse.class)
                            .flatMap(error -> Mono.<T>error(new ApiException(
                                    response.statusCode().value(),
                                    error.getMessage(),
                                    error.getTimestamp()
                            )))
                            .onErrorResume(DecodingException.class, e ->
                                    Mono.error(new ApiException(500, "Internal Server Error", Instant.now()))
                            );
                }
            });
    }
}