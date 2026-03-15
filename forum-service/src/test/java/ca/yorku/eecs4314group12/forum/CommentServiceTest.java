package ca.yorku.eecs4314group12.forum;

import ca.yorku.eecs4314group12.forum.dto.CreateCommentRequest;
import ca.yorku.eecs4314group12.forum.model.Comment;
import ca.yorku.eecs4314group12.forum.repository.CommentRepository;
import ca.yorku.eecs4314group12.forum.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository repository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setPostId(1L);
        request.setUserId(1L);
        request.setContent("Test comment");
        
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setUserId(1L);
        comment.setContent("Test comment");
        
        when(repository.save(any(Comment.class))).thenReturn(comment);

        Comment created = commentService.createComment(request);

        assertNotNull(created);
        assertEquals("Test comment", created.getContent());
        verify(repository, times(1)).save(any(Comment.class));
    }

    @Test
    void testGetCommentsByPost() {
        Long postId = 1L;
        Comment comment1 = new Comment();
        comment1.setPostId(postId);
        Comment comment2 = new Comment();
        comment2.setPostId(postId);
        
        when(repository.findByPostId(postId)).thenReturn(Arrays.asList(comment1, comment2));

        List<Comment> comments = commentService.getCommentsByPost(postId);

        assertEquals(2, comments.size());
        verify(repository, times(1)).findByPostId(postId);
    }

    @Test
    void testDeleteComment_AdminCanDeleteAnyComment() {
        Long id1 = 1L;
        Long user1 = 1L;
        Long user2 = 2L;
        Comment comment = new Comment();
        comment.setUserId(user1);
        
        when(repository.findById(id1)).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(id1, user2, "ADMIN");

        assertTrue(result);
        verify(repository, times(1)).deleteById(id1);
    }

    @Test
    void testDeleteComment_UserCanDeleteOwnComment() {
        Long id2 = 1L;
        Long user1 = 1L;
        Comment comment = new Comment();
        comment.setUserId(user1);
        
        when(repository.findById(id2)).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(id2, user1, "USER");

        assertTrue(result);
        verify(repository, times(1)).deleteById(id2);
    }

    @Test
    void testDeleteComment_UserCannotDeleteOthersComment() {
        Long id3 = 1L;
        Long user1 = 1L;
        Long user2 = 2L;
        Comment comment = new Comment();
        comment.setUserId(user1);
        
        when(repository.findById(id3)).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(id3, user2, "USER");

        assertFalse(result);
        verify(repository, never()).deleteById(id3);
    }

    @Test
    void testDeleteComment_UserCannotDeleteCommentWithNullUserId() {
        Long id4 = 1L;
        Long user1 = 1L;
        Comment comment = new Comment();
        comment.setUserId(null);
        
        when(repository.findById(id4)).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(id4, user1, "USER");

        assertFalse(result);
        verify(repository, never()).deleteById(id4);
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        Long id5 = 999L;
        Long user1 = 1L;
        when(repository.findById(id5)).thenReturn(Optional.empty());

        boolean result = commentService.deleteComment(id5, user1, "ADMIN");

        assertFalse(result);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void testGetCommentById() {
        Long id6 = 1L;
        Comment comment = new Comment();
        comment.setContent("Test");
        
        when(repository.findById(id6)).thenReturn(Optional.of(comment));

        Comment found = commentService.getCommentById(id6);

        assertNotNull(found);
        assertEquals("Test", found.getContent());
    }

    @Test
    void testGetCommentById_NotFound() {
        Long id7 = 999L;
        when(repository.findById(id7)).thenReturn(Optional.empty());

        Comment found = commentService.getCommentById(id7);

        assertNull(found);
    }
}
