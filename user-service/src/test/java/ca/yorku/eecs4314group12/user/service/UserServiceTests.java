package ca.yorku.eecs4314group12.user.service;

import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@Transactional
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void testAuthenticateWithUsername() {

        User user = new User("loginuser", "loginuser@example.com", encoder.encode("secret123"));
        user.setEmailVerified(true);
        userRepository.save(user);

        User authenticated = userService.authenticate("loginuser", "secret123");

        Assertions.assertNotNull(authenticated);
        Assertions.assertEquals("loginuser", authenticated.getUsername());
    }

    @Test
    public void testAuthenticateWithEmail() {

        User user = new User("loginuser2", "loginuser2@example.com", encoder.encode("secret234"));
        user.setEmailVerified(true);
        userRepository.save(user);

        User authenticated = userService.authenticate("loginuser2@example.com", "secret234");

        Assertions.assertNotNull(authenticated);
        Assertions.assertEquals("loginuser2@example.com", authenticated.getEmail());
    }

    @Test
    public void testAuthenticateInvalidPassword() {

        User user = new User("loginuser3", "loginuser3@example.com", encoder.encode("rightpass1"));
        user.setEmailVerified(true);
        userRepository.save(user);

        Assertions.assertThrows(RuntimeException.class, () ->
                userService.authenticate("loginuser3", "wrongpass"));
    }

    @Test
    public void testLikedGenresPersistence() {

        User user = new User("genreuser", "genreuser@example.com", "password1");
        user.setEmailVerified(true);

        Set<String> genres = new HashSet<>();
        genres.add("ACTION");
        genres.add("COMEDY");
        user.setLikedGenres(genres);

        User saved = userRepository.save(user);

        User found = userRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Assertions.assertEquals(2, found.getLikedGenres().size());
        Assertions.assertTrue(found.getLikedGenres().contains("ACTION"));
        Assertions.assertTrue(found.getLikedGenres().contains("COMEDY"));
    }
}