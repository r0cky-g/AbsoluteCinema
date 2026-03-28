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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAccountSeederTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminAccountSeeder seeder;

    @Test
    void run_doesNothingWhenAdminAlreadyExists() {
        when(userRepository.existsByUsername(AdminAccountSeeder.ADMIN_USERNAME)).thenReturn(true);

        seeder.run(new DefaultApplicationArguments(new String[0]));

        verify(userRepository, never()).save(any());
    }

    @Test
    void run_createsAdminWhenMissing() {
        when(userRepository.existsByUsername(AdminAccountSeeder.ADMIN_USERNAME)).thenReturn(false);

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
        assertTrue(saved.getPassword().startsWith("$2a$") || saved.getPassword().startsWith("$2b$"),
                "Password should be BCrypt-encoded");
    }
}
