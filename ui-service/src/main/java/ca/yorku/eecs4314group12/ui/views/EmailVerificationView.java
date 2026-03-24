package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Email verification page — route /verify?userId={id}
 *
 * Shown after registration. The user enters the 4-digit code
 * sent to their email. On success they are redirected to login.
 *
 * The userId is passed as a query parameter from RegisterView.
 */
@Route("verify")
@PageTitle("Verify Email | Absolute Cinema")
@AnonymousAllowed
public class EmailVerificationView extends VerticalLayout implements HasUrlParameter<String> {

    private final BackendClientService backendClient;
    private Long userId;
    private final TextField codeField;
    private final Span errorMsg;

    public EmailVerificationView(BackendClientService backendClient) {
        this.backendClient = backendClient;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        H1 appName = new H1("🎬 Absolute Cinema");
        appName.getStyle().set("margin-bottom", "0").set("font-size", "2rem");

        H2 heading = new H2("Verify your email");
        heading.getStyle()
                .set("font-size", "1rem").set("font-weight", "normal")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0.25rem").set("margin-bottom", "var(--lumo-space-s)");

        Paragraph instructions = new Paragraph(
                "We sent a 4-digit verification code to your email address. " +
                "Enter it below to activate your account.");
        instructions.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("text-align", "center")
                .set("max-width", "360px")
                .set("margin", "0 0 var(--lumo-space-m) 0");

        codeField = new TextField("Verification Code");
        codeField.setPlaceholder("Enter 4-digit code");
        codeField.setMaxLength(4);
        codeField.setWidth("100%"); codeField.setMaxWidth("360px");
        codeField.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> submitCode());

        errorMsg = new Span();
        errorMsg.getStyle()
                .set("color", "var(--lumo-error-color)")
                .set("font-size", "var(--lumo-font-size-s)");
        errorMsg.setVisible(false);

        Button verifyBtn = new Button("Verify Email");
        verifyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        verifyBtn.setWidth("100%"); verifyBtn.setMaxWidth("360px");
        verifyBtn.addClickListener(e -> submitCode());

        Button backBtn = new Button("Back to Login");
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));

        VerticalLayout form = new VerticalLayout(
                instructions, codeField, errorMsg, verifyBtn, backBtn);
        form.setPadding(true); form.setSpacing(true);
        form.setAlignItems(FlexComponent.Alignment.CENTER);
        form.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("width", "100%").set("max-width", "420px");

        add(appName, heading, form);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        // Read userId from query param: /verify?userId=123
        var queryParams = event.getLocation().getQueryParameters().getParameters();
        if (queryParams.containsKey("userId")) {
            try {
                userId = Long.parseLong(queryParams.get("userId").get(0));
            } catch (NumberFormatException e) {
                userId = null;
            }
        }

        if (userId == null) {
            // No userId — show error and redirect to register
            Notification.show("Invalid verification link. Please register again.",
                    4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.rerouteTo(RegisterView.class);
        }
    }

    private void submitCode() {
        String code = codeField.getValue().trim();

        if (code.isEmpty()) {
            showError("Please enter the verification code."); return;
        }
        if (!code.matches("\\d{4}")) {
            showError("Code must be exactly 4 digits."); return;
        }
        if (userId == null) {
            showError("Session expired. Please register again."); return;
        }

        boolean success = backendClient.verifyEmail(userId, code);
        if (success) {
            Notification.show("Email verified! You can now log in.",
                    4000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        } else {
            showError("Invalid code. Please check your email and try again.");
        }
    }

    private void showError(String message) {
        errorMsg.setText(message);
        errorMsg.setVisible(true);
    }
}