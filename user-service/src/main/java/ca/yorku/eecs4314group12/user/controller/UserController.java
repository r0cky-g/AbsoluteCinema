package ca.yorku.eecs4314group12.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.service.UserService;
import ca.yorku.eecs4314group12.user.dto.LoginRequest;
import ca.yorku.eecs4314group12.user.dto.UserRegisterRequest;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    //Register
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Valid @RequestBody UserRegisterRequest request) {

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        user.setOver18(request.isOver18());

        User createdUser = service.createUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public User login(@Valid @RequestBody LoginRequest request) {
        return service.authenticate(
                request.getIdentifier(),
                request.getPassword()
        );
    }

    // ================= VERIFY EMAIL =================
    @PostMapping("/{id}/verify")
    public String verifyEmail(@PathVariable Long id,
                              @RequestParam String code) {

        boolean success = service.verifyEmail(id, code);

        if (success) {
            return "Email verified successfully";
        } else {
            return "Invalid verification code";
        }
    }

    //get all users
    @GetMapping
    public List<User> getUsers() {
        return service.getAllUsers();
    }

    //get user by id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    //update user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,
                           @Valid @RequestBody User user) {
        return service.updateUser(id, user);
    }

    //delete user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}