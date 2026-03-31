package ca.yorku.eecs4314group12.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.model.Watchlist;
import ca.yorku.eecs4314group12.user.service.UserService;
import ca.yorku.eecs4314group12.user.service.WatchlistService;
import ca.yorku.eecs4314group12.user.service.RecommendationService;
import ca.yorku.eecs4314group12.user.service.WatchHistoryService;
import ca.yorku.eecs4314group12.user.model.WatchHistory;
import ca.yorku.eecs4314group12.user.service.FavouriteMovieService;
import ca.yorku.eecs4314group12.user.model.FavouriteMovie;
import ca.yorku.eecs4314group12.user.dto.LoginRequest;
import ca.yorku.eecs4314group12.user.dto.AdminActorRequest;
import ca.yorku.eecs4314group12.user.dto.UserRegisterRequest;
import ca.yorku.eecs4314group12.user.dto.UserUpdateRequest;
import ca.yorku.eecs4314group12.user.dto.UserResponseDTO;
import ca.yorku.eecs4314group12.user.dto.MovieDTO;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;
    private final WatchlistService watchlistService;
    private final RecommendationService recommendationService;
    private final WatchHistoryService watchHistoryService;
    private final FavouriteMovieService favouriteMovieService;

    public UserController(UserService service, WatchlistService watchlistService,
                         RecommendationService recommendationService,
                         WatchHistoryService watchHistoryService,
                         FavouriteMovieService favouriteMovieService) {
        this.service = service;
        this.watchlistService = watchlistService;
        this.recommendationService = recommendationService;
        this.watchHistoryService = watchHistoryService;
        this.favouriteMovieService = favouriteMovieService;
    }

    // register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody UserRegisterRequest request) {

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword());

        user.setOver18(request.isOver18());

        User createdUser = service.createUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDTO(createdUser));
    }

    // login
    @PostMapping("/login")
    public UserResponseDTO login(@Valid @RequestBody LoginRequest request) {

        User user = service.authenticate(
                request.getIdentifier(),
                request.getPassword());

        return toDTO(user);
    }

    // verify email
    @PostMapping("/{id}/verify")
    public ResponseEntity<String> verifyEmail(
            @PathVariable Long id,
            @RequestParam String code) {

        boolean success = service.verifyEmail(id, code);

        if (success) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid verification code");
        }
    }

    // get all users
    @GetMapping
    public List<UserResponseDTO> getUsers() {

        return service.getAllUsers()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // get user by id
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {

        User user = service.getUserById(id);

        return toDTO(user);
    }

    // update user
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        User updatedUser = service.updateUser(id, request);

        return toDTO(updatedUser);
    }

    // delete user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }

    /**
     * Promote a user to MODERATOR. Only a user with role ADMIN may call this (identified by body).
     */
    @PostMapping("/{userId}/promote-moderator")
    public ResponseEntity<UserResponseDTO> promoteModerator(
            @PathVariable Long userId,
            @Valid @RequestBody AdminActorRequest actor) {
        User updated = service.promoteToModerator(userId, actor.getAdminIdentifier());
        return ResponseEntity.ok(toDTO(updated));
    }

    /**
     * Demote a MODERATOR back to USER. Only an ADMIN may call this.
     */
    @PostMapping("/{userId}/demote-moderator")
    public ResponseEntity<UserResponseDTO> demoteModerator(
            @PathVariable Long userId,
            @Valid @RequestBody AdminActorRequest actor) {
        User updated = service.demoteModeratorToUser(userId, actor.getAdminIdentifier());
        return ResponseEntity.ok(toDTO(updated));
    }

    // Watchlist endpoints
    @PostMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<Watchlist> addToWatchlist(
            @PathVariable Long userId,
            @PathVariable Integer movieId) {
        Watchlist watchlist = watchlistService.addToWatchlist(userId, movieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(watchlist);
    }

    @DeleteMapping("/{userId}/watchlist/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWatchlist(
            @PathVariable Long userId,
            @PathVariable Integer movieId) {
        watchlistService.removeFromWatchlist(userId, movieId);
    }

    @GetMapping("/{userId}/watchlist")
    public List<Watchlist> getUserWatchlist(@PathVariable Long userId) {
        return watchlistService.getUserWatchlist(userId);
    }

    @GetMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<Boolean> isInWatchlist(
            @PathVariable Long userId,
            @PathVariable Integer movieId) {
        boolean inWatchlist = watchlistService.isInWatchlist(userId, movieId);
        return ResponseEntity.ok(inWatchlist);
    }

    // Recommendations endpoint
    @GetMapping("/{userId}/recommendations")
    public List<MovieDTO> getRecommendations(@PathVariable Long userId) {
        return recommendationService.getRecommendedMovies(userId);
    }

    // Watch history endpoints
    @PostMapping("/{userId}/history/{movieId}")
    public ResponseEntity<WatchHistory> addToHistory(
            @PathVariable Long userId,
            @PathVariable Integer movieId) {
        WatchHistory entry = watchHistoryService.addToHistory(userId, movieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @GetMapping("/{userId}/history")
    public List<WatchHistory> getUserHistory(@PathVariable Long userId) {
        return watchHistoryService.getUserHistory(userId);
    }

    @DeleteMapping("/{userId}/history/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromHistory(
            @PathVariable Long userId,
            @PathVariable Integer movieId) {
        watchHistoryService.removeFromHistory(userId, movieId);
    }

    // Favourite movies endpoints
    @PostMapping("/{userId}/favourites/{movieId}")
    public ResponseEntity<FavouriteMovie> addFavourite(
            @PathVariable Long userId,
            @PathVariable Integer movieId) {
        FavouriteMovie entry = favouriteMovieService.addFavourite(userId, movieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    // Get all favourite movies for a user
    @GetMapping("/{userId}/favourites")
    public List<FavouriteMovie> getUserFavourites(@PathVariable Long userId) {
        return favouriteMovieService.getUserFavourites(userId);
    }

    // Remove a movie from favourites
    @DeleteMapping("/{userId}/favourites/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavourite(
            @PathVariable Long userId,
            @PathVariable Integer movieId) {
        favouriteMovieService.removeFavourite(userId, movieId);
    }

    // private mapper
    /**
     * Converts a User entity to a UserResponseDTO.
     * This method ensures that sensitive information such as password and
     * verificationCode is NOT displayed to the client.
     *
     * The DTO is used to control exactly what data is returned in API responses.
     *
     * @param user the User entity retrieved from the database
     * @return a UserResponseDTO containing safe and displayable fields
     */
    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getRole() != null ? user.getRole().name() : "USER",
                user.getLikedGenres());
    }
}