package ca.yorku.eecs4314group12.ui.security;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Session-scoped service that stores the logged-in user's numeric ID and role
 * from user-service after successful authentication.
 *
 * Uses ScopedProxyMode.TARGET_CLASS so this session-scoped bean can be safely
 * injected into singleton beans like SecurityConfig.
 */
@Service
@VaadinSessionScope
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSessionService {

    private Long userId;
    private String role;
    private String email;

    public void setUser(Long userId, String role, String email) {
        this.userId = userId;
        this.role = role;
        this.email = email;
    }

    public void clear() {
        this.userId = null;
        this.role = null;
        this.email = null;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role != null ? role : "USER";
    }

    public String getEmail() {
        return email;
    }

    public boolean isLoggedIn() {
        return userId != null;
    }
}