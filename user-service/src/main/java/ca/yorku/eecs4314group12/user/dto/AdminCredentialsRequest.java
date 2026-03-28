package ca.yorku.eecs4314group12.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Credentials of the acting administrator for role-management endpoints.
 * {@code adminIdentifier} is the same value used for login (username or email).
 */
public class AdminCredentialsRequest {

    @NotBlank(message = "Administrator identifier cannot be blank")
    private String adminIdentifier;

    @NotBlank(message = "Administrator password cannot be blank")
    private String adminPassword;

    public String getAdminIdentifier() {
        return adminIdentifier;
    }

    public void setAdminIdentifier(String adminIdentifier) {
        this.adminIdentifier = adminIdentifier;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
