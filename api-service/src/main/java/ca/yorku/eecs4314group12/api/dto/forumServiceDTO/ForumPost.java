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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}