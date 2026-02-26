package ca.yorku.eecs4314group12.ui.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Main application layout — provides the top navbar and side drawer
 * that wrap every authenticated view.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("🎬 Absolute Cinema");
        logo.getStyle()
                .set("font-size", "1.2rem")
                .set("margin", "0")
                .set("color", "var(--lumo-primary-text-color)");

        // Spacer
        Span spacer = new Span();
        spacer.getStyle().set("flex", "1");

        // Current user
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Avatar avatar = new Avatar(username);
        avatar.setColorIndex(2);
        avatar.getStyle().set("cursor", "pointer");

        Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        logoutBtn.getStyle().set("color", "var(--lumo-secondary-text-color)");
        logoutBtn.addClickListener(e -> {
            // Vaadin + Spring Security logout
            getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"));
        });

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(), logo, spacer, avatar, logoutBtn
        );
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.getStyle().set("padding", "0 var(--lumo-space-m)");

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();

        SideNavItem homeItem = new SideNavItem("Home", HomeView.class,
                VaadinIcon.HOME.create());
        SideNavItem accountItem = new SideNavItem("My Account", AccountView.class,
                VaadinIcon.USER.create());

        nav.addItem(homeItem, accountItem);

        VerticalLayout drawerContent = new VerticalLayout(nav);
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);

        addToDrawer(drawerContent);
    }
}