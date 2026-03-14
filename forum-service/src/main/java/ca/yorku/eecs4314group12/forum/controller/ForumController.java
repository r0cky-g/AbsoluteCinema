package ca.yorku.eecs4314group12.forum.controller;

import ca.yorku.eecs4314group12.forum.model.ForumPost;
import ca.yorku.eecs4314group12.forum.service.ForumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum/posts")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    // get all posts
    @GetMapping
    public List<ForumPost> getPosts() {
        return forumService.getAllPosts();
    }

    // create post
    @PostMapping
    public ForumPost createPost(@RequestBody ForumPost post) {
        return forumService.createPost(post);
    }

    // delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, 
                                           @RequestParam Long userId, 
                                           @RequestParam String userRole) {
        boolean deleted = forumService.deletePost(id, userId, userRole);
        if (deleted) {
            return ResponseEntity.ok("Post deleted successfully");
        } else {
            ForumPost post = forumService.getPostById(id);
            if (post == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Post not found");
            } else if ("USER".equals(userRole) && post.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Cannot delete legacy posts without owner information. Contact admin.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only delete your own posts (or you must be an admin)");
            }
        }
    }

    // Get post by id
    @GetMapping("/{id}")
    public ForumPost getPostById(@PathVariable Long id) {
        return forumService.getPostById(id);
    }
}