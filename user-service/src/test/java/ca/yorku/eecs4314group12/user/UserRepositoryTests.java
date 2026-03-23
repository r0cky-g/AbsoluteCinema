package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAndFindUser() {
        User user = new User("alicesmith", "alice@example.com", "password123");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("alicesmith");
        assertTrue(found.isPresent());
        assertEquals("alicesmith", found.get().getUsername());
        assertEquals("alice@example.com", found.get().getEmail());

        Optional<User> byEmail = userRepository.findByEmail("alice@example.com");
        assertTrue(byEmail.isPresent());
    }

    @Test
    void testDeleteUser() {
        User user = new User("bobjohnson", "bob@example.com", "secret123");
        userRepository.save(user);

        // Delete user
        userRepository.delete(user);

        // Verify deletion
        Optional<User> found = userRepository.findByUsername("bobjohnson");
        assertFalse(found.isPresent());
    }
}