package ca.yorku.eecs4314group12.api.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieDTO;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.LoginRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserRegisterRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserResponseDTO;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserUpdateRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.Watchlist;
import reactor.core.publisher.Mono;

@Component
public class UserClient {

    private final BaseWebClient baseWebClient;

    public UserClient(@Qualifier("APIUserClient") WebClient webClient) {
        this.baseWebClient = new BaseWebClient(webClient);
    }

    public Mono<ResponseEntity<UserResponseDTO>> register(UserRegisterRequest request) {
        return baseWebClient.post("/user/register", request,  new ParameterizedTypeReference<UserResponseDTO>() {});
    }

    public Mono<ResponseEntity<UserResponseDTO>> login(LoginRequest request) {
        return baseWebClient.post("/user/login", request,  new ParameterizedTypeReference<UserResponseDTO>() {});
    }

    // email verification is temporarily desabled
    // public Mono<ResponseEntity<String>> verifyEmail(Long id, String code) {
    //     return baseWebClient.post("/user/{id}/verify?code={code}", null,  new ParameterizedTypeReference<String>() {}, id, code);
    // }

    public Mono<ResponseEntity<List<UserResponseDTO>>> getUsers() {
        return baseWebClient.get("/user", new ParameterizedTypeReference<List<UserResponseDTO>>() {});
    }
    
    public Mono<ResponseEntity<UserResponseDTO>> getUserById(Long id) {
        return baseWebClient.get("/user/{id}", new ParameterizedTypeReference<UserResponseDTO>() {}, id);
    }

    public Mono<ResponseEntity<UserResponseDTO>> updateUser(Long id, UserUpdateRequest request) {
        return baseWebClient.put("/user/{id}", request, new ParameterizedTypeReference<UserResponseDTO>() {}, id);
    }

    // delete should return some message
    public Mono<ResponseEntity<Void>> deleteUser(Long id) {
        return baseWebClient.delete("/user/{id}", null, id);
    }

    public Mono<ResponseEntity<Watchlist>> addToWatchlist(Long userId, Integer movieId) {
        return baseWebClient.post("/user/{userId}/watchlist/{movieId}", null, null, userId, movieId);
    }

    public Mono<ResponseEntity<Void>> removeFromWatchlist(Long userId, Integer movieId) {
        return baseWebClient.delete("/user/{userId}/watchlist/{movieId}", null, userId, movieId);
    }

    public Mono<ResponseEntity<List<Watchlist>>> getUserWatchlist(Long userId) {
        return baseWebClient.get("/user/{userId}/watchlist", new ParameterizedTypeReference<List<Watchlist>>() {}, userId);
    }

    public Mono<ResponseEntity<Boolean>> isInWatchlist(Long userId, Integer movieId) {
        return baseWebClient.get("/user/{userId}/watchlist/{movieId}", new ParameterizedTypeReference<Boolean>() {}, userId, movieId);
    }

    public Mono<ResponseEntity<List<MovieDTO>>> getRecommendedMovies(Long userId) {
        return baseWebClient.get("/user/{userId}/recommendations", new ParameterizedTypeReference<List<MovieDTO>>() {}, userId);
    }
}
