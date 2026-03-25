package ca.yorku.eecs4314group12.user.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRoleUpdateRequest {

    @NotBlank(message = "Role is required")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
