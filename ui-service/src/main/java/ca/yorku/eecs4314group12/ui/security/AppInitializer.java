package ca.yorku.eecs4314group12.ui.security;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Wires the PasswordEncoder into InMemoryUserRegistry after the application
 * context is fully loaded to avoid a circular dependency.
 */
@Component
public class AppInitializer {

    private final InMemoryUserRegistry userRegistry;
    private final PasswordEncoder passwordEncoder;

    public AppInitializer(InMemoryUserRegistry userRegistry, PasswordEncoder passwordEncoder) {
        this.userRegistry = userRegistry;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        userRegistry.setPasswordEncoder(passwordEncoder);
    }
}