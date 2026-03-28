package ca.yorku.eecs4314group12.api.client;

import java.util.Collections;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.exception.ApiException;
import reactor.core.publisher.Mono;

public class BaseWebClient {

    private final WebClient webClient;

    public BaseWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> Mono<ResponseEntity<T>> get(String uri, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {

        return webClient.get()
            .uri(uri, uriVariables)
            .exchangeToMono(response -> {
                HttpStatusCode status = response.statusCode();
                HttpHeaders headers = response.headers().asHttpHeaders();

                if (status.is2xxSuccessful()) {
                    // 2xx → parse as T
                    return response.bodyToMono(typeRef)
                        .map(body -> ResponseEntity.status(status).headers(headers).body(body))
                        .onErrorResume(DecodingException.class, e ->
                            Mono.<ResponseEntity<T>>error(new ApiException(500, "Internal Server Error", headers)))
                        .defaultIfEmpty(ResponseEntity.status(status).headers(headers).body(null));
                } else {
                    // non-2xx → parse body as String for exception
                    return response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), body, headers)))
                        .onErrorResume(err -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), "", headers)));
                }
            });
    }

    public <T> Mono<ResponseEntity<T>> delete(String uri, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {
        return webClient.delete()
            .uri(uri, uriVariables)
            .exchangeToMono(response -> {
                HttpStatusCode status = response.statusCode();
                HttpHeaders headers = response.headers().asHttpHeaders();

                if (status.is2xxSuccessful()) {
                    // 2xx → parse as T
                    return response.bodyToMono(typeRef)
                        .map(body -> ResponseEntity.status(status).headers(headers).body(body))
                        .onErrorResume(DecodingException.class, e ->
                            Mono.<ResponseEntity<T>>error(new ApiException(500, "Internal Server Error", headers)))
                        .defaultIfEmpty(ResponseEntity.status(status).headers(headers).body(null));
                } else {
                    // non-2xx → parse body as String for exception
                    return response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), body, headers)))
                        .onErrorResume(err -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), "", headers)));
                }
            });
    }

    public <T, B> Mono<ResponseEntity<T>> put(String uri, B requestBody, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {
    return webClient.put()
            .uri(uri, uriVariables)
            .bodyValue(requestBody)
            .exchangeToMono(response -> {
                HttpStatusCode status = response.statusCode();
                HttpHeaders headers = response.headers().asHttpHeaders();

                if (status.is2xxSuccessful()) {
                    // 2xx → parse as T
                    return response.bodyToMono(typeRef)
                        .map(body -> ResponseEntity.status(status).headers(headers).body(body))
                        .onErrorResume(DecodingException.class, e ->
                            Mono.<ResponseEntity<T>>error(new ApiException(500, "Internal Server Error", headers)))
                        .defaultIfEmpty(ResponseEntity.status(status).headers(headers).body(null));
                } else {
                    // non-2xx → parse body as String for exception
                    return response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), body, headers)))
                        .onErrorResume(err -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), "", headers)));
                }
            });
    }

    public <T, B> Mono<ResponseEntity<T>> post(String uri, B requestBody, ParameterizedTypeReference<T> typeRef, Object... uriVariables) {
    return webClient.post()
            .uri(uri, uriVariables)
            .bodyValue(requestBody)
            .exchangeToMono(response -> {
                HttpStatusCode status = response.statusCode();
                HttpHeaders headers = response.headers().asHttpHeaders();

                if (status.is2xxSuccessful()) {
                    // 2xx → parse as T
                    return response.bodyToMono(typeRef)
                        .map(body -> ResponseEntity.status(status).headers(headers).body(body))
                        .onErrorResume(DecodingException.class, e ->
                            Mono.<ResponseEntity<T>>error(new ApiException(500, "Internal Server Error", headers)))
                        .defaultIfEmpty(ResponseEntity.status(status).headers(headers).body(null));
                } else {
                    // non-2xx → parse body as String for exception
                    return response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), body, headers)))
                        .onErrorResume(err -> Mono.<ResponseEntity<T>>error(new ApiException(status.value(), "", headers)));
                }
            });
    }
}