package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.security.InMemoryUserRegistry;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Registration page — creates accounts via user-service POST /user/register.
 * Mirrors the user into InMemoryUserRegistry so Spring Security can log them in.
 */
@Route("register")
@PageTitle("Create Account | Absolute Cinema")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final InMemoryUserRegistry userRegistry;
    private final BackendClientService backendClient;

    public RegisterView(InMemoryUserRegistry userRegistry, BackendClientService backendClient) {
        this.userRegistry = userRegistry;
        this.backendClient = backendClient;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        H1 appName = new H1("🎬 Absolute Cinema");
        appName.getStyle().set("margin-bottom", "0").set("font-size", "2rem");

        H2 heading = new H2("Create an account");
        heading.getStyle()
                .set("font-size", "1rem").set("font-weight", "normal")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0.25rem").set("margin-bottom", "var(--lumo-space-l)");

        TextField usernameField = new TextField("Username");
        usernameField.setWidth("100%"); usernameField.setMaxWidth("360px");
        usernameField.setHelperText("8–16 characters, letters/numbers/underscores only");

        EmailField emailField = new EmailField("Email");
        emailField.setWidth("100%"); emailField.setMaxWidth("360px");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth("100%"); passwordField.setMaxWidth("360px");
        passwordField.setHelperText("8–20 characters");

        PasswordField confirmPasswordField = new PasswordField("Confirm Password");
        confirmPasswordField.setWidth("100%"); confirmPasswordField.setMaxWidth("360px");

        Checkbox over18Box = new Checkbox("I confirm I am 18 or older");

        Checkbox moderatorBox = new Checkbox("Create as moderator");

        Button registerBtn = new Button("Create Account");
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerBtn.setWidth("100%"); registerBtn.setMaxWidth("360px");

        registerBtn.addClickListener(e -> {
            String username = usernameField.getValue().trim();
            String email = emailField.getValue().trim();
            String password = passwordField.getValue();
            String confirm = confirmPasswordField.getValue();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("All fields are required."); return;
            }
            if (username.length() < 8 || username.length() > 16) {
                showError("Username must be 8–16 characters."); return;
            }
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                showError("Username can only contain letters, numbers, and underscores."); return;
            }
            if (password.length() < 8 || password.length() > 20) {
                showError("Password must be 8–20 characters."); return;
            }
            if (!password.equals(confirm)) {
                showError("Passwords do not match."); return;
            }

            boolean isModerator = moderatorBox.getValue();
            boolean serviceSuccess = backendClient.registerUser(username, password, email);
            if (serviceSuccess) {
                userRegistry.register(username, password, email);
                Notification.show("Account created! You can now log in.",
                        4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            } else {
                showError("Registration failed. Username or email may already be taken.");
            }
        });

        Paragraph loginPrompt = new Paragraph();
        loginPrompt.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "var(--lumo-space-s)");
        loginPrompt.setText("Already have an account? ");
        Anchor loginLink = new Anchor("/login", "Log in here");
        loginLink.getStyle().set("color", "var(--lumo-primary-color)");
        loginPrompt.add(loginLink);

        VerticalLayout form = new VerticalLayout(
                usernameField, emailField, passwordField, confirmPasswordField, over18Box, moderatorBox, registerBtn);
        form.setPadding(true); form.setSpacing(true);
        form.setAlignItems(FlexComponent.Alignment.CENTER);
        form.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("width", "100%").set("max-width", "420px");

        add(appName, heading, form, loginPrompt);
    }

    private void showError(String message) {
        Notification.show(message, 4000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}