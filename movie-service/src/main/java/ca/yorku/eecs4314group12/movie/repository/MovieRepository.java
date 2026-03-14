package ca.yorku.eecs4314group12.movie.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ca.yorku.eecs4314group12.movie.document.Movie;

public interface MovieRepository extends MongoRepository<Movie, Integer> {}