package ca.yorku.eecs4314group12.api.dto.forumServiceDTO;

import java.time.LocalDateTime;

public class ForumPost {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String category;
    private LocalDateTime createdAt;

    public ForumPost() {
    }

    public ForumPost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ForumPost(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
}