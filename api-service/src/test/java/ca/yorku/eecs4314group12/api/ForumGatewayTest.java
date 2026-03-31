package ca.yorku.eecs4314group12.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ForumGatewayTest {

    @LocalServerPort
    int port;

    private WebClient gateway;

    @BeforeEach
    void setup() {
        gateway = WebClient.create("http://localhost:" + port + "/api");
    }

    @Test
    void testForum() {
        // Create a post
        Map<String, Object> postBody = Map.of(
            "title", "Test Post " + System.currentTimeMillis(),
            "content", "This is a test post",
            "userId", 1,
            "category", "test"
        );

        // Send POST to create the post
        Map<String, Object> postResp = gateway.post()
            .uri("/forum/posts")
            .bodyValue(postBody)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        Long postId = ((Number) postResp.get("id")).longValue();
        assertNotNull(postId);

        // Get the created post by search
        List<Map<String, Object>> posts = gateway.get()
            .uri("/forum/posts?category=test")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
            .block();
        
        assertNotNull(posts, "Retrieved posts should not be null");
        
        // Verify that the post we just created is in the list
        boolean found = posts.stream()
            .anyMatch(p -> ((Number)p.get("id")).longValue() == postId &&
                postBody.get("title").equals(p.get("title")) &&
                postBody.get("content").equals(p.get("content")) &&
                postBody.get("userId").equals(((Number)p.get("userId")).intValue()) &&
                postBody.get("category").equals(p.get("category")));

        assertTrue(found, "Created post should appear in the search results");

        // Get the created post by ID
        Map<String, Object> createdPost = gateway.get()
            .uri("/forum/posts/{postId}", postId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        assertNotNull(createdPost, "Retrieved post should not be null");
        assertEquals(postBody.get("title"), createdPost.get("title"));
        assertEquals(postBody.get("content"), createdPost.get("content"));
        assertEquals(postBody.get("userId"), ((Number)createdPost.get("userId")).intValue());
        assertEquals(postBody.get("category"), createdPost.get("category"));

        // Create a comment on the post
        Map<String, Object> commentBody = Map.of(
            "postId", postId,
            "userId", 1,
            "content", "This is a test comment"
        );

        Map<String, Object> commentResp = gateway.post()
            .uri("/forum/comments")
            .bodyValue(commentBody)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        Long commentId = ((Number) commentResp.get("id")).longValue();
        assertNotNull(commentId);

        // Get comments for the post
        List<Map<String, Object>> comments = gateway.get()
            .uri("/forum/comments/{postId}", postId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
            .block();

        assertTrue(comments.stream().anyMatch(c -> ((Number)c.get("id")).longValue() == commentId),
            "Created comment should appear in list");

        // Delete comment
        String deleteCommentResp = gateway.delete()
            .uri(uriBuilder -> uriBuilder
                    .path("/forum/comments/{commentId}")
                    .queryParam("userId", 1)
                    .queryParam("userRole", "ADMIN")
                    .build(commentId))
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertEquals("Comment deleted successfully", deleteCommentResp);

        // Delete post
        String deletePostResp = gateway.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/forum/posts/{postId}")
                .queryParam("userId", 1)
                .queryParam("userRole", "ADMIN")
                .build(postId))
            .retrieve()
            .bodyToMono(String.class)
            .block();
                
        assertEquals("Post deleted successfully", deletePostResp);
    }
}