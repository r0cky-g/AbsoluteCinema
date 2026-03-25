package ca.yorku.eecs4314group12.user.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import ca.yorku.eecs4314group12.user.config.AdminAccountInitializer;
import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import ca.yorku.eecs4314group12.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    public UserService(UserRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    // CREATE USER
    public User createUser(User user) {

        if (AdminAccountInitializer.ADMIN_USERNAME.equalsIgnoreCase(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This username is reserved");
        }

        if (repo.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already registered");
        }

        if (repo.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already taken");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not already set
        if (user.getRole() == null) {
            user.setRole(ca.yorku.eecs4314group12.user.model.Role.USER);
        }

        // Email verification disabled for now
        // Generate 4-digit verification code
        // String code = String.format("%04d", (int) (Math.random() * 10000));
        // user.setVerificationCode(code);
        // user.setEmailVerified(false);
        // Send verification email
        // emailService.sendVerificationEmail(user.getEmail(), code);
        
        // Set email as verified by default (no verification required)
        user.setEmailVerified(true);
        user.setVerificationCode(null);

        return repo.save(user);
    }

    // authenticate (LOGIN)
    public User authenticate(String identifier, String rawPassword) {

        Optional<User> optionalUser = repo.findByUsername(identifier);

        if (optionalUser.isEmpty()) {
            optionalUser = repo.findByEmail(identifier);
        }

        User user = optionalUser
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials");
        }

        // Email verification check disabled for now
        // if (!user.isEmailVerified()) {
        //     throw new ResponseStatusException(
        //             HttpStatus.UNAUTHORIZED,
        //             "Email not verified");
        // }

        return user;
    }

    // verify email
    public boolean verifyEmail(Long userId, String code) {

        User user = repo.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        if (user.getVerificationCode() == null ||
                !user.getVerificationCode().equals(code)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid verification code");
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        repo.save(user);

        return true;
    }

    //get all users
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    //get user by id
    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with id " + id));
    }

    //update user
    public User updateUser(Long id, ca.yorku.eecs4314group12.user.dto.UserUpdateRequest request) {

        User existingUser = getUserById(id);

        if (!existingUser.getEmail().equals(request.getEmail()) &&
                repo.existsByEmail(request.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already registered");
        }

        if (!existingUser.getUsername().equals(request.getUsername()) &&
                repo.existsByUsername(request.getUsername())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already taken");
        }

        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());

        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(request.getPassword()));
        }

        // Update liked genres if provided
        if (request.getLikedGenres() != null) {
            existingUser.setLikedGenres(request.getLikedGenres());
        }

        // Update other fields if provided
        if (request.getDob() != null) {
            existingUser.setDob(request.getDob());
        }
        if (request.getOver18() != null) {
            existingUser.setOver18(request.getOver18());
        }

        return repo.save(existingUser);
    }

    // Delete user
    public void deleteUser(Long id) {

        User existingUser = getUserById(id);
        repo.delete(existingUser);
    }

    /**
     * Updates a user's role. The built-in ADMIN account cannot be demoted from ADMIN.
     */
    public User updateRole(Long userId, String roleName) {
        Role newRole;
        try {
            newRole = Role.valueOf(roleName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid role; use USER, MODERATOR, or ADMIN");
        }

        User user = getUserById(userId);
        if (AdminAccountInitializer.ADMIN_USERNAME.equalsIgnoreCase(user.getUsername())
                && newRole != Role.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "The built-in administrator account cannot be demoted");
        }

        user.setRole(newRole);
        return repo.save(user);
    }
}