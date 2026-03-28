package ca.yorku.eecs4314group12.user.bootstrap;

import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures a single platform administrator exists so moderation and role management can be delegated.
 * Credentials: username {@code ADMIN}, password {@code ADMIN}. Change password after first deploy in production.
 */
@Component
@Order(0)
public class AdminAccountSeeder implements ApplicationRunner {

    public static final String ADMIN_USERNAME = "ADMIN";
    public static final String ADMIN_EMAIL = "admin@absolutecinema.internal";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAccountSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(ADMIN_USERNAME)) {
            return;
        }
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode("ADMIN"));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin.setOver18(true);
        admin.setVerificationCode(null);
        userRepository.save(admin);
    }
}
