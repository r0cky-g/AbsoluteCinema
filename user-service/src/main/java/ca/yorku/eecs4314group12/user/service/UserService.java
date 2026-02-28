package ca.yorku.eecs4314group12.user.service;

import org.springframework.stereotype.Service;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import ca.yorku.eecs4314group12.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository repo;

    // Constructor injection
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // Create a new user
    // check if eamil or username already exists
    // generates a unique verification code to email.
    public User createUser(User user) {
        // Check duplicate email
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        // Check duplicate username
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Set email verification
        user.setVerificationCode(UUID.randomUUID().toString());
        user.setEmailVerified(false);

        return repo.save(user);
    }


    // Verify user's email using the verification code
    public boolean verifyEmail(Long userId, String code) {
        Optional<User> optionalUser = repo.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setEmailVerified(true);
            // clear code after verification
            user.setVerificationCode(null); 
            repo.save(user);
            return true;
        }
        return false;
    }
    // Get all users
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    // Get user by ID
    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id " + id));
    }

    // Update existing user
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        // Check if updating email to an existing one
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                repo.findByEmail(updatedUser.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check if updating username to an existing one
        if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                repo.findByUsername(updatedUser.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        return repo.save(existingUser);
    }

    // Delete user
    public void deleteUser(Long id) {
        User existingUser = getUserById(id);
        repo.delete(existingUser);
    }
}