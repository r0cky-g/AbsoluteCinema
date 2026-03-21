package ca.yorku.eecs4314group12.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ca.yorku.eecs4314group12.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username (used for login)
    Optional<User> findByUsername(String username);

    //// Find user by email
    Optional<User> findByEmail(String email);

    // Check if username already exists
    boolean existsByUsername(String username);

    // Check if email already exists
    boolean existsByEmail(String email);
}