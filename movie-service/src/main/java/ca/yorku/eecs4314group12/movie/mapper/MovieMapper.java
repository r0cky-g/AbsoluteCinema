package ca.yorku.eecs4314group12.movie.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ca.yorku.eecs4314group12.movie.document.Movie;
import ca.yorku.eecs4314group12.movie.dto.*;
import ca.yorku.eecs4314group12.movie.dto.tmdb.*;

@Mapper(componentModel = "spring")
public interface MovieMapper {
	
	// Mappings for Objects
	@Mapping(source = "genres", target = "genres", qualifiedByName = "mapGenres")
	@Mapping(source = "release_dates", target = "age_rating", qualifiedByName = "extractUSRating")
	@Mapping(source = "images.backdrops", target = "images", qualifiedByName = "extractImages")
	@Mapping(source = "videos.results", target = "videos", qualifiedByName = "extractOfficialYouTubeVids")
	@Mapping(source = "credits.cast", target = "cast")
	@Mapping(source = "credits.crew", target = "crew")
	@Mapping(source = "production_companies", target = "production_companies", qualifiedByName = "mapProductionCompanies")
	Movie toMovie(TmdbMovieDTO tmdbMovieDTO);
	
	Movie toMovie(MovieDTO movieDTO);
	
	@BeanMapping(ignoreByDefault = true)
	@Mapping(target = "id")
	@Mapping(target = "adult")
	@Mapping(target = "original_title")
	@Mapping(target = "title")
	@Mapping(source = "genre_ids", target = "genres", qualifiedByName = "mapGenresByID")
	@Mapping(target = "release_date")
	@Mapping(target = "poster_path")
	MovieDTO toMovieDTO(TmdbMovieDTO tmdbMovieDTO);

	MovieDTO toMovieDTO(Movie movie);
	
	MovieSearchDTO toMovieSearchDTO(TmdbMovieSearchDTO tmdbMovieSearchDTO);
	
	MoviesTrendingDTO toMoviesTrendingDTO(TmdbMoviesTrendingDTO tmdbMoviesTrendingDTO);
	
	MoviesNowPlayingDTO toMoviesNowPlayingDTO(TmdbMoviesNowPlayingDTO tmdbMoviesNowPlayingDTO);
	
	// Mappings for Nested Objects 
	ActorDTO toActorDTO(TmdbCreditsActorDTO tmdbActorDTO);
	
	CrewMemberDTO toCrewMemberDTO(TmdbCreditsCrewDTO tmdbCrewDTO);
	
	List<MovieDTO> toMovieDTOList(List<Movie> movies);
	
	// Explicitly defined custom behaviour when mapping certain fields or objects
	@Named("mapGenres")
    default List<String> mapGenres(List<TmdbGenreDTO> genres) {
        return genres.stream()
                .map(TmdbGenreDTO::getName)  
                .collect(Collectors.toList());
    }
	
	@Named("mapGenresByID")
    default List<String> mapGenresByID(List<Integer> genres) {
        return genres.stream()
        		.map(id -> switch(id) {
        		case 28    -> "Action";
        		case 12    -> "Adventure";
        		case 16    -> "Animation";
        		case 35    -> "Comedy";
        		case 80    -> "Crime";
        		case 99    -> "Documentary";
        		case 18    -> "Drama";
        		case 10751 -> "Family";
        		case 14    -> "Fantasy";
        		case 36    -> "History";
        		case 27    -> "Horror";
        		case 10402 -> "Music";
        		case 9648  -> "Mystery";
        		case 10749 -> "Romance";
        		case 878   -> "Science Fiction";
        		case 10770 -> "TV Movie";
        		case 53    -> "Thriller";
        		case 10752 -> "War";
        		case 37    -> "Western";
        		default    -> "Unknown";
        		})
        		.collect(Collectors.toList());
    }
	
	@Named("extractUSRating")
    default String extractUsRating(TmdbReleaseDatesDTO releaseDates) {
        return releaseDates.getResults().stream()
                .filter(r -> "US".equals(r.getIso_3166_1()))  
                .findFirst()
                .flatMap(r -> r.getRelease_dates().stream()
                        .filter(d -> !d.getCertification().isEmpty())  
                        .findFirst())
                .map(TmdbCertificateDTO::getCertification)  
                .orElse("Unrated");
    }
	
	@Named("extractImages")
    default List<String> extractImages(List<TmdbImageDTO> images) {
        return images.stream()
                .map(TmdbImageDTO::getFile_path)  
                .collect(Collectors.toList());
    }
	
	@Named("extractOfficialYouTubeVids")
    default List<String> extractOfficialYouTubeVids(List<TmdbVideoDTO> videos) {
        return videos.stream()
        		.filter(v -> "YouTube".equals(v.getSite()) && Boolean.TRUE.equals(v.isOfficial()))
                .map(TmdbVideoDTO::getKey)  
                .collect(Collectors.toList());
    }
	
	@Named("mapProductionCompanies")
    default List<String> mapProductionCompanies(List<TmdbCompanyDTO> companies) {
        return companies.stream()
                .map(TmdbCompanyDTO::getName)  
                .collect(Collectors.toList());
    }
	
	default MovieSearchDTO toMovieSearchDTO(List<Movie> movies) {
		MovieSearchDTO movieSearchDTO = new MovieSearchDTO();
		movieSearchDTO.setResults(toMovieDTOList(movies));
		return movieSearchDTO;
	}
}