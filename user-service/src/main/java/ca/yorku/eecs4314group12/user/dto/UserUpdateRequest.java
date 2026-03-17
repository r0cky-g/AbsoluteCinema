package ca.yorku.eecs4314group12.user.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public class UserUpdateRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 8, max = 16, message = "Username must be between 8 and 16 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    // Password is optional for updates (only updated if provided)
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    private Boolean over18;

    private Set<String> likedGenres;

    // Getters and setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Boolean getOver18() {
        return over18;
    }

    public void setOver18(Boolean over18) {
        this.over18 = over18;
    }

    public Set<String> getLikedGenres() {
        return likedGenres;
    }

    public void setLikedGenres(Set<String> likedGenres) {
        this.likedGenres = likedGenres;
    }
}
