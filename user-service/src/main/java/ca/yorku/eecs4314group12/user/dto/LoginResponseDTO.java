package ca.yorku.eecs4314group12.user.dto;

/**
 * Response returned after a successful login.
 * Contains basic user information.
 */
public class LoginResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean emailVerified;

    public LoginResponseDTO(Long id,
            String username,
            String email,
            String role,
            boolean emailVerified) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.emailVerified = emailVerified;
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

    public String getRole() {
        return role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}