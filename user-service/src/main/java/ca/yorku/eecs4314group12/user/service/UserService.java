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

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User createUser(User user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        user.setVerificationCode(UUID.randomUUID().toString());
        user.setEmailVerified(false);

        return repo.save(user);
    }

    public User authenticate(String usernameOrEmail, String password) {
        Optional<User> userOpt = repo.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = repo.findByEmail(usernameOrEmail);
        }

        User user = userOpt.orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    public boolean verifyEmail(Long userId, String code) {
        Optional<User> optionalUser = repo.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setEmailVerified(true);
            user.setVerificationCode(null); 
            repo.save(user);
            return true;
        }
        return false;
    }
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id " + id));
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                repo.findByEmail(updatedUser.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (!existingUser.getUsername().equals(updatedUser.getUsername()) &&
                repo.findByUsername(updatedUser.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        return repo.save(existingUser);
    }

    public void deleteUser(Long id) {
        User existingUser = getUserById(id);
        repo.delete(existingUser);
    }
}