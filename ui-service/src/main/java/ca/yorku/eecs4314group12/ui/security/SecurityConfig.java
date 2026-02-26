package ca.yorku.eecs4314group12.ui.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import ca.yorku.eecs4314group12.ui.views.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Security configuration.
 *
 * Uses in-memory users as placeholders until the user-service API is finalized.
 * Replace InMemoryUserDetailsManager with a real UserDetailsService that calls
 * user-service once the REST contract is agreed upon.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // Tell Vaadin which view is the login page
        setLoginView(http, LoginView.class);
    }

    /**
     * Placeholder in-memory users.
     * TODO: Replace with a call to user-service once the API is finalized.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails alice = User.withDefaultPasswordEncoder()
                .username("alice")
                .password("password")
                .roles("USER")
                .build();

        UserDetails bob = User.withDefaultPasswordEncoder()
                .username("bob")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(alice, bob);
    }
}