package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.security.InMemoryUserRegistry;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Anchor;
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
 * Registration page — allows new users to create an account.
 *
 * Accounts are stored in memory for the duration of the app session only.
 * TODO: Replace InMemoryUserRegistry.register() with a POST to user-service
 *       once the API contract is finalized.
 */
@Route("register")
@PageTitle("Create Account | Absolute Cinema")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final InMemoryUserRegistry userRegistry;

    public RegisterView(InMemoryUserRegistry userRegistry) {
        this.userRegistry = userRegistry;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        H1 appName = new H1("🎬 Absolute Cinema");
        appName.getStyle()
                .set("margin-bottom", "0")
                .set("font-size", "2rem");

        H2 heading = new H2("Create an account");
        heading.getStyle()
                .set("font-size", "1rem")
                .set("font-weight", "normal")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0.25rem")
                .set("margin-bottom", "var(--lumo-space-l)");

        // Form fields
        TextField usernameField = new TextField("Username");
        usernameField.setWidth("100%");
        usernameField.setMaxWidth("360px");

        EmailField emailField = new EmailField("Email");
        emailField.setWidth("100%");
        emailField.setMaxWidth("360px");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth("100%");
        passwordField.setMaxWidth("360px");

        PasswordField confirmPasswordField = new PasswordField("Confirm Password");
        confirmPasswordField.setWidth("100%");
        confirmPasswordField.setMaxWidth("360px");

        Button registerBtn = new Button("Create Account");
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerBtn.setWidth("100%");
        registerBtn.setMaxWidth("360px");

        registerBtn.addClickListener(e -> {
            String username = usernameField.getValue().trim();
            String email = emailField.getValue().trim();
            String password = passwordField.getValue();
            String confirm = confirmPasswordField.getValue();

            // Basic validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("All fields are required.");
                return;
            }
            if (username.length() < 3) {
                showError("Username must be at least 3 characters.");
                return;
            }
            if (!password.equals(confirm)) {
                showError("Passwords do not match.");
                return;
            }
            if (password.length() < 6) {
                showError("Password must be at least 6 characters.");
                return;
            }
            if (userRegistry.usernameExists(username)) {
                showError("Username \"" + username + "\" is already taken.");
                return;
            }

            boolean success = userRegistry.register(username, password, email);
            if (success) {
                Notification.show("Account created! You can now log in.",
                        4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            } else {
                showError("Registration failed. Please try again.");
            }
        });

        // Back to login link
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
                usernameField, emailField, passwordField, confirmPasswordField, registerBtn
        );
        form.setPadding(true);
        form.setSpacing(true);
        form.setAlignItems(FlexComponent.Alignment.CENTER);
        form.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("width", "100%")
                .set("max-width", "420px");

        add(appName, heading, form, loginPrompt);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 4000,
                Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}