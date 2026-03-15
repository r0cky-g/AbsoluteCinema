package ca.yorku.eecs4314group12.forum.service;

import ca.yorku.eecs4314group12.forum.repository.ForumPostRepository;

import ca.yorku.eecs4314group12.forum.model.ForumPost;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {

    private final ForumPostRepository repository;

    public ForumService(ForumPostRepository repository) {
        this.repository = repository;
    }

    public List<ForumPost> getAllPosts() {
        return repository.findAll();
    }

    public ForumPost createPost(ForumPost post) {
        return repository.save(post);
    }

    public boolean deletePost(Long id, Long userId, String userRole) {
        ForumPost post = repository.findById(id).orElse(null);
        if (post == null) {
            return false;
        }
        
        // Allow MODERATOR and ADMIN to delete any post
        if ("MODERATOR".equals(userRole) || "ADMIN".equals(userRole)) {
            repository.deleteById(id);
            return true;
        }
        
        if ("USER".equals(userRole)) {
            if (post.getUserId() != null && post.getUserId().equals(userId)) {
                repository.deleteById(id);
                return true;
            }
        }
        
        return false;
    }

    public ForumPost getPostById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
