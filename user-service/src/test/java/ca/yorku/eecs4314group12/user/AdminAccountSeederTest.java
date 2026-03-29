package ca.yorku.eecs4314group12.user;

import ca.yorku.eecs4314group12.user.bootstrap.AdminAccountSeeder;
import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAccountSeederTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminAccountSeeder seeder;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void run_skipsSaveWhenAdminAlreadyCorrect() {
        User admin = new User();
        admin.setUsername(AdminAccountSeeder.ADMIN_USERNAME);
        admin.setEmail(AdminAccountSeeder.ADMIN_EMAIL);
        admin.setPassword(encoder.encode(AdminAccountSeeder.ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin.setOver18(true);
        admin.setVerificationCode(null);

        when(userRepository.findByUsername(AdminAccountSeeder.ADMIN_USERNAME)).thenReturn(Optional.of(admin));

        seeder.run(new DefaultApplicationArguments(new String[0]));

        verify(userRepository, never()).save(any());
    }

    @Test
    void run_repairsAdminWhenPasswordWrong() {
        User admin = new User();
        admin.setUsername(AdminAccountSeeder.ADMIN_USERNAME);
        admin.setEmail(AdminAccountSeeder.ADMIN_EMAIL);
        admin.setPassword("plaintext-or-wrong");
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin.setOver18(true);

        when(userRepository.findByUsername(AdminAccountSeeder.ADMIN_USERNAME)).thenReturn(Optional.of(admin));

        seeder.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertTrue(encoder.matches(AdminAccountSeeder.ADMIN_PASSWORD, captor.getValue().getPassword()));
    }

    @Test
    void run_repairsAdminWhenNotVerified() {
        User admin = new User();
        admin.setUsername(AdminAccountSeeder.ADMIN_USERNAME);
        admin.setEmail(AdminAccountSeeder.ADMIN_EMAIL);
        admin.setPassword(encoder.encode(AdminAccountSeeder.ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(false);
        admin.setOver18(true);

        when(userRepository.findByUsername(AdminAccountSeeder.ADMIN_USERNAME)).thenReturn(Optional.of(admin));

        seeder.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertTrue(captor.getValue().isEmailVerified());
    }

    @Test
    void run_createsAdminWhenMissing() {
        when(userRepository.findByUsername(AdminAccountSeeder.ADMIN_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(AdminAccountSeeder.ADMIN_EMAIL)).thenReturn(false);

        seeder.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals(AdminAccountSeeder.ADMIN_USERNAME, saved.getUsername());
        assertEquals(AdminAccountSeeder.ADMIN_EMAIL, saved.getEmail());
        assertEquals(Role.ADMIN, saved.getRole());
        assertTrue(saved.isEmailVerified());
        assertTrue(saved.isOver18());
        assertNotNull(saved.getPassword());
        assertTrue(encoder.matches(AdminAccountSeeder.ADMIN_PASSWORD, saved.getPassword()));
    }

    @Test
    void run_skipsCreateWhenEmailTakenBySomeoneElse() {
        when(userRepository.findByUsername(AdminAccountSeeder.ADMIN_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(AdminAccountSeeder.ADMIN_EMAIL)).thenReturn(true);

        seeder.run(new DefaultApplicationArguments(new String[0]));

        verify(userRepository, never()).save(any());
    }
}
