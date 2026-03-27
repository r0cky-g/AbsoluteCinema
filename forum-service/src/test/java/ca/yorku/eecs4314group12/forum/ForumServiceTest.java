package ca.yorku.eecs4314group12.forum;

import ca.yorku.eecs4314group12.forum.model.ForumPost;
import ca.yorku.eecs4314group12.forum.repository.CommentRepository;
import ca.yorku.eecs4314group12.forum.repository.ForumPostRepository;
import ca.yorku.eecs4314group12.forum.service.ForumService;
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
class ForumServiceTest {

    @Mock
    private ForumPostRepository repository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ForumService forumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPosts() {
        Long id1 = 1L;
        Long id2 = 2L;
        ForumPost post1 = new ForumPost();
        post1.setId(id1);
        post1.setTitle("Test Post 1");
        
        ForumPost post2 = new ForumPost();
        post2.setId(id2);
        post2.setTitle("Test Post 2");
        
        when(repository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<ForumPost> posts = forumService.getAllPosts();

        assertEquals(2, posts.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testCreatePost() {
        Long user1 = 1L;
        ForumPost post = new ForumPost();
        post.setTitle("New Post");
        post.setContent("Content");
        post.setUserId(user1);
        
        when(repository.save(post)).thenReturn(post);

        ForumPost created = forumService.createPost(post);

        assertNotNull(created);
        assertEquals("New Post", created.getTitle());
        verify(repository, times(1)).save(post);
    }

    @Test
    void testDeletePost_AdminCanDeleteAnyPost() {
        Long id1 = 1L;
        Long user1 = 1L;
        Long user2 = 2L;
        ForumPost post = new ForumPost();
        post.setId(id1);
        post.setUserId(user1);
        
        when(repository.findById(id1)).thenReturn(Optional.of(post));

        boolean result = forumService.deletePost(id1, user2, "ADMIN");

        assertTrue(result);
        verify(repository, times(1)).deleteById(id1);
    }

    @Test
    void testDeletePost_UserCanDeleteOwnPost() {
        Long id2 = 1L;
        Long user1 = 1L;
        ForumPost post = new ForumPost();
        post.setId(id2);
        post.setUserId(user1);
        
        when(repository.findById(id2)).thenReturn(Optional.of(post));

        boolean result = forumService.deletePost(id2, user1, "USER");

        assertTrue(result);
        verify(repository, times(1)).deleteById(id2);
    }

    @Test
    void testDeletePost_UserCannotDeleteOthersPost() {
        Long id3 = 1L;
        Long user1 = 1L;
        Long user2 = 2L;
        ForumPost post = new ForumPost();
        post.setId(id3);
        post.setUserId(user1);
        
        when(repository.findById(id3)).thenReturn(Optional.of(post));

        boolean result = forumService.deletePost(id3, user2, "USER");

        assertFalse(result);
        verify(repository, never()).deleteById(id3);
    }

    @Test
    void testDeletePost_UserCannotDeletePostWithNullUserId() {
        Long id4 = 1L;
        Long user1 = 5L;
        ForumPost post = new ForumPost();
        post.setId(id4);
        post.setUserId(null);
        
        when(repository.findById(id4)).thenReturn(Optional.of(post));

        boolean result = forumService.deletePost(id4, user1, "USER");

        assertFalse(result);
        verify(repository, never()).deleteById(id4);
    }

    @Test
    void testDeletePost_PostNotFound() {
        Long id5 = 999L;
        Long user1 = 1L;
        when(repository.findById(id5)).thenReturn(Optional.empty());

        boolean result = forumService.deletePost(id5, user1, "ADMIN");

        assertFalse(result);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void testGetPostById() {
        Long id6 = 1L;
        ForumPost post = new ForumPost();
        post.setId(id6);
        post.setTitle("Test Post");
        
        when(repository.findById(id6)).thenReturn(Optional.of(post));

        ForumPost found = forumService.getPostById(id6);

        assertNotNull(found);
        assertEquals(id6, found.getId());
        assertEquals("Test Post", found.getTitle());
    }

    @Test
    void testGetPostById_NotFound() {
        Long id7 = 999L;
        when(repository.findById(id7)).thenReturn(Optional.empty());

        ForumPost found = forumService.getPostById(id7);

        assertNull(found);
    }

    @Test
    void testGetAllPosts_EmptyList() {
        when(repository.findAll()).thenReturn(Arrays.asList());

        List<ForumPost> posts = forumService.getAllPosts();

        assertNotNull(posts);
        assertEquals(0, posts.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testCreatePost_WithNullUserId() {
        ForumPost post = new ForumPost();
        post.setTitle("Anonymous Post");
        post.setContent("Content");
        post.setUserId(null);

        when(repository.save(post)).thenReturn(post);

        ForumPost created = forumService.createPost(post);

        assertNotNull(created);
        assertNull(created.getUserId());
        verify(repository, times(1)).save(post);
    }

    @Test
    void testCreatePost_SetsTimestamp() {
        ForumPost post = new ForumPost();
        post.setTitle("Test");
        post.setContent("Content");

        when(repository.save(any(ForumPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ForumPost created = forumService.createPost(post);

        assertNotNull(created.getCreatedAt());
    }

    @Test
    void testDeletePost_ModeratorCanDeleteAnyPost() {
        Long id8 = 1L;
        Long user1 = 1L;
        Long user2 = 2L;
        ForumPost post = new ForumPost();
        post.setId(id8);
        post.setUserId(user1);

        when(repository.findById(id8)).thenReturn(Optional.of(post));

        boolean result = forumService.deletePost(id8, user2, "MODERATOR");

        assertTrue(result);
        verify(repository, times(1)).deleteById(id8);
    }

    @Test
    void testDeletePost_UnknownRoleCannotDelete() {
        Long id9 = 1L;
        Long user1 = 1L;
        ForumPost post = new ForumPost();
        post.setId(id9);
        post.setUserId(user1);

        when(repository.findById(id9)).thenReturn(Optional.of(post));

        boolean result = forumService.deletePost(id9, user1, "GUEST");

        assertFalse(result);
        verify(repository, never()).deleteById(id9);
    }

    @Test
    void testGetPostsByCategory() {
        ForumPost post1 = new ForumPost();
        post1.setTitle("Action Movie Discussion");
        post1.setCategory("action");

        ForumPost post2 = new ForumPost();
        post2.setTitle("Best Action Films");
        post2.setCategory("action");

        when(repository.findByCategory("action")).thenReturn(Arrays.asList(post1, post2));

        List<ForumPost> posts = forumService.getPostsByCategory("action");

        assertEquals(2, posts.size());
        verify(repository, times(1)).findByCategory("action");
    }

    @Test
    void testGetPostsByCategory_NoResults() {
        when(repository.findByCategory("nonexistent")).thenReturn(Arrays.asList());

        List<ForumPost> posts = forumService.getPostsByCategory("nonexistent");

        assertNotNull(posts);
        assertEquals(0, posts.size());
        verify(repository, times(1)).findByCategory("nonexistent");
    }

    @Test
    void testSearchPostsByTitle() {
        ForumPost post1 = new ForumPost();
        post1.setTitle("Avengers Review");

        ForumPost post2 = new ForumPost();
        post2.setTitle("avengers endgame discussion");

        when(repository.findByTitleContainingIgnoreCase("avengers"))
                .thenReturn(Arrays.asList(post1, post2));

        List<ForumPost> posts = forumService.searchPostsByTitle("avengers");

        assertEquals(2, posts.size());
        verify(repository, times(1)).findByTitleContainingIgnoreCase("avengers");
    }

    @Test
    void testSearchPostsByTitle_NoResults() {
        when(repository.findByTitleContainingIgnoreCase("xyz123"))
                .thenReturn(Arrays.asList());

        List<ForumPost> posts = forumService.searchPostsByTitle("xyz123");

        assertNotNull(posts);
        assertEquals(0, posts.size());
        verify(repository, times(1)).findByTitleContainingIgnoreCase("xyz123");
    }

    @Test
    void testSearchPostsByTitle_CaseInsensitive() {
        ForumPost post = new ForumPost();
        post.setTitle("Batman Begins");

        when(repository.findByTitleContainingIgnoreCase("BATMAN"))
                .thenReturn(Arrays.asList(post));

        List<ForumPost> posts = forumService.searchPostsByTitle("BATMAN");

        assertEquals(1, posts.size());
        assertEquals("Batman Begins", posts.get(0).getTitle());
    }
}
