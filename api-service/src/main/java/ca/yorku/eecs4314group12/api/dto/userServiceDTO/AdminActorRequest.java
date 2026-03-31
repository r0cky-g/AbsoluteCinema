package ca.yorku.eecs4314group12.api.dto.userServiceDTO;

import jakarta.validation.constraints.NotBlank;

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
