package ca.yorku.eecs4314group12.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Login page — the entry point for unauthenticated users.
 *
 * Vaadin's built-in LoginForm handles the POST to /login automatically
 * when wired through VaadinWebSecurity (see SecurityConfig).
 *
 * Placeholder credentials (see SecurityConfig):
 *   username: alice  password: password
 *   username: bob    password: password
 */
@Route("login")
@PageTitle("Login | Absolute Cinema")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        H1 appName = new H1("🎬 Absolute Cinema");
        appName.getStyle()
                .set("margin-bottom", "0")
                .set("font-size", "2rem");

        H2 tagline = new H2("Your movie reviews, your way.");
        tagline.getStyle()
                .set("font-size", "1rem")
                .set("font-weight", "normal")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0.25rem")
                .set("margin-bottom", "var(--lumo-space-l)");

        loginForm.setAction("login");

        Paragraph hint = new Paragraph("Demo: alice / password  or  bob / password");
        hint.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "var(--lumo-space-s)");

        add(appName, tagline, loginForm, hint);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Show an error banner if Spring Security redirected here after a failed login
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}