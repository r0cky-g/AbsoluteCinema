package ca.yorku.eecs4314group12.movie.exception;

public class MovieNotFoundException extends RuntimeException {
	
	public MovieNotFoundException(int id) {
		super("Movie not found with id: "+id);
	}
}
