package ca.yorku.eecs4314group12.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.service.UserService;
import ca.yorku.eecs4314group12.user.dto.LoginRequest;
import ca.yorku.eecs4314group12.user.dto.UserRegisterRequest;
import ca.yorku.eecs4314group12.user.dto.UserResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
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
            @Valid @RequestBody User user) {

        User updatedUser = service.updateUser(id, user);

        return toDTO(updatedUser);
    }

    // delete user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
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
                user.getRole().name());
    }
}