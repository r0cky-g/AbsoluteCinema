package ca.yorku.eecs4314group12.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ca.yorku.eecs4314group12.forum.model.ForumPost;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    // Find all posts belonging to a specific category
    List<ForumPost> findByCategory(String category);

    // Search posts by title (case-insensitive)
    List<ForumPost> findByTitleContainingIgnoreCase(String keyword);
}