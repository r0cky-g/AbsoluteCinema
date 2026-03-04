package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.security.InMemoryUserRegistry;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Set;

/**
 * Edit Profile page — lets the logged-in user update their account details.
 *
 * Fields exposed:
 *   - Username (8–16 chars, letters/numbers/underscores)
 *   - Email
 *   - Password (change only — requires current password confirmation)
 *   - Date of birth
 *   - Over-18 acknowledgement
 *   - Liked genres (multi-select)
 *
 * Currently backed by InMemoryUserRegistry for username/password changes.
 * TODO: Replace with PUT /user/{id} calls to user-service once integrated.
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

    public EditProfileView(InMemoryUserRegistry userRegistry) {
        this.userRegistry = userRegistry;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle()
                .set("max-width", "680px")
                .set("margin", "0 auto");

        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        add(
                buildPageHeader(),
                buildProfileSection(currentUsername),
                new Hr(),
                buildPasswordSection(currentUsername),
                new Hr(),
                buildPreferencesSection()
        );
    }

    // -------------------------------------------------------------------------
    // Page header
    // -------------------------------------------------------------------------

    private VerticalLayout buildPageHeader() {
        H2 title = new H2("Edit Profile");
        title.getStyle().set("margin-bottom", "0.25rem");

        Span subtitle = new Span("Keep your account information up to date.");
        subtitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        VerticalLayout header = new VerticalLayout(title, subtitle);
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("margin-bottom", "var(--lumo-space-l)");
        return header;
    }

    // -------------------------------------------------------------------------
    // Profile info section (username, email, DOB, over-18)
    // -------------------------------------------------------------------------

    private VerticalLayout buildProfileSection(String currentUsername) {
        H3 sectionTitle = new H3("Account Details");
        sectionTitle.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        TextField usernameField = new TextField("Username");
        usernameField.setValue(currentUsername);
        usernameField.setHelperText("8–16 characters. Letters, numbers, and underscores only.");
        usernameField.setWidth("100%");
        usernameField.setMaxWidth("480px");
        usernameField.setPattern("^[a-zA-Z0-9_]{8,16}$");
        usernameField.setMinLength(8);
        usernameField.setMaxLength(16);

        EmailField emailField = new EmailField("Email");
        emailField.setPlaceholder("your@email.com");
        emailField.setHelperText("We'll send a verification link if you change this.");
        emailField.setWidth("100%");
        emailField.setMaxWidth("480px");
        // TODO: pre-populate from user-service GET /user/{id} response

        DatePicker dobField = new DatePicker("Date of Birth");
        dobField.setMax(LocalDate.now().minusDays(1));
        dobField.setHelperText("Must be in the past.");
        dobField.setWidth("100%");
        dobField.setMaxWidth("480px");
        // TODO: pre-populate from user-service

        Checkbox over18Box = new Checkbox("I confirm that I am 18 years of age or older.");
        // TODO: pre-populate from user-service

        Button saveProfileBtn = new Button("Save Changes");
        saveProfileBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveProfileBtn.addClickListener(e -> {
            String newUsername = usernameField.getValue().trim();
            String newEmail    = emailField.getValue().trim();

            // --- Validation ---
            if (newUsername.isEmpty()) {
                showError("Username cannot be empty.");
                return;
            }
            if (!newUsername.matches("^[a-zA-Z0-9_]{8,16}$")) {
                showError("Username must be 8–16 characters (letters, numbers, underscores only).");
                return;
            }
            if (newEmail.isEmpty()) {
                showError("Email cannot be empty.");
                return;
            }

            // --- Persist to in-memory registry (username change) ---
            String currentUsername2 = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            if (!newUsername.equals(currentUsername2)) {
                if (userRegistry.usernameExists(newUsername)) {
                    showError("That username is already taken.");
                    return;
                }
                // TODO: call user-service PUT /user/{id} with new username
            }

            // TODO: call user-service PUT /user/{id} with newEmail, dob, over18 once integrated
            showSuccess("Profile updated! Note: username/email changes will take effect after your next login.");
        });

        VerticalLayout section = new VerticalLayout(
                sectionTitle, usernameField, emailField, dobField, over18Box, saveProfileBtn
        );
        section.setPadding(false);
        section.setSpacing(true);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        return section;
    }

    // -------------------------------------------------------------------------
    // Password change section
    // -------------------------------------------------------------------------

    private VerticalLayout buildPasswordSection(String currentUsername) {
        H3 sectionTitle = new H3("Change Password");
        sectionTitle.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        PasswordField currentPasswordField = new PasswordField("Current Password");
        currentPasswordField.setWidth("100%");
        currentPasswordField.setMaxWidth("480px");

        PasswordField newPasswordField = new PasswordField("New Password");
        newPasswordField.setHelperText("8–20 characters.");
        newPasswordField.setMinLength(8);
        newPasswordField.setMaxLength(20);
        newPasswordField.setWidth("100%");
        newPasswordField.setMaxWidth("480px");

        PasswordField confirmPasswordField = new PasswordField("Confirm New Password");
        confirmPasswordField.setWidth("100%");
        confirmPasswordField.setMaxWidth("480px");

        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changePasswordBtn.addClickListener(e -> {
            String current = currentPasswordField.getValue();
            String newPw   = newPasswordField.getValue();
            String confirm = confirmPasswordField.getValue();

            if (current.isEmpty() || newPw.isEmpty() || confirm.isEmpty()) {
                showError("All password fields are required.");
                return;
            }
            if (newPw.length() < 8 || newPw.length() > 20) {
                showError("New password must be 8–20 characters.");
                return;
            }
            if (!newPw.equals(confirm)) {
                showError("New passwords do not match.");
                return;
            }

            // Verify current password against in-memory store
            boolean verified = userRegistry.verifyPassword(currentUsername, current);
            if (!verified) {
                showError("Current password is incorrect.");
                return;
            }

            // Update password in in-memory store
            userRegistry.updatePassword(currentUsername, newPw);
            // TODO: replace with PUT /user/{id} call to user-service

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            showSuccess("Password changed successfully.");
        });

        VerticalLayout section = new VerticalLayout(
                sectionTitle, currentPasswordField, newPasswordField, confirmPasswordField, changePasswordBtn
        );
        section.setPadding(false);
        section.setSpacing(true);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        return section;
    }

    // -------------------------------------------------------------------------
    // Preferences section (liked genres)
    // -------------------------------------------------------------------------

    private VerticalLayout buildPreferencesSection() {
        H3 sectionTitle = new H3("Preferences");
        sectionTitle.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        Span genreLabel = new Span("Liked Genres");
        genreLabel.getStyle()
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-s)");

        CheckboxGroup<String> genreGroup = new CheckboxGroup<>();
        genreGroup.setItems(ALL_GENRES.stream().sorted().toList());
        // TODO: pre-populate from user-service GET /user/{id} likedGenres field

        Button savePrefsBtn = new Button("Save Preferences");
        savePrefsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        savePrefsBtn.addClickListener(e -> {
            Set<String> selected = genreGroup.getSelectedItems();
            // TODO: send selected genres to user-service PUT /user/{id}
            showSuccess("Preferences saved! (" + selected.size() + " genre(s) selected)");
        });

        Button backBtn = new Button("← Back to Account");
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(AccountView.class)));

        HorizontalLayout actions = new HorizontalLayout(savePrefsBtn, backBtn);
        actions.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        VerticalLayout section = new VerticalLayout(
                sectionTitle, genreLabel, genreGroup, actions
        );
        section.setPadding(false);
        section.setSpacing(true);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        return section;
    }

    // -------------------------------------------------------------------------
    // Notification helpers
    // -------------------------------------------------------------------------

    private void showSuccess(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}