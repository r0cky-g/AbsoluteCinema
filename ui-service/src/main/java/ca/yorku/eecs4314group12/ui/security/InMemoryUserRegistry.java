package ca.yorku.eecs4314group12.ui.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory user store that supports runtime registration.
 * Users are lost when the application restarts.
 *
 * TODO: Replace with calls to user-service REST API once the contract
 *       is finalized.
 */
@Component
public class InMemoryUserRegistry {

    private final Map<String, UserDetails> users = new ConcurrentHashMap<>();
    private PasswordEncoder passwordEncoder;

    // Setter injection to avoid circular dependency with SecurityConfig
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user. Returns true if successful, false if username
     * is already taken.
     */
    public boolean register(String username, String rawPassword, String email) {
        if (users.containsKey(username.toLowerCase())) {
            return false;
        }
        UserDetails user = new User(
                username,
                passwordEncoder.encode(rawPassword),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        users.put(username.toLowerCase(), user);
        return true;
    }

    public UserDetails findUser(String username) {
        return users.get(username.toLowerCase());
    }

    public boolean usernameExists(String username) {
        return users.containsKey(username.toLowerCase());
    }
}