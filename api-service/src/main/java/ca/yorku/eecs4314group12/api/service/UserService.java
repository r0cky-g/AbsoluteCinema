package ca.yorku.eecs4314group12.api.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ca.yorku.eecs4314group12.api.client.UserClient;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieDTO;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.FavouriteMovie;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.LoginRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserRegisterRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserResponseDTO;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserUpdateRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.WatchHistory;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.Watchlist;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    
    private final UserClient userClient;

    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public Mono<ResponseEntity<UserResponseDTO>> register(UserRegisterRequest request) {
        return userClient.register(request);
    }

    public Mono<ResponseEntity<UserResponseDTO>> login(LoginRequest request) {
        return userClient.login(request);
    }

    public Mono<ResponseEntity<String>> verifyEmail(Long id, String code) {
        return userClient.verifyEmail(id, code);
    }

    public Mono<ResponseEntity<List<UserResponseDTO>>> getUsers() {
        return userClient.getUsers();
    }
    
    public Mono<ResponseEntity<UserResponseDTO>> getUserById(Long id) {
        return userClient.getUserById(id);
    }

    public Mono<ResponseEntity<UserResponseDTO>> updateUser(Long id, UserUpdateRequest request) {
        return userClient.updateUser(id, request);
    }

    // delete should return some message
    public Mono<ResponseEntity<Void>> deleteUser(Long id) {
        return userClient.deleteUser(id);
    }

    public Mono<ResponseEntity<Watchlist>> addToWatchlist(Long userId, Integer movieId) {
        return userClient.addToWatchlist(userId, movieId);
    }

    public Mono<ResponseEntity<Void>> removeFromWatchlist(Long userId, Integer movieId) {
        return userClient.removeFromWatchlist(userId, movieId);
    }

    public Mono<ResponseEntity<List<Watchlist>>> getUserWatchlist(Long userId) {
        return userClient.getUserWatchlist(userId);
    }

    public Mono<ResponseEntity<Boolean>> isInWatchlist(Long userId, Integer movieId) {
        return userClient.isInWatchlist(userId, movieId);
    }

    public Mono<ResponseEntity<List<MovieDTO>>> getRecommendedMovies(Long userId) {
        return userClient.getRecommendedMovies(userId);
    }

    public Mono<ResponseEntity<WatchHistory>> addToHistory(Long userId, Integer movieId) {
        return userClient.addToHistory(userId, movieId);
    }

    public Mono<ResponseEntity<List<WatchHistory>>> getUserHistory(Long userId) {
        return userClient.getUserHistory(userId);
    }

    public Mono<ResponseEntity<Void>> removeFromHistory(Long userId, Integer movieId) {
        return userClient.removeFromHistory(userId, movieId);
    }

    public Mono<ResponseEntity<FavouriteMovie>> addFavourite(Long userId, Integer movieId) {
        return userClient.addFavourite(userId, movieId);
    }

    public Mono<ResponseEntity<List<FavouriteMovie>>> getUserFavourites(Long userId) {
        return userClient.getUserFavourites(userId);
    }

    public Mono<ResponseEntity<Void>> removeFavourite(Long userId, Integer movieId) {
        return userClient.removeFavourite(userId, movieId);
    }
}
