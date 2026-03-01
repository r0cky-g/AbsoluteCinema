package ca.yorku.eecs4314group12.api.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.ApiResponse;
import reactor.core.publisher.Mono;

@Component
public class BaseWebClient {

    private final WebClient webClient;

    public BaseWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Send a GET request and return a response in the form of Mono<ApiResponse<?>>
     *
     * @param uri The endpoint URI
     * @param typeRef ParameterizedTypeReference for the expected ApiResponse type
     * @param <T> The type inside ApiResponse
     * @return Mono<ApiResponse<T>> — guaranteed to return an ApiResponse even if the server returned invalid JSON
     */
    public <T> Mono<ApiResponse<T>> get(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(typeRef)
                .onErrorResume(DecodingException.class, e -> {
                    return Mono.just(ApiResponse.error("Invalid response format"));
                });
    }

    public <T, B> Mono<ApiResponse<T>> put(String uri, B body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
    return webClient.put()
            .uri(uri)
            .bodyValue(body)  // The object you are sending
            .retrieve()
            .bodyToMono(typeRef)
            .onErrorResume(DecodingException.class, e -> {
                return Mono.just(ApiResponse.error("Invalid response format"));
            });
}
}