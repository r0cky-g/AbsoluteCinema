package ca.yorku.eecs4314group12.ui.security;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.UserResponseDTO;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import ca.yorku.eecs4314group12.ui.views.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Optional;

/**
 * Security configuration for Vaadin 25 + Spring Boot 4.
 *
 * Uses a custom AuthenticationProvider that:
 *   1. Calls user-service POST /user/login to verify credentials
 *   2. On success: mirrors the user into InMemoryUserRegistry and
 *      stores the numeric user ID + role in UserSessionService
 *   3. Falls back to InMemoryUserRegistry for users already mirrored
 *      (avoids re-calling user-service on every request)
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final InMemoryUserRegistry userRegistry;
    private final BackendClientService backendClient;
    private final UserSessionService userSessionService;

    public SecurityConfig(InMemoryUserRegistry userRegistry,
                          BackendClientService backendClient,
                          UserSessionService userSessionService) {
        this.userRegistry = userRegistry;
        this.backendClient = backendClient;
        this.userSessionService = userSessionService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            configurer.loginView(LoginView.class);
        }).build();
    }

    /**
     * Custom AuthenticationProvider — tries user-service first, falls back to in-memory.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication)
                    throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                // Try user-service first
                Optional<UserResponseDTO> userOpt = backendClient.loginUser(username, password);
                if (userOpt.isPresent()) {
                    UserResponseDTO dto = userOpt.get();
                    // Mirror into memory and store session data
                    userRegistry.mirror(username, password, dto.getRole());
                    userSessionService.setUser(dto.getId(), dto.getRole(), dto.getEmail());

                    String authority = "ADMIN".equals(dto.getRole()) ? "ROLE_ADMIN" : "ROLE_USER";
                    return new UsernamePasswordAuthenticationToken(
                            username, password,
                            List.of(new SimpleGrantedAuthority(authority)));
                }

                // Fall back to in-memory (for users registered in this session)
                UserDetails inMemory = userRegistry.findUser(username);
                if (inMemory != null && passwordEncoder().matches(password, inMemory.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(
                            username, password, inMemory.getAuthorities());
                }

                throw new BadCredentialsException("Invalid username or password");
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserDetails user = userRegistry.findUser(username);
            if (user == null) throw new UsernameNotFoundException("User not found: " + username);
            return user;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}