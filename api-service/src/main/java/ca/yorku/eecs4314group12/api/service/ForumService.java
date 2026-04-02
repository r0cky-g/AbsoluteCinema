package ca.yorku.eecs4314group12.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ca.yorku.eecs4314group12.api.client.ForumClient;
import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.Comment;
import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.CreateCommentRequest;
import ca.yorku.eecs4314group12.api.dto.forumServiceDTO.ForumPost;
import reactor.core.publisher.Mono;

@Service
public class ForumService {

    private final ForumClient forumClient;

    public ForumService(ForumClient forumClient) {
        this.forumClient = forumClient;
    }

    public Mono<ResponseEntity<List<Map<String,Object>>>> getPost(String category, String search) {
        return forumClient.getPost(category, search);
    }

    public Mono<ResponseEntity<Map<String,Object>>> createPost(ForumPost post) {
        return forumClient.createPost(post);
    }

    public Mono<ResponseEntity<String>> deletePost(Long postId, Long userId, String userRole) {
        return forumClient.deletePost(postId, userId, userRole);
    }

    public Mono<ResponseEntity<Map<String,Object>>> getPostById(Long postId) {
        return forumClient.getPostById(postId);
    }

    public Mono<ResponseEntity<Map<String,Object>>> updatePost(Long postId, ForumPost post, Long userId, String userRole) {
        return forumClient.updatePost(postId, post, userId, userRole);
    }

    public Mono<ResponseEntity<Comment>> createComment(CreateCommentRequest request) {
        return forumClient.createComment(request);
    }

    public Mono<ResponseEntity<List<Comment>>> getComments(Long postId) {
        return forumClient.getComments(postId);
    }

    public Mono<ResponseEntity<String>> deleteComment(Long postId, Long userId, String userRole) {
        return forumClient.deleteComment(postId, userId, userRole);
    }
}
