package ca.yorku.eecs4314group12.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import ca.yorku.eecs4314group12.ui.data.BackendClientService;

/**
 * Main application shell — AppLayout with top navbar and collapsible drawer.
 *
 * Compatible with Vaadin 25 + Spring Boot 4.
 * Uses VaadinSecurityConfigurer (configured in SecurityConfig).
 *
 * @AnonymousAllowed is required here because HomeView is publicly accessible
 * and AppLayout is resolved before the child view's own annotation.
 */
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final BackendClientService backendClient;

    public MainLayout(BackendClientService backendClient) {
        this.backendClient = backendClient;
        createHeader();
        createDrawer();
    }

    // -------------------------------------------------------------------------
    // Top navbar
    // -------------------------------------------------------------------------

    private void createHeader() {
        H1 logo = new H1("🎬 Absolute Cinema");
        logo.getStyle()
                .set("font-size", "1.2rem")
                .set("margin", "0")
                .set("color", "var(--lumo-primary-text-color)")
                .set("cursor", "pointer");
        logo.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));

        Span spacer = new Span();
        spacer.getStyle().set("flex", "1");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean loggedIn = auth != null
                && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");

        HorizontalLayout header;

        if (loggedIn) {
            String username = auth.getName();

            Avatar avatar = new Avatar(username);
            avatar.setColorIndex(2);
            avatar.getStyle().set("cursor", "pointer");
            avatar.getElement().addEventListener("click",
                    e -> getUI().ifPresent(ui -> ui.navigate(AccountView.class)));

            Button editProfileBtn = new Button("Edit Profile", VaadinIcon.EDIT.create());
            editProfileBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            editProfileBtn.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate(EditProfileView.class)));

            Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            logoutBtn.addClickListener(e -> logout());

            header = new HorizontalLayout(new DrawerToggle(), logo, spacer, avatar, editProfileBtn, logoutBtn);
        } else {
            Button loginBtn = new Button("Login", VaadinIcon.SIGN_IN.create());
            loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            loginBtn.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate(LoginView.class)));

            header = new HorizontalLayout(new DrawerToggle(), logo, spacer, loginBtn);
        }

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.getStyle().set("padding", "0 var(--lumo-space-m)");

        addToNavbar(header);
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------

    private void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(),
                    null,
                    auth
            );
        }
        UI.getCurrent().navigate(HomeView.class);
        UI.getCurrent().getPage().reload();
    }

    // -------------------------------------------------------------------------
    // Side drawer
    // -------------------------------------------------------------------------

    private void createDrawer() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Forum", ForumView.class, VaadinIcon.COMMENTS.create()));
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean loggedIn = auth != null
                && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");

        if (loggedIn) {
            nav.addItem(
                new SideNavItem("My Account", AccountView.class, VaadinIcon.USER.create()),
                new SideNavItem("Edit Profile", EditProfileView.class, VaadinIcon.EDIT.create())
            );
            if (auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                nav.addItem(new SideNavItem("Admin", AdminView.class, VaadinIcon.COG.create()));
            }
        } else {
            nav.addItem(
                new SideNavItem("Login", LoginView.class, VaadinIcon.SIGN_IN.create()),
                new SideNavItem("Register", RegisterView.class, VaadinIcon.PLUS_CIRCLE.create())
            );
        }

        // Always visible at the bottom of the nav
        nav.addItem(new SideNavItem("About & Credits", AboutView.class, VaadinIcon.INFO_CIRCLE.create()));

        VerticalLayout drawerContent = new VerticalLayout(nav);
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);

        addToDrawer(drawerContent);
    }
}