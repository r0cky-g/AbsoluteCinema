package ca.yorku.eecs4314group12.forum.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.yorku.eecs4314group12.forum.dto.CreateCommentRequest;
import ca.yorku.eecs4314group12.forum.model.Comment;
import ca.yorku.eecs4314group12.forum.service.CommentService;

@RestController
@RequestMapping("/forum/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Create a comment
    @PostMapping
    public Comment createComment(@RequestBody CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    // Get comments by post
    @GetMapping("/{postId}")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }

    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id,
                                               @RequestParam Long userId,
                                               @RequestParam String userRole) {
        boolean deleted = commentService.deleteComment(id, userId, userRole);
        if (deleted) {
            return ResponseEntity.ok("Comment deleted successfully");
        } else {
            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Comment not found");
            } else if ("USER".equals(userRole) && comment.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Cannot delete comments without owner information. Contact admin.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only delete your own comments (or you must be a moderator/admin)");
            }
        }
    }

}