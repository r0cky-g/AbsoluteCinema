package ca.yorku.eecs4314group12.movie.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ca.yorku.eecs4314group12.movie.document.Movie;
import ca.yorku.eecs4314group12.movie.dto.ActorDTO;
import ca.yorku.eecs4314group12.movie.dto.CrewMemberDTO;
import ca.yorku.eecs4314group12.movie.dto.MovieDTO;
import ca.yorku.eecs4314group12.movie.dto.MoviesTrendingDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbCertificateDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbCompanyDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbCreditsActorDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbCreditsCrewDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbGenreDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbMovieDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbMoviesTrendingDTO;
import ca.yorku.eecs4314group12.movie.dto.TmdbReleaseDatesDTO;

@Mapper(componentModel = "spring")
public interface MovieMapper {
	
	@Mapping(source = "genres", target = "genres", qualifiedByName = "mapGenres")
	@Mapping(source = "release_dates", target = "age_rating", qualifiedByName = "extractUSRating")
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
	@Mapping(target = "release_date")
	MovieDTO toMovieDTO(TmdbMovieDTO tmdbMovieDTO);
	
	MovieDTO toMovieDTO(Movie movie);
	
	ActorDTO toActorDTO(TmdbCreditsActorDTO tmdbActorDTO);
	CrewMemberDTO toCrewMemberDTO(TmdbCreditsCrewDTO tmdbCrewDTO);
	
	MoviesTrendingDTO toMoviesTrendingDTO(TmdbMoviesTrendingDTO tmdbMoviesTrendingDTO);
	
	@Named("mapGenres")
    default List<String> mapGenres(List<TmdbGenreDTO> genres) {
        return genres.stream()
                .map(TmdbGenreDTO::getName)  
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
                .orElse(null);
    }
	
	@Named("mapProductionCompanies")
    default List<String> mapProductionCompanies(List<TmdbCompanyDTO> companies) {
        return companies.stream()
                .map(TmdbCompanyDTO::getName)  
                .collect(Collectors.toList());
    }
}
