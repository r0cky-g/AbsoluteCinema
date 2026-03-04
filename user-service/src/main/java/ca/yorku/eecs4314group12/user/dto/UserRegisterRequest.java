package ca.yorku.eecs4314group12.user.dto;

import jakarta.validation.constraints.*;

public class UserRegisterRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 8, max = 16,
          message = "Username must be between 8 and 16 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 20,
          message = "Password must be between 8 and 20 characters")
    private String password;

    private boolean over18;

    // getters & setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isOver18() { return over18; }
    public void setOver18(boolean over18) { this.over18 = over18; }
}