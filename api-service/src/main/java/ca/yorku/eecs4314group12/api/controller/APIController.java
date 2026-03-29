package ca.yorku.eecs4314group12.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.Comment;
import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.CreateCommentRequest;
import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.ForumPost;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MovieSearchDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesNowPlayingDTO;
import ca.yorku.eecs4314group12.api.dto.movieServiceDTO.MoviesTrendingDTO;
import ca.yorku.eecs4314group12.api.dto.reviewServiceDTO.ReviewDTO;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.FavouriteMovie;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.LoginRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserRegisterRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserResponseDTO;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.UserUpdateRequest;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.WatchHistory;
import ca.yorku.eecs4314group12.api.dto.userServiceDTO.Watchlist;
import ca.yorku.eecs4314group12.api.service.ForumService;
import ca.yorku.eecs4314group12.api.service.MovieService;
import ca.yorku.eecs4314group12.api.service.ReviewService;
import ca.yorku.eecs4314group12.api.service.UserService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class APIController {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final ForumService forumService;

    public APIController(MovieService movieService, UserService userService, ReviewService reviewService, ForumService forumService) {
        this.movieService = movieService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.forumService = forumService;
    }


    // api service calls


    @GetMapping("/test")
    public ResponseEntity<String> testmethod() {
        ResponseEntity<String> response = new ResponseEntity<String>("gateway is working", HttpStatusCode.valueOf(200));
        return response;
    }


    // forum service calls


    @GetMapping("/forum/posts")
    public Mono<ResponseEntity<List<ForumPost>>> getPost(@RequestParam(required = false) String category, 
                                                        @RequestParam(required = false) String search) {
        return forumService.getPost(category, search);
    }

    @PostMapping("/forum/posts")
    public Mono<ResponseEntity<ForumPost>> createPost(@RequestBody ForumPost post) {
        return forumService.createPost(post);
    }

    @DeleteMapping("/forum/posts/{postId}")
    public Mono<ResponseEntity<String>> deletePost(@PathVariable Long postId, 
                                                @RequestParam Long userId, 
                                                @RequestParam String userRole) {
        return forumService.deletePost(postId, userId, userRole);
    }

    @GetMapping("/forum/posts/{postId}")
    public Mono<ResponseEntity<ForumPost>> getPostById(@PathVariable Long postId) {
        return forumService.getPostById(postId);
    }

    @PostMapping("/forum/comments")
    public Mono<ResponseEntity<Comment>> createComment(@RequestBody CreateCommentRequest request) {
        return forumService.createComment(request);
    }

    @GetMapping("/forum/comments/{postId}")
    public Mono<ResponseEntity<List<Comment>>> getComments(@PathVariable Long postId) {
        return forumService.getComments(postId);
    }

    @DeleteMapping("/forum/comments/{postId}")
    public Mono<ResponseEntity<String>> deleteComment(@PathVariable Long postId, 
                                                    @RequestParam Long userId, 
                                                    @RequestParam String userRole) {
        return forumService.deleteComment(postId, userId, userRole);
    }


    // movie service calls


    @GetMapping("/movie/{id}")
    public Mono<ResponseEntity<MovieDTO>> getMovieDetails(@PathVariable int id) {
        return movieService.getDetails(id);
    }

    @GetMapping("/movie/search/{name}")
    public Mono<ResponseEntity<MovieSearchDTO>> searchMovie(@PathVariable String name) {
        return movieService.getDetails(name);
    }

    @GetMapping("/movie/trending")
    public Mono<ResponseEntity<MoviesTrendingDTO>> getMovieTrending() {
        return movieService.getTrending();
    }

    @GetMapping("/movie/nowplaying")
    public Mono<ResponseEntity<MoviesNowPlayingDTO>> getMovieNowPlaying() {
        return movieService.getNowPlaying();
    }


    // review service calls


    @PostMapping("/reviews")
    public Mono<ResponseEntity<ReviewDTO>> createReview(@RequestBody ReviewDTO reviewDTO) {
        return reviewService.createReview(reviewDTO);
    }

    @GetMapping("/reviews/{movieId}")
    public Mono<ResponseEntity<List<ReviewDTO>>> getReviewsByMovie(@PathVariable Long movieId) {
        return reviewService.getReviewsByMovie(movieId);
    }

    @GetMapping("/reviews/user/{userId}")
    public Mono<ResponseEntity<List<ReviewDTO>>> getReviewsByUser(@PathVariable long userId) {
        return reviewService.getReviewsByUser(userId);
    }

    @GetMapping("/reviews/{id}")
    public Mono<ResponseEntity<ReviewDTO>> getReviewByID(@PathVariable long id) {
        return reviewService.getReviewByID(id);
    }

    @PutMapping("/reviews/{id}")
    public Mono<ResponseEntity<ReviewDTO>> updateReview(@PathVariable Long id, 
                                                        @RequestBody ReviewDTO reviewDTO) {
        return reviewService.updateReview(id, reviewDTO);
    }

    // should include role
    @DeleteMapping("/reviews/{id}")
    public Mono<ResponseEntity<Void>> deleteReview(@PathVariable Long id, 
                                                @RequestParam Long userId) {
        return reviewService.deleteReview(id, userId);
    }

    // would really like to get <String, DTO> not just generic
    @GetMapping("/reviews/movie/{movieId}/stats")
    public Mono<ResponseEntity<Map<String, Object>>> getMovieStats(@PathVariable Long movieId) {
        return reviewService.getMovieStats(movieId);
    }

    @PostMapping("/reviews/{id}/helpful")
    public Mono<ResponseEntity<ReviewDTO>> markAsHelpful(@PathVariable Long id) {
        return reviewService.markAsHelpful(id);
    }


    // user service calls

    @PostMapping("/user/register")
    public Mono<ResponseEntity<UserResponseDTO>> register(@RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/user/login")
    public Mono<ResponseEntity<UserResponseDTO>> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/user/{id}/verify")
    public Mono<ResponseEntity<String>> verifyEmail(@PathVariable Long id, 
                                                    @RequestParam String code) {
        return userService.verifyEmail(id, code);
    }

    @GetMapping("/user")
    public Mono<ResponseEntity<List<UserResponseDTO>>> getUsers() {
        return userService.getUsers();
    }
    
    @GetMapping("/user/{id}")
    public Mono<ResponseEntity<UserResponseDTO>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/user/{id}")
    public Mono<ResponseEntity<UserResponseDTO>> updateUser(@PathVariable Long id,
                                                            @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/user/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/user/{userId}/watchlist/{movieId}")
    public Mono<ResponseEntity<Watchlist>> addToWatchlist(@PathVariable Long userId,
                                                        @PathVariable Integer movieId) {
        return userService.addToWatchlist(userId, movieId);
    }

    @DeleteMapping("/user/{userId}/watchlist/{movieId}")
    public Mono<ResponseEntity<Void>> removeFromWatchlist(@PathVariable Long userId, 
                                                        @PathVariable Integer movieId) {
        return userService.removeFromWatchlist(userId, movieId);
    }

    @GetMapping("/user/{userId}/watchlist")
    public Mono<ResponseEntity<List<Watchlist>>> getUserWatchlist(@PathVariable Long userId) {
        return userService.getUserWatchlist(userId);
    }

    @GetMapping("/user/{userId}/watchlist/{movieId}")
    public Mono<ResponseEntity<Boolean>> isInWatchlist(@PathVariable Long userId,  
                                                    @PathVariable Integer movieId) {
        return userService.isInWatchlist(userId, movieId);
    }

    @GetMapping("/user/{userId}/recommendations")
    public Mono<ResponseEntity<List<MovieDTO>>> getRecommendedMovies(@PathVariable Long userId) {
        return userService.getRecommendedMovies(userId);
    }

    @PostMapping("/user/{userId}/history/{movieId}")
    public Mono<ResponseEntity<WatchHistory>> addToHistory(@PathVariable Long userId, 
                                                        @PathVariable Integer movieId) {
        return userService.addToHistory(userId, movieId);
    }

    @GetMapping("/user/{userId}/history")
    public Mono<ResponseEntity<List<WatchHistory>>> getUserHistory(@PathVariable Long userId) {
        return userService.getUserHistory(userId);
    }

    @DeleteMapping("/user/{userId}/history/{movieId}")
    public Mono<ResponseEntity<Void>> removeFromHistory(@PathVariable Long userId, 
                                                        @PathVariable Integer movieId) {
        return userService.removeFromHistory(userId, movieId);
    }

    @PostMapping("user/{userId}/favourites/{movieId}")
    public Mono<ResponseEntity<FavouriteMovie>> addFavourite(@PathVariable Long userId, 
                                                            @PathVariable Integer movieId) {
        return userService.addFavourite(userId, movieId);
    }

    @GetMapping("user/{userId}/favourites")
    public Mono<ResponseEntity<List<FavouriteMovie>>> getUserFavourites(@PathVariable Long userId) {
        return userService.getUserFavourites(userId);
    }

    @DeleteMapping("user/{userId}/favourites/{movieId}")
    public Mono<ResponseEntity<Void>> removeFavourite(@PathVariable Long userId, 
                                                    @PathVariable Integer movieId) {
        return userService.removeFavourite(userId, movieId);
    }

}