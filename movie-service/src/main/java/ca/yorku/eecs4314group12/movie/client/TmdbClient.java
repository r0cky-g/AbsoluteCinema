package ca.yorku.eecs4314group12.movie.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;

@Component
public class TmdbClient {
	
	private final WebClient webClient;
	private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2OTU1ZmI0OGU3YTdlYWU0MGFlMTRlYmY2ZDYwMDQ4ZiIsIm5iZiI6MTc3MjI0NTkwNi41NzEsInN1YiI6IjY5YTI1MzkyYWIxZTk1MTAyY2UyYWVjMSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FU9djysVkuM6gvoO21HunREIX7fnK9K0qh7-gjVkx6U";
	
	public TmdbClient(WebClient tdmbWebClient) {
		webClient = tdmbWebClient;
	}
	
	public TmdbMovieDTO getMovieDetails(int id) {
		return webClient.get()
				.uri("/movie/"+id+"?append_to_response=release_dates,images,videos,credits&include_image_langugage=null")
				.header("accept", "application/json")
				.header("Authorization", "Bearer "+token)
				.retrieve()
				.bodyToMono(TmdbMovieDTO.class)
				.block();
	}
}
