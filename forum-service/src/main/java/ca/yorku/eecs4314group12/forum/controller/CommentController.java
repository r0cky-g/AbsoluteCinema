package ca.yorku.eecs4314group12.forum.controller;

import java.util.List;

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

}