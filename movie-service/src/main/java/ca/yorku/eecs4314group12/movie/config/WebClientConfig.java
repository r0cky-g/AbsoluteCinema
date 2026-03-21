package ca.yorku.eecs4314group12.movie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Bean
	WebClient tmdbWebClient() {
		return WebClient.builder()
				.baseUrl("https://api.themoviedb.org/3")
				.build();
	}
}
