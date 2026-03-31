package ca.yorku.eecs4314group12.forum.service;

import ca.yorku.eecs4314group12.forum.repository.CommentRepository;
import ca.yorku.eecs4314group12.forum.repository.ForumPostRepository;
import ca.yorku.eecs4314group12.forum.model.ForumPost;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumService {

    private final ForumPostRepository repository;
    private final CommentRepository commentRepository;

    public ForumService(ForumPostRepository repository, CommentRepository commentRepository) {
        this.repository = repository;
        this.commentRepository = commentRepository;
    }

    public List<ForumPost> getAllPosts() {
        return repository.findAll();
    }

    // Get all posts filtered by category
    public List<ForumPost> getPostsByCategory(String category) {
        return repository.findByCategoryIgnoreCase(category);
    }

    // Search posts by title keyword
    public List<ForumPost> searchPostsByTitle(String keyword) {
        return repository.findByTitleContainingIgnoreCase(keyword);
    }

    public ForumPost createPost(ForumPost post) {
        post.setCreatedAt(LocalDateTime.now());
        return repository.save(post);
    }

    @Transactional
    public boolean deletePost(Long id, Long userId, String userRole) {
        ForumPost post = repository.findById(id).orElse(null);
        if (post == null) {
            return false;
        }
        
        // Allow MODERATOR and ADMIN to delete any post
        if ("MODERATOR".equals(userRole) || "ADMIN".equals(userRole)) {
            commentRepository.deleteByPostId(id);
            repository.deleteById(id);
            return true;
        }
        
        if ("USER".equals(userRole)) {
            if (post.getUserId() != null && post.getUserId().equals(userId)) {
                commentRepository.deleteByPostId(id);
                repository.deleteById(id);
                return true;
            }
        }
        
        return false;
    }

    public ForumPost getPostById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public ForumPost updatePost(Long id, String title, String content, Long userId, String userRole) {
        ForumPost post = repository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }
        
        // Check permissions: only owner, moderator, or admin can update
        boolean canUpdate = "MODERATOR".equals(userRole) || "ADMIN".equals(userRole) ||
                           (post.getUserId() != null && post.getUserId().equals(userId));
        
        if (!canUpdate) {
            return null;
        }
        
        post.setTitle(title);
        post.setContent(content);
        return repository.save(post);
    }
}
