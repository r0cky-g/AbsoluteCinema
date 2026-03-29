package ca.yorku.eecs4314group12.user.bootstrap;

import ca.yorku.eecs4314group12.user.model.Role;
import ca.yorku.eecs4314group12.user.model.User;
import ca.yorku.eecs4314group12.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Ensures a single platform administrator exists so moderation and role management can be delegated.
 * Credentials: username {@code ADMIN123}, password {@code ADMIN123}. Change password after first deploy in production.
 * <p>
 * On every startup, an existing {@code ADMIN123} row is repaired if needed (BCrypt password, {@link Role#ADMIN},
 * email verified), so manual SQL inserts still work after a restart.
 */
@Component
@Order(0)
public class AdminAccountSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountSeeder.class);

    public static final String ADMIN_USERNAME = "ADMIN123";
    public static final String ADMIN_PASSWORD = "ADMIN123";
    public static final String ADMIN_EMAIL = "admin@absolutecinema.internal";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAccountSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Optional<User> existing = userRepository.findByUsername(ADMIN_USERNAME);
        if (existing.isPresent()) {
            ensureAdminState(existing.get());
            return;
        }

        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.warn(
                    "Seed admin skipped: email {} is already taken. Use username {} or free that email.",
                    ADMIN_EMAIL,
                    ADMIN_USERNAME);
            return;
        }

        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin.setOver18(true);
        admin.setVerificationCode(null);
        userRepository.save(admin);
        log.info("Created seed administrator '{}'", ADMIN_USERNAME);
    }

    private void ensureAdminState(User admin) {
        boolean dirty = false;

        if (admin.getPassword() == null
                || !passwordEncoder.matches(ADMIN_PASSWORD, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            dirty = true;
        }
        if (admin.getRole() != Role.ADMIN) {
            admin.setRole(Role.ADMIN);
            dirty = true;
        }
        if (!admin.isEmailVerified()) {
            admin.setEmailVerified(true);
            dirty = true;
        }
        if (admin.getVerificationCode() != null) {
            admin.setVerificationCode(null);
            dirty = true;
        }
        if (!admin.isOver18()) {
            admin.setOver18(true);
            dirty = true;
        }

        if (dirty) {
            userRepository.save(admin);
            log.info("Updated seed administrator '{}' (password, role, and/or verification)", ADMIN_USERNAME);
        }
    }
}
