package ca.yorku.eecs4314group12.user.service;

import org.springframework.stereotype.Service;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import ca.yorku.eecs4314group12.user.model.User;

import java.util.List;

/**
 * Handles business logic for User operations.
 * Connects Controller and Repository layers.
 */
@Service
public class UserService {

    private final UserRepository repo;

    // Constructor injection
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // Create a new user
    public User createUser(User user) {
        return repo.save(user);
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
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());
        return repo.save(existingUser);
    }

    // Delete user
    public void deleteUser(Long id) {
        User existingUser = getUserById(id);
        repo.delete(existingUser);
    }
}