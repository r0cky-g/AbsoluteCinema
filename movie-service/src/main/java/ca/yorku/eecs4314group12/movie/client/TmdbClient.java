package ca.yorku.eecs4314group12.movie.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbMoviesNowPlayingDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbMoviesTrendingDTO;

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
				.uri("/movie/"+id+"?append_to_response=release_dates,credits")
				.header("accept", "application/json")
				.header("Authorization", "Bearer "+token)
				.retrieve()
				.bodyToMono(TmdbMovieDTO.class)
				.block();
	}
	
	public TmdbMoviesTrendingDTO getMoviesTrending() {
		return webClient.get()
				.uri("/trending/movie/day")
				.header("accept", "application/json")
				.header("Authorization", "Bearer "+token)
				.retrieve()
				.bodyToMono(TmdbMoviesTrendingDTO.class)
				.block();
	}
	
	public TmdbMoviesNowPlayingDTO getMoviesNowPlaying() {
		return webClient.get()
				.uri("/movie/now_playing")
				.header("accept", "application/json")
				.header("Authorization", "Bearer "+token)
				.retrieve()
				.bodyToMono(TmdbMoviesNowPlayingDTO.class)
				.block();
	}
}
