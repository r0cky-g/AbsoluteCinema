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
import ca.yorku.eecs4314group12.user.dto.LoginRequest;
import ca.yorku.eecs4314group12.user.dto.UserRegisterRequest;
import ca.yorku.eecs4314group12.user.dto.UserRoleUpdateRequest;
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

    public UserController(UserService service, WatchlistService watchlistService, 
                         RecommendationService recommendationService) {
        this.service = service;
        this.watchlistService = watchlistService;
        this.recommendationService = recommendationService;
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

    // Email verification endpoint disabled for now
    // verify email
    // @PostMapping("/{id}/verify")
    // public ResponseEntity<String> verifyEmail(
    //         @PathVariable Long id,
    //         @RequestParam String code) {
    //
    //     boolean success = service.verifyEmail(id, code);
    //
    //     if (success) {
    //         return ResponseEntity.ok("Email verified successfully");
    //     } else {
    //         return ResponseEntity
    //                 .status(HttpStatus.BAD_REQUEST)
    //                 .body("Invalid verification code");
    //     }
    // }

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

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequest request) {
        User updated = service.updateRole(id, request.getRole());
        return ResponseEntity.ok(toDTO(updated));
    }

    // delete user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
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