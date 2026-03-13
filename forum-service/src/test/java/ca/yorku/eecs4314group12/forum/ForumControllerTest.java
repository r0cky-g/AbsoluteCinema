package ca.yorku.eecs4314group12.forum;

import ca.yorku.eecs4314group12.forum.controller.CommentController;
import ca.yorku.eecs4314group12.forum.controller.ForumController;
import ca.yorku.eecs4314group12.forum.model.Comment;
import ca.yorku.eecs4314group12.forum.model.ForumPost;
import ca.yorku.eecs4314group12.forum.service.CommentService;
import ca.yorku.eecs4314group12.forum.service.ForumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class ForumControllerTest {

    @Mock
    private ForumService forumService;

    @Mock
    private CommentService commentService;

    private ForumController forumController;
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        forumController = new ForumController(forumService);
        commentController = new CommentController(commentService);
    }

    @Test
    void testGetPosts() {
        ForumPost post1 = new ForumPost("Test Post 1", "Content 1");
        post1.setId(1L);
        ForumPost post2 = new ForumPost("Test Post 2", "Content 2");
        post2.setId(2L);
        List<ForumPost> posts = Arrays.asList(post1, post2);

        when(forumService.getAllPosts()).thenReturn(posts);

        List<ForumPost> result = forumController.getPosts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Post 1", result.get(0).getTitle());
        assertEquals("Test Post 2", result.get(1).getTitle());
        verify(forumService, times(1)).getAllPosts();
    }

    @Test
    void testGetPostById() {
        ForumPost post = new ForumPost("Test Post", "Test Content");
        post.setId(1L);

        when(forumService.getPostById(1L)).thenReturn(post);

        ForumPost result = forumController.getPostById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Post", result.getTitle());
        assertEquals("Test Content", result.getContent());
        verify(forumService, times(1)).getPostById(1L);
    }

    @Test
    void testCreatePost() {
        ForumPost inputPost = new ForumPost("New Post", "New Content");
        ForumPost savedPost = new ForumPost("New Post", "New Content");
        savedPost.setId(1L);

        when(forumService.createPost(any(ForumPost.class))).thenReturn(savedPost);

        ForumPost result = forumController.createPost(inputPost);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Post", result.getTitle());
        assertEquals("New Content", result.getContent());
        verify(forumService, times(1)).createPost(any(ForumPost.class));
    }

    @Test
    void testDeletePost() {
        forumController.deletePost(1L);

        verify(forumService, times(1)).deletePost(1L);
    }

    @Test
    void testGetComments() {
        Comment comment1 = new Comment();
        comment1.setPostId(1L);
        comment1.setUserId(1L);
        comment1.setContent("Great post!");
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setPostId(1L);
        comment2.setUserId(2L);
        comment2.setContent("I agree!");
        comment2.setCreatedAt(LocalDateTime.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentService.getCommentsByPost(1L)).thenReturn(comments);

        List<Comment> result = commentController.getComments(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Great post!", result.get(0).getContent());
        assertEquals("I agree!", result.get(1).getContent());
        verify(commentService, times(1)).getCommentsByPost(1L);
    }

    @Test
    void testCreateComment() {
        ca.yorku.eecs4314group12.forum.dto.CreateCommentRequest request = 
            new ca.yorku.eecs4314group12.forum.dto.CreateCommentRequest();
        request.setPostId(1L);
        request.setUserId(1L);
        request.setContent("Great movie discussion!");

        Comment savedComment = new Comment();
        savedComment.setPostId(1L);
        savedComment.setUserId(1L);
        savedComment.setContent("Great movie discussion!");
        savedComment.setCreatedAt(LocalDateTime.now());

        when(commentService.createComment(any())).thenReturn(savedComment);

        Comment result = commentController.createComment(request);

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertEquals(1L, result.getUserId());
        assertEquals("Great movie discussion!", result.getContent());
        verify(commentService, times(1)).createComment(any());
    }
}
