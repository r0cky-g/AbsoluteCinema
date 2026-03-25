package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import ca.yorku.eecs4314group12.user.service.EmailService;
import ca.yorku.eecs4314group12.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRoleManagementTest {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User admin;
    private User moderatorUser;
    private User normalUser;
    private final String adminPlainPassword = "AdminPass1";

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setUsername("ADMIN");
        admin.setEmail("admin@test.local");
        admin.setPassword(BCRYPT.encode(adminPlainPassword));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);

        moderatorUser = new User();
        moderatorUser.setId(2L);
        moderatorUser.setUsername("moderator_user");
        moderatorUser.setEmail("mod@test.local");
        moderatorUser.setPassword(BCRYPT.encode("modpass12"));
        moderatorUser.setRole(Role.MODERATOR);
        moderatorUser.setEmailVerified(true);

        normalUser = new User();
        normalUser.setId(3L);
        normalUser.setUsername("regular_user");
        normalUser.setEmail("user@test.local");
        normalUser.setPassword(BCRYPT.encode("userpass12"));
        normalUser.setRole(Role.USER);
        normalUser.setEmailVerified(true);
    }

    @Test
    void promoteToModerator_success() {
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(admin));
        when(userRepository.findById(3L)).thenReturn(Optional.of(normalUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User out = userService.promoteToModerator(3L, "ADMIN", adminPlainPassword);

        assertEquals(Role.MODERATOR, out.getRole());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Role.MODERATOR, captor.getValue().getRole());
    }

    @Test
    void promoteToModerator_nonAdminIsForbidden() {
        when(userRepository.findByUsername("moderator_user")).thenReturn(Optional.of(moderatorUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.promoteToModerator(3L, "moderator_user", "modpass12"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void promoteToModerator_cannotChangeAdminRole() {
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.promoteToModerator(1L, "ADMIN", adminPlainPassword));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void demoteModeratorToUser_success() {
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(moderatorUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User out = userService.demoteModeratorToUser(2L, "ADMIN", adminPlainPassword);

        assertEquals(Role.USER, out.getRole());
    }

    @Test
    void demoteModeratorToUser_whenTargetNotModerator_badRequest() {
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(admin));
        when(userRepository.findById(3L)).thenReturn(Optional.of(normalUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.demoteModeratorToUser(3L, "ADMIN", adminPlainPassword));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void demoteModeratorToUser_cannotChangeAdmin() {
        when(userRepository.findByUsername("ADMIN")).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.demoteModeratorToUser(1L, "ADMIN", adminPlainPassword));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }
}
