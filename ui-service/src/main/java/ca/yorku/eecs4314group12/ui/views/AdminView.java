package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.UserResponseDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/**
 * Administrator-only: list users and assign USER / MODERATOR / ADMIN roles.
 */
@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin | Absolute Cinema")
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {

    private final BackendClientService backendClient;
    private final VerticalLayout userRows = new VerticalLayout();

    public AdminView(BackendClientService backendClient) {
        this.backendClient = backendClient;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "960px").set("margin", "0 auto");

        H2 title = new H2("User management");
        title.getStyle().set("margin", "0");

        Paragraph hint = new Paragraph(
                "Promote or demote users. Moderators can remove any forum post, comment, or review. "
                        + "The built-in ADMIN account cannot be demoted.");
        hint.getStyle().set("color", "var(--lumo-secondary-text-color)").set("margin", "0");

        Button refreshBtn = new Button("Refresh list");
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshBtn.addClickListener(e -> loadUsers());

        userRows.setPadding(false);
        userRows.setSpacing(true);

        add(title, hint, refreshBtn, userRows);
        loadUsers();
    }

    private void loadUsers() {
        userRows.removeAll();
        for (UserResponseDTO u : backendClient.getAllUsers()) {
            userRows.add(buildUserRow(u));
        }
        if (userRows.getComponentCount() == 0) {
            userRows.add(new Span("No users returned from the server."));
        }
    }

    private HorizontalLayout buildUserRow(UserResponseDTO u) {
        Span name = new Span(u.getUsername() != null ? u.getUsername() : "(no name)");
        name.getStyle().set("min-width", "7rem").set("font-weight", "500");

        Span email = new Span(u.getEmail() != null ? u.getEmail() : "—");
        email.getStyle().set("flex", "1").set("color", "var(--lumo-secondary-text-color)");

        Select<String> roleSelect = new Select<>("USER", "MODERATOR", "ADMIN");
        String current = u.getRole() != null ? u.getRole() : "USER";
        roleSelect.setValue(current);

        boolean builtInAdmin = "ADMIN".equalsIgnoreCase(u.getUsername());
        if (builtInAdmin) {
            roleSelect.setReadOnly(true);
        }

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        save.setEnabled(!builtInAdmin);
        save.addClickListener(e -> {
            String newRole = roleSelect.getValue();
            backendClient.updateUserRole(u.getId(), newRole)
                    .ifPresentOrElse(
                            updated -> {
                                Notification.show("Role updated for " + updated.getUsername(),
                                        3500, Notification.Position.BOTTOM_START)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                loadUsers();
                            },
                            () -> Notification.show(
                                    "Could not update role (forbidden, invalid, or server error).",
                                    4500, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR));
        });

        HorizontalLayout row = new HorizontalLayout(name, email, roleSelect, save);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setFlexGrow(1, email);
        return row;
    }
}
