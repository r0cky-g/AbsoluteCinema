package ca.yorku.eecs4314group12.ui.data.dto;

public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private boolean emailVerified;
    private String role;

    public UserResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}