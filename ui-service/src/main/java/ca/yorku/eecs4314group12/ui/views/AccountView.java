package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.DummyDataService;
import ca.yorku.eecs4314group12.ui.data.Review;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Account / profile landing page.
 *
 * Displays the logged-in user's profile info (placeholder) and their recent
 * reviews (placeholder dummy data).
 *
 * TODO: Replace dummy data calls with actual user-service and review-service
 *       REST calls once the API contracts are finalized.
 */
@Route(value = "account", layout = MainLayout.class)
@PageTitle("My Account | Absolute Cinema")
@PermitAll
public class AccountView extends VerticalLayout {

    private final DummyDataService dataService;

    public AccountView(DummyDataService dataService) {
        this.dataService = dataService;
        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "900px").set("margin", "0 auto");

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        add(buildProfileCard(username), buildReviewsSection(username));
    }

    // -------------------------------------------------------------------------
    // Profile card
    // -------------------------------------------------------------------------

    private HorizontalLayout buildProfileCard(String username) {
        Avatar avatar = new Avatar(username);
        avatar.setColorIndex(2);
        avatar.getStyle()
                .set("width", "80px")
                .set("height", "80px")
                .set("font-size", "2rem");

        H2 nameHeading = new H2(username);
        nameHeading.getStyle().set("margin", "0");

        // Placeholder fields — replace with real user data from user-service
        Span emailSpan = new Span(VaadinIcon.ENVELOPE.create().getElement().toString()
                + "  " + username + "@example.com");
        emailSpan.getStyle().set("color", "var(--lumo-secondary-text-color)")
                            .set("font-size", "var(--lumo-font-size-s)");

        Span joinedSpan = new Span("Member since January 2024  ·  3 reviews");
        joinedSpan.getStyle().set("color", "var(--lumo-secondary-text-color)")
                             .set("font-size", "var(--lumo-font-size-s)");

        Button editBtn = new Button("Edit Profile", VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        // TODO: open an edit dialog once user-service PATCH endpoint is available

        VerticalLayout info = new VerticalLayout(nameHeading, emailSpan, joinedSpan, editBtn);
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("gap", "var(--lumo-space-xs)");

        HorizontalLayout card = new HorizontalLayout(avatar, info);
        card.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        card.setSpacing(true);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        card.setWidth("100%");

        return card;
    }

    // -------------------------------------------------------------------------
    // Recent reviews
    // -------------------------------------------------------------------------

    private VerticalLayout buildReviewsSection(String username) {
        H3 heading = new H3("My Recent Reviews");
        heading.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(false);
        section.setSpacing(false);

        for (Review review : dataService.getReviewsForUser(username)) {
            section.add(buildReviewCard(review));
        }

        return section;
    }

    private Div buildReviewCard(Review review) {
        Span movieTitle = new Span(review.getMovieTitle());
        movieTitle.getStyle().set("font-weight", "600")
                             .set("font-size", "var(--lumo-font-size-m)");

        Span stars = new Span(buildStarString(review.getStars()));
        stars.getStyle().set("color", "var(--lumo-primary-color)")
                        .set("font-size", "var(--lumo-font-size-m)");

        HorizontalLayout titleRow = new HorizontalLayout(movieTitle, stars);
        titleRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        titleRow.setSpacing(true);

        Paragraph body = new Paragraph(review.getBody());
        body.getStyle()
                .set("margin", "var(--lumo-space-xs) 0")
                .set("color", "var(--lumo-body-text-color)");

        Span date = new Span("Posted " + review.getDatePosted());
        date.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                       .set("color", "var(--lumo-secondary-text-color)");

        Div card = new Div(titleRow, body, date);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("margin-bottom", "var(--lumo-space-m)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        card.setWidth("100%");

        return card;
    }

    private String buildStarString(int stars) {
        return "★".repeat(stars) + "☆".repeat(Math.max(0, 5 - stars));
    }
}