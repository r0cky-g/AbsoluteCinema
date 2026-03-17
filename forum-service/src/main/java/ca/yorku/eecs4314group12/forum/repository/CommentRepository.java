package ca.yorku.eecs4314group12.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.yorku.eecs4314group12.forum.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

}