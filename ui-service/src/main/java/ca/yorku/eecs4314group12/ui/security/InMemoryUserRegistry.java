package ca.yorku.eecs4314group12.ui.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory user store that mirrors accounts from user-service.
 *
 * When a user logs in successfully via user-service, their credentials are
 * stored here so Spring Security can authenticate them on subsequent requests
 * within the same session.
 *
 * When a user registers via user-service, they are also mirrored here so
 * they can log in immediately.
 */
@Component
public class InMemoryUserRegistry {

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRegistry.class);

    private final Map<String, UserDetails> users = new ConcurrentHashMap<>();
    private PasswordEncoder passwordEncoder;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Mirrors a user from user-service into memory with their raw password
     * so Spring Security can authenticate them.
     * Always overwrites — call this after every successful user-service login/register.
     */
    public void mirror(String username, String rawPassword, String role) {
        String r = role != null ? role : "USER";
        String authority = switch (r) {
            case "ADMIN" -> "ROLE_ADMIN";
            case "MODERATOR" -> "ROLE_MODERATOR";
            default -> "ROLE_USER";
        };
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .authorities(new SimpleGrantedAuthority(authority))
                .build();
        users.put(username.toLowerCase(), user);
        log.debug("Mirrored user '{}' with role {}", username, authority);
    }

    /**
     * Legacy register without user-service — kept for fallback.
     */
    public boolean register(String username, String rawPassword, String email) {
        if (users.containsKey(username.toLowerCase())) return false;
        mirror(username, rawPassword, "USER");
        return true;
    }

    public UserDetails findUser(String username) {
        return users.get(username.toLowerCase());
    }

    public boolean usernameExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    public boolean verifyPassword(String username, String rawPassword) {
        UserDetails user = findUser(username);
        if (user == null) return false;
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void updatePassword(String username, String rawNewPassword) {
        String key = username.toLowerCase();
        UserDetails existing = users.get(key);
        if (existing == null) return;
        UserDetails updated = User.builder()
                .username(existing.getUsername())
                .password(passwordEncoder.encode(rawNewPassword))
                .authorities(existing.getAuthorities())
                .build();
        users.put(key, updated);
    }
}