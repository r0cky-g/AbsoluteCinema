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

    public void deletePost(Long id) {
        repository.deleteById(id);
    }

    public ForumPost getPostById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
