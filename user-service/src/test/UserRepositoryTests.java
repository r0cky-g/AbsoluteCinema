package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAndFindUser() {
        // Create user with username, password, email
        User user = new User("alice", "password123", "alice@example.com");
        userRepository.save(user);

        // Find by username
        Optional<User> found = userRepository.findByUsername("alice");
        assertTrue(found.isPresent());
        assertEquals("alice", found.get().getUsername());
        assertEquals("alice@example.com", found.get().getEmail());

        // Find by email
        Optional<User> byEmail = userRepository.findByEmail("alice@example.com");
        assertTrue(byEmail.isPresent());
    }

    @Test
    void testDeleteUser() {
        User user = new User("bob", "secret", "bob@example.com");
        userRepository.save(user);

        // Delete user
        userRepository.delete(user);

        // Verify deletion
        Optional<User> found = userRepository.findByUsername("bob");
        assertFalse(found.isPresent());
    }
}