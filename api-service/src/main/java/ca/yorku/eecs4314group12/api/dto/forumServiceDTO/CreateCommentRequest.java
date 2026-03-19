package ca.yorku.eecs4314group12.api.dto.forumServiceDTO;

public class CreateCommentRequest {

    private Long postId;
    private Long userId;
    private String content;

    public CreateCommentRequest() {
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}