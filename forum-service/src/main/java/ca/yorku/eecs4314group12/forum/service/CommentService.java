package ca.yorku.eecs4314group12.forum.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import ca.yorku.eecs4314group12.forum.dto.CreateCommentRequest;
import ca.yorku.eecs4314group12.forum.model.Comment;
import ca.yorku.eecs4314group12.forum.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository repository;

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public Comment createComment(CreateCommentRequest request) {

        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(request.getUserId());
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        return repository.save(comment);
    }

    public List<Comment> getCommentsByPost(Long postId) {
        return repository.findByPostId(postId);
    }

    public boolean deleteComment(Long id, Long userId, String userRole) {
        Comment comment = repository.findById(id).orElse(null);
        if (comment == null) {
            return false;
        }
        
        // Allow MODERATOR and ADMIN to delete any comment
        if ("MODERATOR".equals(userRole) || "ADMIN".equals(userRole)) {
            repository.deleteById(id);
            return true;
        }
        
        if ("USER".equals(userRole)) {
            if (comment.getUserId() != null && comment.getUserId().equals(userId)) {
                repository.deleteById(id);
                return true;
            }
        }
        
        return false;
    }

    public Comment getCommentById(Long id) {
        return repository.findById(id).orElse(null);
    }
}