package ca.yorku.eecs4314group12.ui.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Security configuration.
 *
 * Uses an in-memory user store that supports runtime registration.
 * Accounts created during a session are lost on restart.
 *
 * TODO: Replace InMemoryUserRegistry with a call to user-service once the
 *       REST contract is agreed upon.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private final InMemoryUserRegistry userRegistry;

    public SecurityConfig(InMemoryUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
        http.logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );
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