package ca.yorku.eecs4314group12.ui.views;

import java.util.Comparator;
import java.util.List;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import ca.yorku.eecs4314group12.ui.data.DummyDataService;
import ca.yorku.eecs4314group12.ui.data.Movie;
import ca.yorku.eecs4314group12.ui.data.Review;

/**
 * Dynamic movie detail page — route /movie/{id}
 *
 * Sections:
 *   1. Hero banner — poster, title, tagline, genres, TMDB score, user score
 *   2. Overview
 *   3. Cast & crew
 *   4. Film details
 *   5. Reviews — top recommended + 3 most recent
 *
 * TODO: Replace DummyDataService calls with:
 *   - movie data  → WebClient GET movie-service/movie/{id}
 *   - reviews     → WebClient GET review-service/api/reviews/movie/{id}
 */
@Route(value = "movie/:movieId", layout = MainLayout.class)
@PageTitle("Movie | Absolute Cinema")
@AnonymousAllowed
public class MovieView extends VerticalLayout implements BeforeEnterObserver {

    private final DummyDataService dataService;

    public MovieView(DummyDataService dataService) {
        this.dataService = dataService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String movieIdParam = event.getRouteParameters().get("movieId").orElse("0");
        int movieId;
        try {
            movieId = Integer.parseInt(movieIdParam);
        } catch (NumberFormatException e) {
            showNotFound();
            return;
        }
        dataService.getMovieById(movieId).ifPresentOrElse(this::buildPage, this::showNotFound);
    }

    // -------------------------------------------------------------------------
    // Page assembly
    // -------------------------------------------------------------------------

    private void buildPage(Movie movie) {
        getUI().ifPresent(ui -> ui.getPage().setTitle(movie.getTitle() + " | Absolute Cinema"));
        add(buildHeroBanner(movie), buildContentArea(movie));
    }

    // -------------------------------------------------------------------------
    // Hero banner
    // -------------------------------------------------------------------------

    private Div buildHeroBanner(Movie movie) {
        // Poster placeholder
        Div poster = new Div();
        poster.setText(movie.getPosterEmoji());
        poster.getStyle()
                .set("font-size", "6rem")
                .set("width", "160px")
                .set("min-width", "160px")
                .set("height", "240px")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("box-shadow", "var(--lumo-box-shadow-m)");
        // TODO: replace with <img> sourced from TMDB image base URL + poster_path

        H1 title = new H1(movie.getTitle());
        title.getStyle()
                .set("margin", "0 0 var(--lumo-space-xs) 0")
                .set("font-size", "2rem")
                .set("color", "var(--lumo-header-text-color)");

        // Tagline
        Div taglineDiv = new Div();
        if (movie.getTagline() != null && !movie.getTagline().isBlank()) {
            Span tagline = new Span("\"" + movie.getTagline() + "\"");
            tagline.getStyle()
                    .set("font-style", "italic")
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-m)");
            taglineDiv.add(tagline);
        }

        // Genre chips
        FlexLayout genreChips = new FlexLayout();
        genreChips.getStyle().set("gap", "var(--lumo-space-xs)").set("flex-wrap", "wrap");
        for (String genre : movie.getGenres()) {
            Span chip = new Span(genre);
            chip.getStyle()
                    .set("background", "var(--lumo-primary-color-10pct)")
                    .set("color", "var(--lumo-primary-color)")
                    .set("border-radius", "999px")
                    .set("padding", "2px 10px")
                    .set("font-size", "var(--lumo-font-size-xs)")
                    .set("font-weight", "600");
            genreChips.add(chip);
        }

        // Meta row
        Span meta = new Span(
                movie.getYear() + "  ·  "
                + movie.getRuntimeFormatted() + "  ·  "
                + movie.getStatus());
        meta.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        // Score row — TMDB score + user score side by side
        HorizontalLayout scores = new HorizontalLayout(
                buildScoreBadge("🎬 TMDB", movie.getTmdbScore(), false),
                buildScoreBadge("👥 Users", movie.getUserScore(), true)
        );
        scores.setSpacing(true);
        scores.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Director
        Span directorSpan = new Span("Directed by  " + movie.getDirector());
        directorSpan.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        VerticalLayout info = new VerticalLayout(title, taglineDiv, genreChips, meta, scores, directorSpan);
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("gap", "var(--lumo-space-s)");

        HorizontalLayout heroContent = new HorizontalLayout(poster, info);
        heroContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        heroContent.setSpacing(true);
        heroContent.getStyle()
                .set("max-width", "1100px")
                .set("margin", "0 auto")
                .set("padding", "var(--lumo-space-xl) var(--lumo-space-l)");
        heroContent.setWidth("100%");

        Div hero = new Div(heroContent);
        hero.setWidth("100%");
        hero.getStyle()
                .set("background", "linear-gradient(180deg, #0f3460 0%, #16213e 100%)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");
        return hero;
    }

    private Div buildScoreBadge(String label, double score, boolean isPrimary) {
        String scoreText = score > 0
                ? buildStarString(score / 2.0) + "  " + score + " / 10"
                : "No reviews yet";

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
                .set("color", isPrimary ? "var(--lumo-primary-color)" : "var(--lumo-secondary-text-color)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.06em");

        Span valueSpan = new Span(scoreText);
        valueSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "700")
                .set("color", isPrimary ? "var(--lumo-primary-color)" : "var(--lumo-body-text-color)");

        VerticalLayout badge = new VerticalLayout(labelSpan, valueSpan);
        badge.setPadding(false);
        badge.setSpacing(false);
        badge.getStyle()
                .set("gap", "2px")
                .set("background", isPrimary ? "var(--lumo-primary-color-10pct)" : "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-s) var(--lumo-space-m)");
        return new Div(badge);
    }

    // -------------------------------------------------------------------------
    // Main content area
    // -------------------------------------------------------------------------

    private Div buildContentArea(Movie movie) {
        VerticalLayout content = new VerticalLayout(
                buildOverviewSection(movie),
                buildCastSection(movie),
                buildDetailsSection(movie),
                buildReviewsSection(movie)
        );
        content.setPadding(true);
        content.setSpacing(false);
        content.getStyle()
                .set("max-width", "1100px")
                .set("margin", "0 auto")
                .set("gap", "var(--lumo-space-xl)");
        content.setWidth("100%");

        Div wrapper = new Div(content);
        wrapper.setWidth("100%");
        return wrapper;
    }

    // -------------------------------------------------------------------------
    // Overview
    // -------------------------------------------------------------------------

    private VerticalLayout buildOverviewSection(Movie movie) {
        Paragraph overview = new Paragraph(movie.getOverview());
        overview.getStyle()
                .set("color", "var(--lumo-body-text-color)")
                .set("line-height", "1.7")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("margin", "0");
        return sectionCard(sectionHeading("Overview"), overview);
    }

    // -------------------------------------------------------------------------
    // Cast & crew
    // -------------------------------------------------------------------------

    private VerticalLayout buildCastSection(Movie movie) {
        FlexLayout castRow = new FlexLayout();
        castRow.getStyle().set("gap", "var(--lumo-space-m)").set("flex-wrap", "wrap");

        for (String actorName : movie.getCast()) {
            Avatar avatar = new Avatar(actorName);
            avatar.setColorIndex(Math.abs(actorName.hashCode()) % 7);
            avatar.getStyle().set("width", "48px").set("height", "48px");
            // TODO: replace with TMDB profile_path image

            Span name = new Span(actorName);
            name.getStyle()
                    .set("font-size", "var(--lumo-font-size-s)")
                    .set("color", "var(--lumo-body-text-color)")
                    .set("text-align", "center");

            VerticalLayout actorCard = new VerticalLayout(avatar, name);
            actorCard.setPadding(false);
            actorCard.setSpacing(false);
            actorCard.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
            actorCard.getStyle().set("gap", "var(--lumo-space-xs)").set("width", "80px");
            castRow.add(actorCard);
        }

        return sectionCard(sectionHeading("Cast & Crew"), castRow);
    }

    // -------------------------------------------------------------------------
    // Film details
    // -------------------------------------------------------------------------

    private VerticalLayout buildDetailsSection(Movie movie) {
        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(200px, 1fr))")
                .set("gap", "var(--lumo-space-m)");

        if (movie.getBudget() > 0)
            grid.add(detailItem("Budget", "$" + formatMoney(movie.getBudget())));
        if (movie.getRevenue() > 0)
            grid.add(detailItem("Box Office", "$" + formatMoney(movie.getRevenue())));
        if (!movie.getProductionCompanies().isEmpty())
            grid.add(detailItem("Production", String.join(", ", movie.getProductionCompanies())));
        grid.add(detailItem("Original Title", movie.getOriginalTitle()));
        grid.add(detailItem("Original Language", movie.getOriginalLanguage().toUpperCase()));
        grid.add(detailItem("Release Date", movie.getReleaseDate()));
        grid.add(detailItem("Runtime", movie.getRuntimeFormatted()));

        return sectionCard(sectionHeading("Film Details"), grid);
    }

    private Div detailItem(String label, String value) {
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.08em")
                .set("font-weight", "600");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-body-text-color)");

        Div item = new Div(labelSpan, valueSpan);
        item.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "4px");
        return item;
    }

    // -------------------------------------------------------------------------
    // Reviews section
    // -------------------------------------------------------------------------

    private VerticalLayout buildReviewsSection(Movie movie) {
        List<Review> allReviews = dataService.getReviewsForMovie(movie.getId());

        VerticalLayout section = new VerticalLayout(sectionHeading("Reviews"));
        section.setPadding(false);
        section.setSpacing(false);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("gap", "var(--lumo-space-l)");

        if (allReviews.isEmpty()) {
            Paragraph empty = new Paragraph("No reviews yet. Be the first to review this film!");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            section.add(empty);
        } else {
            // Top recommended
            Review topReview = allReviews.stream()
                    .max(Comparator.comparingInt(Review::getHelpfulCount))
                    .orElse(null);

            if (topReview != null) {
                H3 topHeading = new H3("⭐ Top Recommended");
                topHeading.getStyle()
                        .set("margin", "0 0 var(--lumo-space-s) 0")
                        .set("color", "var(--lumo-primary-color)")
                        .set("font-size", "var(--lumo-font-size-m)");
                section.add(topHeading, buildReviewCard(topReview, true));
            }

            // 3 most recent (excluding top)
            List<Review> recent = allReviews.stream()
                    .filter(r -> r != topReview)
                    .sorted(Comparator.comparing(Review::getDatePosted).reversed())
                    .limit(3)
                    .toList();

            if (!recent.isEmpty()) {
                H3 recentHeading = new H3("Most Recent");
                recentHeading.getStyle()
                        .set("margin", "var(--lumo-space-m) 0 var(--lumo-space-s) 0")
                        .set("font-size", "var(--lumo-font-size-m)");
                section.add(recentHeading);
                recent.forEach(r -> section.add(buildReviewCard(r, false)));
            }
        }

        Button writeReviewBtn = new Button("Write a Review");
        writeReviewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        writeReviewBtn.getStyle().set("margin-top", "var(--lumo-space-m)");
        writeReviewBtn.addClickListener(e ->
                Notification.show("Review submission coming soon!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_PRIMARY));
        // TODO: navigate to review submission form / dialog once review-service is wired
        section.add(writeReviewBtn);
        section.setWidth("100%");
        return section;
    }

    private Div buildReviewCard(Review review, boolean isTop) {
        Avatar avatar = new Avatar(review.getUsername());
        avatar.setColorIndex(Math.abs(review.getUsername().hashCode()) % 7);
        avatar.getStyle().set("width", "36px").set("height", "36px");

        Span username = new Span(review.getUsername());
        username.getStyle().set("font-weight", "600").set("font-size", "var(--lumo-font-size-s)");

        Span date = new Span(review.getDatePosted());
        date.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-xs)");

        Span ratingBadge = new Span(buildStarString(review.getStars()) + "  " + review.getRating() + "/10");
        ratingBadge.getStyle()
                .set("margin-left", "auto")
                .set("color", "var(--lumo-primary-color)")
                .set("font-weight", "700")
                .set("font-size", "var(--lumo-font-size-s)");

        VerticalLayout userInfo = new VerticalLayout(username, date);
        userInfo.setPadding(false);
        userInfo.setSpacing(false);
        userInfo.getStyle().set("gap", "2px");

        HorizontalLayout header = new HorizontalLayout(avatar, userInfo, ratingBadge);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");

        H4 reviewTitle = new H4(review.getTitle());
        reviewTitle.getStyle().set("margin", "var(--lumo-space-xs) 0").set("font-size", "var(--lumo-font-size-m)");

        Paragraph body = new Paragraph(review.getBody());
        body.getStyle().set("color", "var(--lumo-body-text-color)").set("line-height", "1.6").set("margin", "0");

        Span helpful = new Span("👍 " + review.getHelpfulCount() + " found this helpful");
        helpful.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "var(--lumo-space-xs)");

        Div card = new Div(header, reviewTitle, body, helpful);
        card.getStyle()
                .set("background", isTop ? "var(--lumo-primary-color-10pct)" : "var(--lumo-contrast-5pct)")
                .set("border", isTop ? "1px solid var(--lumo-primary-color-50pct)" : "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-xs)");
        card.setWidth("100%");
        return card;
    }

    // -------------------------------------------------------------------------
    // 404 fallback
    // -------------------------------------------------------------------------

    private void showNotFound() {
        VerticalLayout notFound = new VerticalLayout();
        notFound.setSizeFull();
        notFound.setAlignItems(FlexComponent.Alignment.CENTER);
        notFound.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H2 msg = new H2("Movie not found");
        msg.getStyle().set("color", "var(--lumo-secondary-text-color)");

        Button backBtn = new Button("← Back to Home");
        backBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));
        notFound.add(msg, backBtn);
        add(notFound);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private H2 sectionHeading(String text) {
        H2 h = new H2(text);
        h.getStyle().set("margin", "0 0 var(--lumo-space-m) 0").set("font-size", "var(--lumo-font-size-l)");
        return h;
    }

    private VerticalLayout sectionCard(com.vaadin.flow.component.Component... components) {
        VerticalLayout card = new VerticalLayout(components);
        card.setPadding(false);
        card.setSpacing(false);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        card.setWidth("100%");
        return card;
    }

    private String buildStarString(double rating) {
        int full = (int) Math.round(Math.min(rating, 5));
        return "★".repeat(full) + "☆".repeat(Math.max(0, 5 - full));
    }

    private String formatMoney(int amount) {
        if (amount >= 1_000_000_000) return String.format("%.2fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000)     return String.format("%.1fM", amount / 1_000_000.0);
        return String.format("%,d", amount);
    }
}