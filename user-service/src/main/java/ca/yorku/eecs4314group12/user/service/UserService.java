package ca.yorku.eecs4314group12.user.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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

        // Generate 4-digit verification code
        String code = String.format("%04d", (int) (Math.random() * 10000));
        user.setVerificationCode(code);
        user.setEmailVerified(false);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), code);

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

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email not verified");
        }

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
    public User updateUser(Long id, User updatedUser) {

        User existingUser = getUserById(id);

        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                repo.existsByEmail(updatedUser.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already registered");
        }

        if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                repo.existsByUsername(updatedUser.getUsername())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already taken");
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword()));
        }

        return repo.save(existingUser);
    }

    // Delete user
    public void deleteUser(Long id) {

        User existingUser = getUserById(id);
        repo.delete(existingUser);
    }
}