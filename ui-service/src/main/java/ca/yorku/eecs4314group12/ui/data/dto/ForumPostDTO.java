package ca.yorku.eecs4314group12.ui.data.dto;

public class ForumPostDTO {
    private Long id;
    private String title;
    private String content;

    public ForumPostDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}