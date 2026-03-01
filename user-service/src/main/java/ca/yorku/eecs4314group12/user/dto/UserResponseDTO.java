package ca.yorku.eecs4314group12.user.dto;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private boolean emailVerified;
    private String role;

    public UserResponseDTO(Long id,
            String username,
            String email,
            boolean emailVerified,
            String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getRole() {
        return role;
    }
}