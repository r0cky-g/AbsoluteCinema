package ca.yorku.eecs4314group12.movie.service;

import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MovieService {
	
	private String tmdbToken;
	
	public MovieService(@Value("${tmdb.token}") String tmdbToken) {
		this.tmdbToken = tmdbToken;
	}
	
	public TmdbMovieDTO getDetails(int id) {
		return callTmdbApiDetails(id);
	}
	
	private TmdbMovieDTO callTmdbApiDetails(int id) {
		WebClient tmdbClient = WebClient.create("https://api.themoviedb.org/3");
		
		TmdbMovieDTO response = tmdbClient.get()
										  .uri("/movie/"+id+"?append_to_response=images,videos,credits&language=en-US&include_image_langugage=null")
										  .header("accept", "application/json")
										  .header("Authorization", "Bearer "+tmdbToken)
										  .retrieve()
										  .bodyToMono(TmdbMovieDTO.class)
										  .block();
		
		return response;
	}
}
