package ca.yorku.eecs4314group12.forum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "forum_posts")
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    @Column(nullable = false)
    private String content;
    
    @Column(name = "user_id")
    private Long userId;

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

    public Long getId() { 
        return id; 
        }
    public void setId(Long id) { 
        this.id = id; 
        }
    public String getTitle() { 
        return title; 
        }
    public void setTitle(String title) { 
        this.title = title; 
        }
    public String getContent() { 
        return content; 
        }
    public void setContent(String content) { 
        this.content = content; 
        }
    public Long getUserId() { 
        return userId; 
        }
    public void setUserId(Long userId) { 
        this.userId = userId; 
        }
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
        }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
        }
}