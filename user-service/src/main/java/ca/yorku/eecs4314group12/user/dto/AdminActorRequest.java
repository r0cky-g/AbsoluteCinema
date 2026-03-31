package ca.yorku.eecs4314group12.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Identifies the administrator performing a role-management action.
 * {@code adminIdentifier} matches login (username or email).
 */
public class AdminActorRequest {

    @NotBlank(message = "Administrator identifier cannot be blank")
    private String adminIdentifier;

    public String getAdminIdentifier() {
        return adminIdentifier;
    }

    public void setAdminIdentifier(String adminIdentifier) {
        this.adminIdentifier = adminIdentifier;
    }
}
