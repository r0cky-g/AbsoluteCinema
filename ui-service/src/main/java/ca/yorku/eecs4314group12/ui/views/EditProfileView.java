package ca.yorku.eecs4314group12.ui.views;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.UserResponseDTO;
import ca.yorku.eecs4314group12.ui.security.InMemoryUserRegistry;
import ca.yorku.eecs4314group12.ui.security.UserSessionService;
import jakarta.annotation.security.PermitAll;

/**
 * Edit Profile page — fully wired to user-service PUT /user/{id}.
 *
 * Sections:
 *   - Account Details (username, email, dob, over-18) → PUT /user/{id}
 *   - Change Password → PUT /user/{id} with new password field
 *   - Liked Genres (multi-select) → PUT /user/{id} with likedGenres
 *
 * All sections pre-populate from user-service GET /user/{id}.
 */
@Route(value = "edit-profile", layout = MainLayout.class)
@PageTitle("Edit Profile | Absolute Cinema")
@PermitAll
public class EditProfileView extends VerticalLayout {

    private static final Set<String> ALL_GENRES = Set.of(
            "Action", "Adventure", "Animation", "Comedy", "Crime",
            "Documentary", "Drama", "Fantasy", "Horror", "Mystery",
            "Romance", "Sci-Fi", "Thriller", "Western"
    );

    private final InMemoryUserRegistry userRegistry;
    private final BackendClientService backendClient;
    private final UserSessionService userSessionService;

    public EditProfileView(InMemoryUserRegistry userRegistry,
                           BackendClientService backendClient,
                           UserSessionService userSessionService) {
        this.userRegistry = userRegistry;
        this.backendClient = backendClient;
        this.userSessionService = userSessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "680px").set("margin", "0 auto");

        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // Pre-fetch user data from user-service
        UserResponseDTO userData = userSessionService.getUserId() != null
                ? backendClient.getUserData(userSessionService.getUserId()).orElse(null)
                : null;

        add(
                buildPageHeader(),
                buildProfileSection(currentUsername, userData),
                new Hr(),
                buildPasswordSection(currentUsername),
                new Hr(),
                buildPreferencesSection(userData)
        );
    }

    // -------------------------------------------------------------------------
    // Page header
    // -------------------------------------------------------------------------

    private VerticalLayout buildPageHeader() {
        H2 title = new H2("Edit Profile");
        title.getStyle().set("margin-bottom", "0.25rem");
        Span subtitle = new Span("Keep your account information up to date.");
        subtitle.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");
        VerticalLayout header = new VerticalLayout(title, subtitle);
        header.setPadding(false); header.setSpacing(false);
        header.getStyle().set("margin-bottom", "var(--lumo-space-l)");
        return header;
    }

    // -------------------------------------------------------------------------
    // Profile section
    // -------------------------------------------------------------------------

    private VerticalLayout buildProfileSection(String currentUsername, UserResponseDTO userData) {
        H3 sectionTitle = new H3("Account Details");
        sectionTitle.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        TextField usernameField = new TextField("Username");
        usernameField.setValue(currentUsername);
        usernameField.setHelperText("8–16 characters. Letters, numbers, and underscores only.");
        usernameField.setWidth("100%"); usernameField.setMaxWidth("480px");
        usernameField.setPattern("^[a-zA-Z0-9_]{8,16}$");
        usernameField.setMinLength(8); usernameField.setMaxLength(16);

        EmailField emailField = new EmailField("Email");
        emailField.setWidth("100%"); emailField.setMaxWidth("480px");
        if (userData != null && userData.getEmail() != null)
            emailField.setValue(userData.getEmail());

        DatePicker dobField = new DatePicker("Date of Birth");
        dobField.setMax(LocalDate.now().minusDays(1));
        dobField.setHelperText("Must be in the past.");
        dobField.setWidth("100%"); dobField.setMaxWidth("480px");

        Checkbox over18Box = new Checkbox("I confirm that I am 18 years of age or older.");

        Button saveBtn = new Button("Save Changes");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClickListener(e -> {
            String newUsername = usernameField.getValue().trim();
            String newEmail = emailField.getValue().trim();

            if (newUsername.isEmpty()) { showError("Username cannot be empty."); return; }
            if (!newUsername.matches("^[a-zA-Z0-9_]{8,16}$")) {
                showError("Username must be 8–16 characters (letters, numbers, underscores only)."); return;
            }
            if (newEmail.isEmpty()) { showError("Email cannot be empty."); return; }

            if (userSessionService.getUserId() == null) {
                showError("Session expired. Please log in again."); return;
            }

            LocalDate dob = dobField.getValue();
            Boolean over18 = over18Box.getValue();

            boolean success = backendClient.updateUser(
                    userSessionService.getUserId(),
                    newUsername, newEmail,
                    null, dob, over18, null);

            if (success) {
                // Also update in-memory registry so Spring Security session stays valid
                if (!newUsername.equals(currentUsername)) {
                    userRegistry.mirror(newUsername,
                            "", // password stays as-is in memory
                            userSessionService.getRole());
                }
                showSuccess("Profile updated successfully.");
            } else {
                showError("Failed to save profile. Username or email may already be taken.");
            }
        });

        VerticalLayout section = new VerticalLayout(
                sectionTitle, usernameField, emailField, dobField, over18Box, saveBtn);
        styleSection(section);
        return section;
    }

    // -------------------------------------------------------------------------
    // Password section
    // -------------------------------------------------------------------------

    private VerticalLayout buildPasswordSection(String currentUsername) {
        H3 sectionTitle = new H3("Change Password");
        sectionTitle.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        PasswordField currentPasswordField = new PasswordField("Current Password");
        currentPasswordField.setWidth("100%"); currentPasswordField.setMaxWidth("480px");

        PasswordField newPasswordField = new PasswordField("New Password");
        newPasswordField.setHelperText("8–20 characters.");
        newPasswordField.setMinLength(8); newPasswordField.setMaxLength(20);
        newPasswordField.setWidth("100%"); newPasswordField.setMaxWidth("480px");

        PasswordField confirmPasswordField = new PasswordField("Confirm New Password");
        confirmPasswordField.setWidth("100%"); confirmPasswordField.setMaxWidth("480px");

        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changePasswordBtn.addClickListener(e -> {
            String current = currentPasswordField.getValue();
            String newPw = newPasswordField.getValue();
            String confirm = confirmPasswordField.getValue();

            if (current.isEmpty() || newPw.isEmpty() || confirm.isEmpty()) {
                showError("All password fields are required."); return;
            }
            if (newPw.length() < 8 || newPw.length() > 20) {
                showError("New password must be 8–20 characters."); return;
            }
            if (!newPw.equals(confirm)) {
                showError("New passwords do not match."); return;
            }

            // Verify current password against in-memory store
            boolean verified = userRegistry.verifyPassword(currentUsername, current);
            if (!verified) { showError("Current password is incorrect."); return; }

            if (userSessionService.getUserId() == null) {
                showError("Session expired. Please log in again."); return;
            }

            // Get current user data to preserve username/email
            Optional<UserResponseDTO> userOpt =
                    backendClient.getUserData(userSessionService.getUserId());
            if (userOpt.isEmpty()) { showError("Could not load user data."); return; }

            UserResponseDTO user = userOpt.get();
            boolean success = backendClient.updateUser(
                    userSessionService.getUserId(),
                    user.getUsername(), user.getEmail(),
                    newPw, null, null, null);

            if (success) {
                // Update in-memory registry with new password
                userRegistry.mirror(currentUsername, newPw, userSessionService.getRole());
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                showSuccess("Password changed successfully.");
            } else {
                showError("Failed to change password. Please try again.");
            }
        });

        VerticalLayout section = new VerticalLayout(
                sectionTitle, currentPasswordField, newPasswordField,
                confirmPasswordField, changePasswordBtn);
        styleSection(section);
        return section;
    }

    // -------------------------------------------------------------------------
    // Preferences section
    // -------------------------------------------------------------------------

    private VerticalLayout buildPreferencesSection(UserResponseDTO userData) {
        H3 sectionTitle = new H3("Preferences");
        sectionTitle.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        Span genreLabel = new Span("Liked Genres");
        genreLabel.getStyle().set("font-weight", "600").set("font-size", "var(--lumo-font-size-s)");

        CheckboxGroup<String> genreGroup = new CheckboxGroup<>();
        genreGroup.setItems(ALL_GENRES.stream().sorted().toList());

        // Pre-populate from user-service
        if (userData != null && userData.getLikedGenres() != null
                && !userData.getLikedGenres().isEmpty()) {
            genreGroup.setValue(userData.getLikedGenres());
        }

        Button savePrefsBtn = new Button("Save Preferences");
        savePrefsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        savePrefsBtn.addClickListener(e -> {
            if (userSessionService.getUserId() == null) {
                showError("Session expired. Please log in again."); return;
            }
            Set<String> selected = genreGroup.getSelectedItems();
            boolean success = backendClient.updateUserPreferences(
                    userSessionService.getUserId(), selected);
            if (success) {
                showSuccess("Preferences saved! (" + selected.size() + " genre(s) selected)");
            } else {
                showError("Failed to save preferences. Please try again.");
            }
        });

        VerticalLayout section = new VerticalLayout(sectionTitle, genreLabel, genreGroup, savePrefsBtn);
        styleSection(section);
        return section;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void styleSection(VerticalLayout section) {
        section.setPadding(false); section.setSpacing(true);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
    }

    private void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification.show(message, 4000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}