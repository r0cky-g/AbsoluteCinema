package ca.yorku.eecs4314group12.forum.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import ca.yorku.eecs4314group12.forum.model.ForumPost;
import ca.yorku.eecs4314group12.forum.service.ForumService;

@RestController
@RequestMapping("/forum/posts")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping
    public List<ForumPost> getPosts() {
        return forumService.getAllPosts();
    }

    @PostMapping
    public ForumPost createPost(@RequestBody ForumPost post) {
        return forumService.createPost(post);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        forumService.deletePost(id);
    }

}