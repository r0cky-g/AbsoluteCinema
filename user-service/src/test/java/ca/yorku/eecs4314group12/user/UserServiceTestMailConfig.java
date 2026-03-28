package ca.yorku.eecs4314group12.user;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

/**
 * Provides a no-op {@link JavaMailSender} so {@code EmailService} can load without a real SMTP server.
 */
@TestConfiguration
public class UserServiceTestMailConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }
}
