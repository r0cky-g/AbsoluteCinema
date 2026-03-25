package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.UserResponseDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Lists all users and lets the platform administrator promote or demote moderators.
 * Uses user-service role endpoints; password confirms each action.
 */
@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin | Absolute Cinema")
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {

    public AdminView(BackendClientService backendClient) {
        setSizeFull();
        setPadding(true);
        getStyle().set("max-width", "1100px").set("margin", "0 auto");

        H2 heading = new H2("User administration");
        Paragraph hint = new Paragraph(
                "Grant or remove moderator privileges (forum posts and comments). "
                        + "Enter your administrator password before each action.");
        hint.getStyle().set("color", "var(--lumo-secondary-text-color)").set("max-width", "720px");

        PasswordField adminPassword = new PasswordField("Your administrator password");
        adminPassword.setWidth("360px");

        Grid<UserResponseDTO> grid = new Grid<>(UserResponseDTO.class, false);
        grid.addColumn(UserResponseDTO::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(UserResponseDTO::getUsername).setHeader("Username").setFlexGrow(1);
        grid.addColumn(UserResponseDTO::getEmail).setHeader("Email").setFlexGrow(1);
        grid.addColumn(UserResponseDTO::getRole).setHeader("Role").setAutoWidth(true);

        grid.addComponentColumn(user -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            if ("ADMIN".equals(user.getRole())) {
                return actions;
            }
            String adminIdentifier = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("MODERATOR".equals(user.getRole())) {
                Button demote = new Button("Remove moderator");
                demote.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
                demote.addClickListener(e -> {
                    if (adminPassword.getValue() == null || adminPassword.getValue().isBlank()) {
                        Notification.show("Enter your password first.", 3000, Notification.Position.MIDDLE);
                        return;
                    }
                    backendClient.demoteModerator(user.getId(), adminIdentifier, adminPassword.getValue())
                            .ifPresentOrElse(
                                    u -> {
                                        refreshGrid(grid, backendClient);
                                        Notification.show("User is now a standard account.", 2500,
                                                Notification.Position.BOTTOM_START)
                                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                    },
                                    () -> Notification.show(
                                            "Could not demote. Check password or try again.", 4000,
                                            Notification.Position.MIDDLE)
                                            .addThemeVariants(NotificationVariant.LUMO_ERROR));
                });
                actions.add(demote);
            } else {
                Button promote = new Button("Make moderator");
                promote.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
                promote.addClickListener(e -> {
                    if (adminPassword.getValue() == null || adminPassword.getValue().isBlank()) {
                        Notification.show("Enter your password first.", 3000, Notification.Position.MIDDLE);
                        return;
                    }
                    backendClient.promoteToModerator(user.getId(), adminIdentifier, adminPassword.getValue())
                            .ifPresentOrElse(
                                    u -> {
                                        refreshGrid(grid, backendClient);
                                        Notification.show("User can now moderate forum content.", 2500,
                                                Notification.Position.BOTTOM_START)
                                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                    },
                                    () -> Notification.show(
                                            "Could not promote. Check password or try again.", 4000,
                                            Notification.Position.MIDDLE)
                                            .addThemeVariants(NotificationVariant.LUMO_ERROR));
                });
                actions.add(promote);
            }
            return actions;
        }).setHeader("Actions").setWidth("280px").setFlexGrow(0);

        refreshGrid(grid, backendClient);

        Button reload = new Button("Reload users", e -> refreshGrid(grid, backendClient));
        reload.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        add(heading, hint, adminPassword, reload, grid);
        grid.setWidthFull();
        setFlexGrow(1, grid);
    }

    private static void refreshGrid(Grid<UserResponseDTO> grid, BackendClientService backendClient) {
        grid.setItems(backendClient.listAllUsers());
    }
}
