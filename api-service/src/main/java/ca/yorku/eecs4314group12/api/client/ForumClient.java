package ca.yorku.eecs4314group12.api.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.Comment;
import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.CreateCommentRequest;
import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.ForumPost;
import reactor.core.publisher.Mono;

@Component
public class ForumClient {

    private final BaseWebClient baseWebClient;

    public ForumClient(@Qualifier("APIForumClient") WebClient webClient) {
        this.baseWebClient = new BaseWebClient(webClient);
    }

    public Mono<ResponseEntity<List<ForumPost>>> getPost(String category, String search) {
        if (category == null && search == null)
            return baseWebClient.get("/forum/posts", new ParameterizedTypeReference<List<ForumPost>> () {});
        if (category == null)
            return baseWebClient.get("/forum/posts?search={search}", new ParameterizedTypeReference<List<ForumPost>> () {}, search);  
        return baseWebClient.get("/forum/posts?category={category}", new ParameterizedTypeReference<List<ForumPost>> () {}, category);
    }

    public Mono<ResponseEntity<ForumPost>> createPost(ForumPost post) {
        return baseWebClient.post("/forum/posts", post, new ParameterizedTypeReference<ForumPost> () {});
    }

    public Mono<ResponseEntity<String>> deletePost(Long postId, Long userId, String userRole) {
        return baseWebClient.delete("/forum/posts/{postId}?userId=" + userId + "&userRole=" + userRole, new ParameterizedTypeReference<String> () {}, postId);
    }

    public Mono<ResponseEntity<ForumPost>> getPostById(Long postId) {
        return baseWebClient.get("/forum/posts/{postId}", new ParameterizedTypeReference<ForumPost> () {}, postId);
    }

    public Mono<ResponseEntity<Comment>> createComment(CreateCommentRequest request) {
        return baseWebClient.post("/forum/comments", request, new ParameterizedTypeReference<Comment> () {});
    }

    public Mono<ResponseEntity<List<Comment>>> getComments(Long postId) {
        return baseWebClient.get("/forum/comments/{postId}", new ParameterizedTypeReference<List<Comment>> () {}, postId);
    }

    public Mono<ResponseEntity<String>> deleteComment(Long postId, Long userId, String userRole) {
        return baseWebClient.delete("/forum/comments/{postId}?userId=" + userId + "&userRole=" + userRole, new ParameterizedTypeReference<String> () {}, postId);
    }
}
