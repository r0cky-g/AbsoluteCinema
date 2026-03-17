package ca.yorku.eecs4314group12.movie.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ca.yorku.eecs4314group12.movie.document.Movie;

public interface MovieRepository extends MongoRepository<Movie, Integer> {
	@Query("{ title: { $regex: ?0, $options: 'i' } }")
    List<Movie> findByTitle(String keyword);
}