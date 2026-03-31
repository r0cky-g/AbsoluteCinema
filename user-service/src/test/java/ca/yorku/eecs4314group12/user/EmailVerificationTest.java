package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import ca.yorku.eecs4314group12.user.service.EmailService;
import ca.yorku.eecs4314group12.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User unverifiedUser;

    @BeforeEach
    void setUp() {
        unverifiedUser = new User("testuser", "test@example.com", "password");
        unverifiedUser.setEmailVerified(false);
        unverifiedUser.setVerificationCode("1234");
    }

    // Registration should send a verification email and set emailVerified=false
    @Test
    void createUser_shouldSendVerificationEmail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User user = new User("newuser", "new@example.com", "password123");
        User result = userService.createUser(user);

        assertFalse(result.isEmailVerified());
        assertNotNull(result.getVerificationCode());
        verify(emailService, times(1)).sendVerificationEmail(eq("new@example.com"), anyString());
    }

    // Correct verification code should verify the email successfully
    @Test
    void verifyEmail_withCorrectCode_shouldSucceed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(unverifiedUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        boolean result = userService.verifyEmail(1L, "1234");

        assertTrue(result);
        assertTrue(unverifiedUser.isEmailVerified());
        assertNull(unverifiedUser.getVerificationCode());
    }

    // Wrong verification code should throw BAD_REQUEST
    @Test
    void verifyEmail_withWrongCode_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(unverifiedUser));

        assertThrows(ResponseStatusException.class, () ->
                userService.verifyEmail(1L, "9999"));
    }

    // Verifying a non-existent user should throw NOT_FOUND
    @Test
    void verifyEmail_userNotFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                userService.verifyEmail(99L, "1234"));
    }

    // Login should fail if email is not verified
    @Test
    void authenticate_withUnverifiedEmail_shouldThrow() {
        unverifiedUser.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(unverifiedUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.authenticate("testuser", "password"));

        assertEquals(401, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Email not verified"));
    }

    // Login should succeed after email is verified
    @Test
    void authenticate_withVerifiedEmail_shouldSucceed() {
        unverifiedUser.setEmailVerified(true);
        unverifiedUser.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(unverifiedUser));

        User result = userService.authenticate("testuser", "password");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
}
