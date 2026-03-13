package ca.yorku.eecs4314group12.forum;

import ca.yorku.eecs4314group12.forum.service.ForumService;
import ca.yorku.eecs4314group12.forum.controller.CommentController;
import ca.yorku.eecs4314group12.forum.controller.ForumController;
import ca.yorku.eecs4314group12.forum.service.CommentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ ForumController.class, CommentController.class })
class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForumService forumService;

    @MockBean
    private CommentService commentService;

    // GET /forum/posts
    @Test
    void testGetPosts() throws Exception {
        mockMvc.perform(get("/forum/posts"))
                .andExpect(status().isOk());
    }

    // GET /forum/posts/{id}
    @Test
    void testGetPostById() throws Exception {
        mockMvc.perform(get("/forum/posts/1"))
                .andExpect(status().isOk());
    }

    // POST /forum/posts
    @Test
    void testCreatePost() throws Exception {

        String json = """
                {
                    "title": "Test Post",
                    "content": "This is a test post"
                }
                """;

        mockMvc.perform(post("/forum/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // DELETE /forum/posts/{id}
    @Test
    void testDeletePost() throws Exception {
        mockMvc.perform(delete("/forum/posts/1"))
                .andExpect(status().isOk());
    }

    // GET /forum/comments/{postId}
    @Test
    void testGetComments() throws Exception {
        mockMvc.perform(get("/forum/comments/1"))
                .andExpect(status().isOk());
    }

    // POST /forum/comments
    @Test
    void testCreateComment() throws Exception {

        String json = """
                {
                    "postId": 1,
                    "userId": 1,
                    "content": "Great movie discussion!"
                }
                """;

        mockMvc.perform(post("/forum/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

}