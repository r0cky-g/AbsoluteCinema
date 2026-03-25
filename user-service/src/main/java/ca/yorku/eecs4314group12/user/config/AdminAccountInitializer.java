package ca.yorku.eecs4314group12.user.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;

/**
 * Ensures the single built-in administrator account exists (username ADMIN, password ADMIN).
 */
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    public static final String ADMIN_USERNAME = "ADMIN";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAccountInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
            return;
        }
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail("admin@absolute-cinema.local");
        admin.setPassword(passwordEncoder.encode("ADMIN"));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin.setOver18(true);
        userRepository.save(admin);
    }
}
