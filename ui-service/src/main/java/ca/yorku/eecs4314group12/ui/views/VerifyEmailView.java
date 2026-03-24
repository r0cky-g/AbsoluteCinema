package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Email verification page — shown after registration.
 * Displays 4 individual digit input boxes and submits the code to user-service.
 */
@Route("verify")
@PageTitle("Verify Email | Absolute Cinema")
@AnonymousAllowed
public class VerifyEmailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final BackendClientService backendClient;
    private Long userId;

    public VerifyEmailView(BackendClientService backendClient) {
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
                .set("margin-top", "0.25rem").set("margin-bottom", "var(--lumo-space-l)");

        Paragraph instructions = new Paragraph("Enter the 4-digit code sent to your email address.");
        instructions.getStyle().set("color", "var(--lumo-secondary-text-color)").set("text-align", "center");

        // 4 individual digit boxes
        TextField[] digits = new TextField[4];
        for (int i = 0; i < 4; i++) {
            digits[i] = new TextField();
            digits[i].setMaxLength(1);
            digits[i].getStyle()
                    .set("width", "60px")
                    .set("text-align", "center")
                    .set("font-size", "1.5rem")
                    .set("font-weight", "bold");
            digits[i].getElement().setAttribute("inputmode", "numeric");

            final int idx = i;
            digits[i].addValueChangeListener(e -> {
                String val = e.getValue();
                if (val.length() > 1) {
                    digits[idx].setValue(val.substring(val.length() - 1));
                }
                if (!val.isEmpty() && idx < 3) {
                    digits[idx + 1].focus();
                }
            });
        }

        HorizontalLayout digitRow = new HorizontalLayout(digits);
        digitRow.setSpacing(true);
        digitRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button verifyBtn = new Button("Verify Email");
        verifyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        verifyBtn.setWidth("100%");
        verifyBtn.setMaxWidth("360px");

        verifyBtn.addClickListener(e -> {
            StringBuilder code = new StringBuilder();
            for (TextField digit : digits) {
                code.append(digit.getValue().trim());
            }
            if (code.length() != 4 || !code.toString().matches("\\d{4}")) {
                Notification.show("Please enter all 4 digits.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            boolean success = backendClient.verifyEmail(userId, code.toString());
            if (success) {
                Notification.show("Email verified! You can now log in.", 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            } else {
                Notification.show("Invalid code. Please try again.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        VerticalLayout form = new VerticalLayout(instructions, digitRow, verifyBtn);
        form.setPadding(true);
        form.setSpacing(true);
        form.setAlignItems(FlexComponent.Alignment.CENTER);
        form.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("width", "100%").set("max-width", "420px");

        add(appName, heading, form);
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.userId = parameter;
    }
}
