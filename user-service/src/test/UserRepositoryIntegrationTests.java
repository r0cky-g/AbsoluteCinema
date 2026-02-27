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
public class UserRepositoryIntegrationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndFindUser() {
        User user = new User("testuser", "testuser@example.com", "testpass");
        userRepository.save(user);

        Optional<User> byUsername = userRepository.findByUsername("testuser");
        assertTrue(byUsername.isPresent());
        assertEquals("testuser", byUsername.get().getUsername());
        assertEquals("testpass", byUsername.get().getPassword());

        Optional<User> byEmail = userRepository.findByEmail("testuser@example.com");
        assertTrue(byEmail.isPresent());
        assertEquals("testuser@example.com", byEmail.get().getEmail());
    }

    @Test
    public void testDeleteUser() {
        User user = new User("todelete", "todelete@example.com", "12345");
        userRepository.save(user);

        assertTrue(userRepository.findByUsername("todelete").isPresent());
        assertTrue(userRepository.findByEmail("todelete@example.com").isPresent());

        userRepository.delete(user);

        assertTrue(userRepository.findByUsername("todelete").isEmpty());
        assertTrue(userRepository.findByEmail("todelete@example.com").isEmpty());
    }
}