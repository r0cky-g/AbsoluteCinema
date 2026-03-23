package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.FavouriteMovieDTO;
import ca.yorku.eecs4314group12.ui.data.dto.MovieListItemDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewDTO;
import ca.yorku.eecs4314group12.ui.data.dto.WatchHistoryDTO;
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
 * Sections:
 *   - Profile card
 *   - Watchlist (user-service GET /user/{id}/watchlist)
 *   - Favourites (user-service GET /user/{id}/favourites)
 *   - Watch History (user-service GET /user/{id}/history)
 *   - Recommendations (user-service GET /user/{id}/recommendations)
 *   - My Reviews (review-service GET /api/reviews/user/{id})
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
            add(buildFavouritesSection(userId));
            add(buildWatchHistorySection(userId));
            add(buildRecommendationsSection(userId));
            add(buildReviewsSection(userId));
        } else {
            Paragraph note = new Paragraph(
                    "Log out and log back in to see your watchlist, favourites, and history.");
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

        Span roleSpan = new Span("Role: " + userSessionService.getRole());
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
        styleCard(card);
        return card;
    }

    // -------------------------------------------------------------------------
    // Watchlist
    // -------------------------------------------------------------------------

    private VerticalLayout buildWatchlistSection(long userId) {
        H3 heading = sectionHeading("My Watchlist");
        List<WatchlistDTO> watchlist = backendClient.getWatchlist(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        styleCard(section);

        if (watchlist.isEmpty()) {
            section.add(emptyMessage("Your watchlist is empty. Add movies from any movie page!"));
            return section;
        }

        FlexLayout grid = movieGrid();
        for (WatchlistDTO item : watchlist) {
            grid.add(buildRemovableMovieCard(
                    item.getMovieId(),
                    () -> {
                        backendClient.removeFromWatchlist(userId, item.getMovieId());
                        getUI().ifPresent(ui -> ui.navigate(AccountView.class));
                    }
            ));
        }
        section.add(grid);
        return section;
    }

    // -------------------------------------------------------------------------
    // Favourites
    // -------------------------------------------------------------------------

    private VerticalLayout buildFavouritesSection(long userId) {
        H3 heading = sectionHeading("My Favourites");
        List<FavouriteMovieDTO> favourites = backendClient.getFavourites(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        styleCard(section);

        if (favourites.isEmpty()) {
            section.add(emptyMessage("No favourites yet. Heart a movie from its detail page!"));
            return section;
        }

        FlexLayout grid = movieGrid();
        for (FavouriteMovieDTO item : favourites) {
            grid.add(buildRemovableMovieCard(
                    item.getMovieId(),
                    () -> {
                        backendClient.removeFromFavourites(userId, item.getMovieId());
                        getUI().ifPresent(ui -> ui.navigate(AccountView.class));
                    }
            ));
        }
        section.add(grid);
        return section;
    }

    // -------------------------------------------------------------------------
    // Watch History
    // -------------------------------------------------------------------------

    private VerticalLayout buildWatchHistorySection(long userId) {
        H3 heading = sectionHeading("Watch History");
        List<WatchHistoryDTO> history = backendClient.getWatchHistory(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        styleCard(section);

        if (history.isEmpty()) {
            section.add(emptyMessage("No watch history yet. Mark movies as watched from their detail page!"));
            return section;
        }

        FlexLayout grid = movieGrid();
        for (WatchHistoryDTO item : history) {
            grid.add(buildRemovableMovieCard(
                    item.getMovieId(),
                    () -> {
                        backendClient.removeFromWatchHistory(userId, item.getMovieId());
                        getUI().ifPresent(ui -> ui.navigate(AccountView.class));
                    }
            ));
        }
        section.add(grid);
        return section;
    }

    // -------------------------------------------------------------------------
    // Recommendations
    // -------------------------------------------------------------------------

    private VerticalLayout buildRecommendationsSection(long userId) {
        H3 heading = sectionHeading("Recommended for You ✨");
        List<MovieListItemDTO> recommendations = backendClient.getRecommendations(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        styleCard(section);

        if (recommendations.isEmpty()) {
            section.add(emptyMessage(
                    "Add movies to your watchlist to get personalised recommendations!"));
            return section;
        }

        section.add(buildMovieListGrid(recommendations));
        return section;
    }

    // -------------------------------------------------------------------------
    // Reviews
    // -------------------------------------------------------------------------

    private VerticalLayout buildReviewsSection(long userId) {
        H3 heading = sectionHeading("My Reviews");
        List<ReviewDTO> reviews = backendClient.getReviewsForUser(userId);

        VerticalLayout section = new VerticalLayout(heading);
        section.setPadding(true); section.setSpacing(true);
        styleCard(section);

        if (reviews.isEmpty()) {
            section.add(emptyMessage("You haven't written any reviews yet."));
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
    // Shared card builder for watchlist/favourites/history
    // -------------------------------------------------------------------------

    private Div buildRemovableMovieCard(int movieId, Runnable onRemove) {
        Div wrapper = new Div();
        wrapper.getStyle().set("position", "relative");

        Div card = new Div();
        card.getStyle().set("cursor", "pointer");
        card.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("movie/" + movieId)));

        // Use movie-service directly (cached) instead of api-service to avoid TMDB roundtrips
        backendClient.getMovieSummary(movieId).ifPresentOrElse(movie -> {
            String posterPath = movie.getPoster_path();
            if (posterPath != null && !posterPath.isBlank()) {
                String url = posterPath.startsWith("http") ? posterPath : TMDB_IMAGE_BASE + posterPath;
                Image img = new Image(url, movie.getTitle() != null ? movie.getTitle() : "");
                img.getStyle().set("width", "100%")
                        .set("border-radius", "var(--lumo-border-radius-m)")
                        .set("display", "block");
                Span title = new Span(movie.getTitle());
                title.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                        .set("display", "block").set("margin-top", "4px");
                card.add(img, title);
            } else {
                addFallbackCard(card, "Movie #" + movieId);
            }
        }, () -> addFallbackCard(card, "Movie #" + movieId));

        Button removeBtn = new Button(VaadinIcon.CLOSE_SMALL.create());
        removeBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        removeBtn.getStyle().set("position", "absolute").set("top", "0").set("right", "0");
        removeBtn.addClickListener(e -> {
            onRemove.run();
            Notification.show("Removed.", 2000, Notification.Position.BOTTOM_START);
        });

        wrapper.add(card, removeBtn);
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

    private FlexLayout buildMovieListGrid(List<MovieListItemDTO> movies) {
        FlexLayout grid = movieGrid();
        for (MovieListItemDTO movie : movies) {
            Div card = new Div();
            card.getStyle().set("cursor", "pointer");
            card.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate("movie/" + movie.getId())));
            String posterPath = movie.getPoster_path();
            if (posterPath != null && !posterPath.isBlank()) {
                String url = posterPath.startsWith("http") ? posterPath : TMDB_IMAGE_BASE + posterPath;
                Image img = new Image(url, movie.getTitle());
                img.getStyle().set("width", "100%")
                        .set("border-radius", "var(--lumo-border-radius-m)")
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
                fallback.add(new Span(movie.getTitle() != null && !movie.getTitle().isBlank()
                        ? movie.getTitle().substring(0, 1) : "?"));
                card.add(fallback);
            }
            grid.add(card);
        }
        return grid;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private FlexLayout movieGrid() {
        FlexLayout grid = new FlexLayout();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(120px, 1fr))")
                .set("gap", "var(--lumo-space-m)").set("width", "100%");
        return grid;
    }

    private H3 sectionHeading(String text) {
        H3 h = new H3(text);
        h.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");
        return h;
    }

    private Paragraph emptyMessage(String text) {
        Paragraph p = new Paragraph(text);
        p.getStyle().set("color", "var(--lumo-secondary-text-color)");
        return p;
    }

    private void styleCard(VerticalLayout card) {
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("margin-bottom", "var(--lumo-space-l)");
    }
}