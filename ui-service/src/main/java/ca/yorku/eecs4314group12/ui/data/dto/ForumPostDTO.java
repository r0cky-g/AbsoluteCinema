package ca.yorku.eecs4314group12.ui.data.dto;

public class ForumPostDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String category;

    public ForumPostDTO() {}

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

    /** Normalised category key — lowercased, trimmed, for grouping/comparison. */
    public String getCategoryKey() {
        if (category == null || category.isBlank()) return "general";
        return category.trim().toLowerCase();
    }

    /** Display-friendly category — trimmed, title-cased first char. */
    public String getCategoryDisplay() {
        if (category == null || category.isBlank()) return "General";
        String t = category.trim();
        return t.substring(0, 1).toUpperCase() + t.substring(1);
    }
}