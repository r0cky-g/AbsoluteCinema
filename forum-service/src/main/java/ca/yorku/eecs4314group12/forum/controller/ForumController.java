package ca.yorku.eecs4314group12.forum.controller;

import ca.yorku.eecs4314group12.forum.model.ForumPost;
import ca.yorku.eecs4314group12.forum.service.ForumService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum/posts")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    // get all posts
    @GetMapping
    public List<ForumPost> getPosts() {
        return forumService.getAllPosts();
    }

    // create post
    @PostMapping
    public ForumPost createPost(@RequestBody ForumPost post) {
        return forumService.createPost(post);
    }

    // delete post
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        forumService.deletePost(id);
    }

    // Get post by id
    @GetMapping("/{id}")
    public ForumPost getPostById(@PathVariable Long id) {
        return forumService.getPostById(id);
    }
}