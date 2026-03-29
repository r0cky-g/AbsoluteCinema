package ca.yorku.eecs4314group12.api.dto.userServiceDTO;
import java.util.Set;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private boolean emailVerified;
    private String role;
    private Set<String> likedGenres;

    /** For JSON deserialization from user-service via WebClient. */
    public UserResponseDTO() {}

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

    public UserResponseDTO(Long id,
            String username,
            String email,
            boolean emailVerified,
            String role,
            Set<String> likedGenres) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.role = role;
        this.likedGenres = likedGenres;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<String> getLikedGenres() {
        return likedGenres;
    }

    public void setLikedGenres(Set<String> likedGenres) {
        this.likedGenres = likedGenres;
    }
}