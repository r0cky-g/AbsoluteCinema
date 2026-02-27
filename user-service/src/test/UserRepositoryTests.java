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
        User user = new User("alice", "password123");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("alice");
        assertTrue(found.isPresent(), "User should be found by username");
        assertEquals("alice", found.get().getUsername());

        Optional<User> notFound = userRepository.findByEmail("alice@example.com");
        assertFalse(notFound.isPresent(), "User with this email should not exist");
    }

    @Test
    void testDeleteUser() {
        User user = new User("bob", "secret");
        userRepository.save(user);

        userRepository.delete(user);

        Optional<User> found = userRepository.findByUsername("bob");
        assertFalse(found.isPresent(), "User should be deleted");
    }
}