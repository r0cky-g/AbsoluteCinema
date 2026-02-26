package ca.yorku.eecs4314group12.ui.security;

import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import ca.yorku.eecs4314group12.ui.views.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Vaadin 25 + Spring Boot 4.
 *
 * Uses VaadinSecurityConfigurer (replaces the removed VaadinWebSecurity).
 * Uses an in-memory user store that supports runtime registration.
 *
 * TODO: Replace InMemoryUserRegistry with a call to user-service once the
 *       REST contract is agreed upon.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final InMemoryUserRegistry userRegistry;

    public SecurityConfig(InMemoryUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            configurer.loginView(LoginView.class);
        }).build();
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