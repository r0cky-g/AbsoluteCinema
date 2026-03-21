package ca.yorku.eecs4314group12.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ca.yorku.eecs4314group12.forum.model.ForumPost;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
}