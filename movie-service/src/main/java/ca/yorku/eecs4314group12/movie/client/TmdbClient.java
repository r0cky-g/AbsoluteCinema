package ca.yorku.eecs4314group12.movie.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;

@Component
public class TmdbClient {
	
	private final WebClient webClient;
	private final String token;
	
	public TmdbClient(WebClient tdmbWebClient, @Value("${tmdb.token}") String tmdbToken) {
		webClient = tdmbWebClient;
		token = tmdbToken;
	}
	
	public TmdbMovieDTO getMovieDetails(int id) {
		return webClient.get()
				.uri("/movie/"+id+"?append_to_response=images,videos,credits&include_image_langugage=null")
				.header("accept", "application/json")
				.header("Authorization", "Bearer "+token)
				.retrieve()
				.bodyToMono(TmdbMovieDTO.class)
				.block();
	}
}
