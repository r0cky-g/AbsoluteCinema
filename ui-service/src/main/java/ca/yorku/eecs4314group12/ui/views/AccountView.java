package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.MovieListItemDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewDTO;
import ca.yorku.eecs4314group12.ui.data.dto.WatchlistDTO;
import ca.yorku.eecs4314group12.ui.security.UserSessionService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Account / profile page.
 *
 * Shows:
 *   - Profile info (username, email from UserSessionService)
 *   - Watchlist (from user-service GET /user/{id}/watchlist)
 *   - Recommendations (from user-service GET /user/{id}/recommendations)
 *   - Recent reviews (from review-service GET /api/reviews/user/{id})
 */
@Route(value = "account", layout = MainLayout.class)
@PageTitle("My Account | Absolute Cinema")
@PermitAll
public class AccountView extends VerticalLayout {

    private static final String TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w185";

    private final BackendClientService backendClient;
    private final UserSessionService userSessionService;

    public AccountView(BackendClientService backendClient, UserSessionService userSessionService) {
        this.backendClient = backendClient;
        this.userSessionService = userSessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "900px").set("margin", "0 auto");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userSessionService.getUserId();

        add(buildProfileCard(username));

        if (userId != null) {
            add(buildWatchlistSection(userId));
            add(buildRecommendationsSection(userId));
            add(buildReviewsSection(userId));
        } else {
            Paragraph note = new Paragraph(
                    "Log out and log back in to see your watchlist and recommendations.");
            note.getStyle().set("color", "var(--lumo-secondary-text-color)");
            add(note);
        }
    }

    // -------------------------------------------------------------------------
    // Profile card
    // -------------------------------------------------------------------------

    private VerticalLayout buildProfileCard(String username) {
        Avatar avatar = new Avatar(username);
        avatar.setColorIndex(2);
        avatar.getStyle().set("width", "80px").set("height", "80px").set("font-size", "2rem");

        H2 nameHeading = new H2(username);
        nameHeading.getStyle().set("margin", "0");

        String email = userSessionService.getEmail() != null
                ? userSessionService.getEmail() : username + "@example.com";
        Span emailSpan = new Span("✉  " + email);
        emailSpan.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        String role = userSessionService.getRole();
        Span roleSpan = new Span("Role: " + role);
        roleSpan.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Button editBtn = new Button("Edit Profile", VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(EditProfileView.class)));

        HorizontalLayout avatarRow = new HorizontalLayout(avatar,
                new VerticalLayout(nameHeading, emailSpan, roleSpan));
        avatarRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        avatarRow.setSpacing(true);

        VerticalLayout card = new VerticalLayout(avatarRow, editBtn);
        card.setPadding(true); card.setSpacing(true);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("margin-bottom", "var(--lumo-space-l)");
        return card;
    }

    // -------------------------------------------------------------------------
    // Watchlist section
    // -------------------------------------------------------------------------

    private VerticalLayout buildWatchlistSection(long userId) {
        H3 heading = new H3("My Watchlist");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");

        List<WatchlistDTO> watchlist = backendClient.getWatchlist(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("margin-bottom", "var(--lumo-space-l)");

        if (watchlist.isEmpty()) {
            Paragraph empty = new Paragraph(
                    "Your watchlist is empty. Add movies from any movie page!");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            section.add(empty);
            return section;
        }

        FlexLayout grid = new FlexLayout();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(120px, 1fr))")
                .set("gap", "var(--lumo-space-m)").set("width", "100%");

        for (WatchlistDTO item : watchlist) {
            grid.add(buildWatchlistCard(userId, item));
        }

        section.add(grid);
        return section;
    }

    private Div buildWatchlistCard(long userId, WatchlistDTO item) {
        Div card = new Div();
        card.getStyle().set("cursor", "pointer").set("position", "relative");

        // Try to get poster from movie-service
        backendClient.getMovieById(item.getMovieId()).ifPresentOrElse(movie -> {
            String posterPath = movie.getPoster_path();
            if (posterPath != null && !posterPath.isBlank()) {
                String url = posterPath.startsWith("http") ? posterPath : TMDB_IMAGE_BASE + posterPath;
                Image img = new Image(url, movie.getTitle());
                img.getStyle().set("width", "100%").set("border-radius", "var(--lumo-border-radius-m)")
                        .set("display", "block");
                Span title = new Span(movie.getTitle());
                title.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                        .set("display", "block").set("margin-top", "4px");
                card.add(img, title);
            } else {
                addFallbackCard(card, "Movie #" + item.getMovieId());
            }
            card.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate("movie/" + item.getMovieId())));
        }, () -> addFallbackCard(card, "Movie #" + item.getMovieId()));

        // Remove button
        Button removeBtn = new Button(VaadinIcon.CLOSE_SMALL.create());
        removeBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        removeBtn.getStyle().set("position", "absolute").set("top", "0").set("right", "0");
        removeBtn.addClickListener(e -> {
            backendClient.removeFromWatchlist(userId, item.getMovieId());
            Notification n = Notification.show("Removed from watchlist.", 2000,
                    Notification.Position.BOTTOM_START);
            // Refresh page
            getUI().ifPresent(ui -> ui.navigate(AccountView.class));
        });

        Div wrapper = new Div(card, removeBtn);
        wrapper.getStyle().set("position", "relative");
        return wrapper;
    }

    private void addFallbackCard(Div card, String label) {
        Div fallback = new Div();
        fallback.getStyle()
                .set("width", "100%").set("aspect-ratio", "2/3")
                .set("background", "var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center");
        fallback.add(new Span("🎬"));
        Span title = new Span(label);
        title.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("display", "block").set("margin-top", "4px");
        card.add(fallback, title);
    }

    // -------------------------------------------------------------------------
    // Recommendations section
    // -------------------------------------------------------------------------

    private VerticalLayout buildRecommendationsSection(long userId) {
        H3 heading = new H3("Recommended for You");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");

        List<MovieListItemDTO> recommendations = backendClient.getRecommendations(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("margin-bottom", "var(--lumo-space-l)");

        if (recommendations.isEmpty()) {
            Paragraph empty = new Paragraph(
                    "Add movies to your watchlist to get personalised recommendations!");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            section.add(empty);
            return section;
        }

        section.add(buildMovieGrid(recommendations));
        return section;
    }

    // -------------------------------------------------------------------------
    // Reviews section
    // -------------------------------------------------------------------------

    private VerticalLayout buildReviewsSection(long userId) {
        H3 heading = new H3("My Reviews");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");

        List<ReviewDTO> reviews = backendClient.getReviewsForUser(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("margin-bottom", "var(--lumo-space-l)");

        if (reviews.isEmpty()) {
            Paragraph empty = new Paragraph("You haven't written any reviews yet.");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            section.add(empty);
            return section;
        }

        for (ReviewDTO review : reviews) {
            Div card = new Div();
            card.getStyle()
                    .set("background", "var(--lumo-contrast-5pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-m)")
                    .set("margin-bottom", "var(--lumo-space-s)");

            H4 title = new H4("★ " + review.getRating() + "/10 — " + review.getTitle());
            title.getStyle().set("margin", "0 0 var(--lumo-space-xs) 0");

            Paragraph content = new Paragraph(review.getContent());
            content.getStyle().set("margin", "0").set("color", "var(--lumo-secondary-text-color)");

            card.add(title, content);
            section.add(card);
        }

        return section;
    }

    // -------------------------------------------------------------------------
    // Shared movie grid (same style as HomeView)
    // -------------------------------------------------------------------------

    private FlexLayout buildMovieGrid(List<MovieListItemDTO> movies) {
        FlexLayout grid = new FlexLayout();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(140px, 1fr))")
                .set("gap", "var(--lumo-space-m)").set("width", "100%");

        for (MovieListItemDTO movie : movies) {
            Div card = new Div();
            card.getStyle().set("cursor", "pointer");
            card.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate("movie/" + movie.getId())));

            String posterPath = movie.getPoster_path();
            if (posterPath != null && !posterPath.isBlank()) {
                String url = posterPath.startsWith("http")
                        ? posterPath : "https://image.tmdb.org/t/p/w185" + posterPath;
                Image img = new Image(url, movie.getTitle());
                img.getStyle().set("width", "100%").set("border-radius", "var(--lumo-border-radius-m)")
                        .set("display", "block");
                Span title = new Span(movie.getTitle());
                title.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                        .set("display", "block").set("margin-top", "4px");
                card.add(img, title);
            } else {
                Div fallback = new Div();
                fallback.getStyle()
                        .set("width", "100%").set("aspect-ratio", "2/3")
                        .set("background", "var(--lumo-contrast-10pct)")
                        .set("border-radius", "var(--lumo-border-radius-m)")
                        .set("display", "flex").set("align-items", "center")
                        .set("justify-content", "center").set("font-size", "2rem");
                fallback.add(new Span(movie.getTitle().substring(0, 1)));
                card.add(fallback);
            }

            grid.add(card);
        }

        return grid;
    }
}