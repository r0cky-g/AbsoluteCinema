package ca.yorku.eecs4314group12.api.dto.userServiceDTO;
import java.util.Set;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private boolean emailVerified;
    private String role;
    private Set<String> likedGenres;

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

    public Set<String> getLikedGenres() {
        return likedGenres;
    }

    public void setLikedGenres(Set<String> likedGenres) {
        this.likedGenres = likedGenres;
    }
}