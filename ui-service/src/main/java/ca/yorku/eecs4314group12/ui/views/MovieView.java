package ca.yorku.eecs4314group12.ui.views;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
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

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.DummyDataService;
import ca.yorku.eecs4314group12.ui.data.dto.MovieDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewStatsDTO;

/**
 * Dynamic movie detail page — route /movie/{id}
 *
 * Data sources (in order of preference):
 *   Movie   → BackendClientService → api-service GET /api/movie/{id}
 *             Falls back to DummyDataService if api-service/movie-service is down
 *   Reviews → BackendClientService → review-service GET /api/reviews/movie/{id}
 *             Falls back to empty list if review-service is down
 *   User score → review-service GET /api/reviews/movie/{id}/stats
 */
@Route(value = "movie/:movieId", layout = MainLayout.class)
@PageTitle("Movie | Absolute Cinema")
@AnonymousAllowed
public class MovieView extends VerticalLayout implements BeforeEnterObserver {

    private final BackendClientService backendClient;
    private final DummyDataService dummyDataService;

    public MovieView(BackendClientService backendClient, DummyDataService dummyDataService) {
        this.backendClient = backendClient;
        this.dummyDataService = dummyDataService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String param = event.getRouteParameters().get("movieId").orElse("0");
        int movieId;
        try {
            movieId = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            showNotFound();
            return;
        }

        // Try real backend first, fall back to dummy data
        Optional<MovieDTO> realMovie = backendClient.getMovieById(movieId);
        if (realMovie.isPresent()) {
            buildPageFromDTO(realMovie.get());
        } else {
            dummyDataService.getMovieById(movieId).ifPresentOrElse(
                    this::buildPageFromDummy,
                    this::showNotFound
            );
        }
    }

    // -------------------------------------------------------------------------
    // Build from real MovieDTO
    // -------------------------------------------------------------------------

    private void buildPageFromDTO(MovieDTO movie) {
        getUI().ifPresent(ui -> ui.getPage().setTitle(movie.getTitle() + " | Absolute Cinema"));
        List<ReviewDTO> reviews = backendClient.getReviewsForMovie(movie.getId());
        double userScore = backendClient.getReviewStats(movie.getId())
                .map(ReviewStatsDTO::getAverageRating).orElse(0.0);
        add(buildHeroBannerDTO(movie, userScore), buildContentAreaDTO(movie, reviews));
    }

    // -------------------------------------------------------------------------
    // Build from dummy Movie (fallback when backend is down)
    // -------------------------------------------------------------------------

    private void buildPageFromDummy(ca.yorku.eecs4314group12.ui.data.Movie movie) {
        getUI().ifPresent(ui -> ui.getPage().setTitle(movie.getTitle() + " | Absolute Cinema"));
        List<ReviewDTO> reviews = backendClient.getReviewsForMovie(movie.getId());
        double userScore = backendClient.getReviewStats(movie.getId())
                .map(ReviewStatsDTO::getAverageRating).orElse(movie.getUserScore());
        add(buildHeroBannerDummy(movie, userScore), buildContentAreaDummy(movie, reviews));
    }

    // =========================================================================
    // Hero banner
    // =========================================================================

    private Div buildHeroBannerDTO(MovieDTO movie, double userScore) {
        var poster = buildPosterComponent(movie.getPoster_path(), "🎬", movie.getTitle());

        H1 title = new H1(movie.getTitle());
        title.getStyle().set("margin", "0 0 var(--lumo-space-xs) 0")
                .set("font-size", "2rem").set("color", "var(--lumo-header-text-color)");

        Div taglineDiv = new Div();
        if (movie.getTagline() != null && !movie.getTagline().isBlank()) {
            Span tl = new Span("\"" + movie.getTagline() + "\"");
            tl.getStyle().set("font-style", "italic")
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-m)");
            taglineDiv.add(tl);
        }

        Span meta = new Span(movie.getYear() + "  ·  " + movie.getRuntimeFormatted()
                + "  ·  " + movie.getStatus());
        meta.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        HorizontalLayout scores = new HorizontalLayout(
                buildScoreBadge("🎬 TMDB", movie.getVote_average(), false),
                buildScoreBadge("👥 Users", userScore, true));
        scores.setSpacing(true);

        Span director = new Span("Directed by  " + movie.getDirector());
        director.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        VerticalLayout info = new VerticalLayout(
                title, taglineDiv, buildGenreChips(movie.getGenreNames()), meta, scores, director);
        info.setPadding(false); info.setSpacing(false);
        info.getStyle().set("gap", "var(--lumo-space-s)");

        return wrapHero(poster, info);
    }

    private Div buildHeroBannerDummy(ca.yorku.eecs4314group12.ui.data.Movie movie, double userScore) {
        var poster = buildPosterComponent(null, movie.getPosterEmoji(), movie.getTitle());

        H1 title = new H1(movie.getTitle());
        title.getStyle().set("margin", "0 0 var(--lumo-space-xs) 0")
                .set("font-size", "2rem").set("color", "var(--lumo-header-text-color)");

        Div taglineDiv = new Div();
        if (movie.getTagline() != null && !movie.getTagline().isBlank()) {
            Span tl = new Span("\"" + movie.getTagline() + "\"");
            tl.getStyle().set("font-style", "italic")
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-m)");
            taglineDiv.add(tl);
        }

        Span meta = new Span(movie.getYear() + "  ·  " + movie.getRuntimeFormatted()
                + "  ·  " + movie.getStatus());
        meta.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        HorizontalLayout scores = new HorizontalLayout(
                buildScoreBadge("🎬 TMDB", movie.getTmdbScore(), false),
                buildScoreBadge("👥 Users", userScore, true));
        scores.setSpacing(true);

        Span director = new Span("Directed by  " + movie.getDirector());
        director.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        VerticalLayout info = new VerticalLayout(
                title, taglineDiv, buildGenreChips(movie.getGenres()), meta, scores, director);
        info.setPadding(false); info.setSpacing(false);
        info.getStyle().set("gap", "var(--lumo-space-s)");

        return wrapHero(poster, info);
    }

    // =========================================================================
    // Content areas
    // =========================================================================

    private Div buildContentAreaDTO(MovieDTO movie, List<ReviewDTO> reviews) {
        Paragraph overview = new Paragraph(movie.getOverview());
        overview.getStyle().set("line-height", "1.7").set("margin", "0");

        FlexLayout castRow = new FlexLayout();
        castRow.getStyle().set("gap", "var(--lumo-space-m)").set("flex-wrap", "wrap");
        List<MovieDTO.CastMemberDTO> cast = movie.getCredits() != null && movie.getCredits().getCast() != null
                ? movie.getCredits().getCast().stream().limit(8).toList()
                : List.of();
        cast.forEach(c -> castRow.add(buildActorCard(c.getOriginal_name(), c.getProfile_path())));

        Div detailsGrid = buildDetailsGrid(
                movie.getBudget(), movie.getRevenue(),
                movie.getCompanyNames(), movie.getOriginal_title(),
                movie.getOriginal_language() != null ? movie.getOriginal_language().toUpperCase() : "N/A",
                movie.getRelease_date(), movie.getRuntimeFormatted());

        return wrapContent(
                sectionCard(sectionHeading("Overview"), overview),
                sectionCard(sectionHeading("Cast & Crew"), castRow),
                sectionCard(sectionHeading("Film Details"), detailsGrid),
                buildReviewsSection(reviews, movie.getId(), movie.getTitle()));
    }

    private Div buildContentAreaDummy(ca.yorku.eecs4314group12.ui.data.Movie movie,
                                      List<ReviewDTO> reviews) {
        Paragraph overview = new Paragraph(movie.getOverview());
        overview.getStyle().set("line-height", "1.7").set("margin", "0");

        FlexLayout castRow = new FlexLayout();
        castRow.getStyle().set("gap", "var(--lumo-space-m)").set("flex-wrap", "wrap");
        movie.getCast().forEach(n -> castRow.add(buildActorCard(n, null)));

        Div detailsGrid = buildDetailsGrid(
                movie.getBudget(), movie.getRevenue(),
                movie.getProductionCompanies(), movie.getOriginalTitle(),
                movie.getOriginalLanguage().toUpperCase(),
                movie.getReleaseDate(), movie.getRuntimeFormatted());

        return wrapContent(
                sectionCard(sectionHeading("Overview"), overview),
                sectionCard(sectionHeading("Cast & Crew"), castRow),
                sectionCard(sectionHeading("Film Details"), detailsGrid),
                buildReviewsSection(reviews, movie.getId(), movie.getTitle()));
    }

    private Div buildDetailsGrid(int budget, int revenue, List<String> companies,
                                  String originalTitle, String language,
                                  String releaseDate, String runtime) {
        Div grid = new Div();
        grid.getStyle().set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(200px, 1fr))")
                .set("gap", "var(--lumo-space-m)");
        if (budget > 0) grid.add(detailItem("Budget", "$" + formatMoney(budget)));
        if (revenue > 0) grid.add(detailItem("Box Office", "$" + formatMoney(revenue)));
        if (!companies.isEmpty()) grid.add(detailItem("Production", String.join(", ", companies)));
        grid.add(detailItem("Original Title", originalTitle));
        grid.add(detailItem("Language", language));
        grid.add(detailItem("Release Date", releaseDate));
        grid.add(detailItem("Runtime", runtime));
        return grid;
    }

    // =========================================================================
    // Reviews section
    // =========================================================================

    private VerticalLayout buildReviewsSection(List<ReviewDTO> reviews, int movieId, String movieTitle) {
        VerticalLayout section = new VerticalLayout(sectionHeading("Reviews"));
        section.setPadding(false); section.setSpacing(false);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("gap", "var(--lumo-space-l)");

        if (reviews.isEmpty()) {
            Paragraph empty = new Paragraph("No reviews yet. Be the first to review this film!");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            section.add(empty);
        } else {
            ReviewDTO top = reviews.stream()
                    .max(Comparator.comparingInt(ReviewDTO::getHelpfulCount))
                    .orElse(null);

            if (top != null) {
                H3 topLabel = new H3("⭐ Top Recommended");
                topLabel.getStyle().set("margin", "0 0 var(--lumo-space-s) 0")
                        .set("color", "var(--lumo-primary-color)")
                        .set("font-size", "var(--lumo-font-size-m)");
                section.add(topLabel, buildReviewCard(top, true));
            }

            List<ReviewDTO> recent = reviews.stream()
                    .filter(r -> r != top)
                    .sorted(Comparator.comparing(ReviewDTO::getDatePosted).reversed())
                    .limit(3)
                    .toList();

            if (!recent.isEmpty()) {
                H3 recentLabel = new H3("Most Recent");
                recentLabel.getStyle().set("margin", "var(--lumo-space-m) 0 var(--lumo-space-s) 0")
                        .set("font-size", "var(--lumo-font-size-m)");
                section.add(recentLabel);
                recent.forEach(r -> section.add(buildReviewCard(r, false)));
            }
        }

        Button writeBtn = new Button("Write a Review");
        writeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        writeBtn.getStyle().set("margin-top", "var(--lumo-space-m)");
        writeBtn.addClickListener(e -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean loggedIn = auth != null
                    && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal());

            if (!loggedIn) {
                Notification n = Notification.show(
                        "Please log in to write a review.",
                        3000, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                return;
            }

            ReviewDialog dialog = new ReviewDialog(backendClient, movieId, movieTitle);
            dialog.setOnSuccess(() ->
                    getUI().ifPresent(ui -> ui.navigate("movie/" + movieId)));
            dialog.open();
        });
        section.add(writeBtn);
        section.setWidth("100%");
        return section;
    }

    private Div buildReviewCard(ReviewDTO review, boolean isTop) {
        Avatar avatar = new Avatar(review.getUsername());
        avatar.setColorIndex(Math.abs(review.getUsername().hashCode()) % 7);
        avatar.getStyle().set("width", "36px").set("height", "36px");

        Span username = new Span(review.getUsername());
        username.getStyle().set("font-weight", "600").set("font-size", "var(--lumo-font-size-s)");

        Span date = new Span(review.getDatePosted());
        date.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-xs)");

        Span ratingBadge = new Span(buildStarString(review.getStars()) + "  " + review.getRating() + "/10");
        ratingBadge.getStyle().set("margin-left", "auto").set("color", "var(--lumo-primary-color)")
                .set("font-weight", "700").set("font-size", "var(--lumo-font-size-s)");

        VerticalLayout userInfo = new VerticalLayout(username, date);
        userInfo.setPadding(false); userInfo.setSpacing(false);
        userInfo.getStyle().set("gap", "2px");

        HorizontalLayout header = new HorizontalLayout(avatar, userInfo, ratingBadge);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");

        H4 reviewTitle = new H4(review.getTitle());
        reviewTitle.getStyle().set("margin", "var(--lumo-space-xs) 0")
                .set("font-size", "var(--lumo-font-size-m)");

        Paragraph body = new Paragraph(review.getContent());
        body.getStyle().set("line-height", "1.6").set("margin", "0");

        Span helpful = new Span("👍 " + review.getHelpfulCount() + " found this helpful");
        helpful.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "var(--lumo-space-xs)");

        Div card = new Div(header, reviewTitle, body, helpful);
        card.getStyle()
                .set("background", isTop ? "var(--lumo-primary-color-10pct)" : "var(--lumo-contrast-5pct)")
                .set("border", isTop ? "1px solid var(--lumo-primary-color-50pct)" : "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("display", "flex").set("flex-direction", "column")
                .set("gap", "var(--lumo-space-xs)");
        card.setWidth("100%");
        return card;
    }

    // =========================================================================
    // Shared UI helpers
    // =========================================================================

    private static final String TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/";

    /**
     * Builds a poster element.
     * If posterPath is non-null (real TMDB data), renders an <img> at w342 size.
     * Falls back to an emoji placeholder div (dummy data).
     */
    private com.vaadin.flow.component.Component buildPosterComponent(String posterPath, String fallbackEmoji, String altText) {
        if (posterPath != null && !posterPath.isBlank()) {
            Image img = new Image(TMDB_IMAGE_BASE + "w342" + posterPath, altText);
            img.getStyle()
                    .set("width", "160px").set("min-width", "160px")
                    .set("height", "240px")
                    .set("object-fit", "cover")
                    .set("border-radius", "var(--lumo-border-radius-l)")
                    .set("box-shadow", "var(--lumo-box-shadow-m)");
            return img;
        }
        // Fallback emoji div
        Div poster = new Div();
        poster.setText(fallbackEmoji);
        poster.getStyle()
                .set("font-size", "6rem").set("width", "160px").set("min-width", "160px")
                .set("height", "240px").set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                .set("box-shadow", "var(--lumo-box-shadow-m)");
        return poster;
    }

    private FlexLayout buildGenreChips(List<String> genres) {
        FlexLayout chips = new FlexLayout();
        chips.getStyle().set("gap", "var(--lumo-space-xs)").set("flex-wrap", "wrap");
        for (String g : genres) {
            Span chip = new Span(g);
            chip.getStyle()
                    .set("background", "var(--lumo-primary-color-10pct)")
                    .set("color", "var(--lumo-primary-color)")
                    .set("border-radius", "999px").set("padding", "2px 10px")
                    .set("font-size", "var(--lumo-font-size-xs)").set("font-weight", "600");
            chips.add(chip);
        }
        return chips;
    }

    private Div buildScoreBadge(String label, double score, boolean isPrimary) {
        String scoreText = score > 0
                ? buildStarString(score / 2.0) + "  " + String.format("%.1f", score) + " / 10"
                : "No reviews yet";

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "var(--lumo-font-size-xs)").set("font-weight", "600")
                .set("color", isPrimary ? "var(--lumo-primary-color)" : "var(--lumo-secondary-text-color)")
                .set("text-transform", "uppercase").set("letter-spacing", "0.06em");

        Span valueSpan = new Span(scoreText);
        valueSpan.getStyle().set("font-size", "var(--lumo-font-size-m)").set("font-weight", "700")
                .set("color", isPrimary ? "var(--lumo-primary-color)" : "var(--lumo-body-text-color)");

        VerticalLayout badge = new VerticalLayout(labelSpan, valueSpan);
        badge.setPadding(false); badge.setSpacing(false);
        badge.getStyle().set("gap", "2px")
                .set("background", isPrimary ? "var(--lumo-primary-color-10pct)" : "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-s) var(--lumo-space-m)");
        return new Div(badge);
    }

    private VerticalLayout buildActorCard(String name, String profilePath) {
        com.vaadin.flow.component.Component avatarComponent;
        if (profilePath != null && !profilePath.isBlank()) {
            Image img = new Image(TMDB_IMAGE_BASE + "w92" + profilePath, name);
            img.getStyle()
                    .set("width", "48px").set("height", "48px")
                    .set("border-radius", "50%")
                    .set("object-fit", "cover");
            avatarComponent = img;
        } else {
            Avatar avatar = new Avatar(name);
            avatar.setColorIndex(Math.abs(name.hashCode()) % 7);
            avatar.getStyle().set("width", "48px").set("height", "48px");
            avatarComponent = avatar;
        }
        Span nameSpan = new Span(name);
        nameSpan.getStyle().set("font-size", "var(--lumo-font-size-s)").set("text-align", "center");
        VerticalLayout card = new VerticalLayout(avatarComponent, nameSpan);
        card.setPadding(false); card.setSpacing(false);
        card.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        card.getStyle().set("gap", "var(--lumo-space-xs)").set("width", "80px");
        return card;
    }

    private Div detailItem(String label, String value) {
        Span l = new Span(label);
        l.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("text-transform", "uppercase").set("letter-spacing", "0.08em")
                .set("font-weight", "600");
        Span v = new Span(value);
        v.getStyle().set("font-size", "var(--lumo-font-size-s)");
        Div item = new Div(l, v);
        item.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "4px");
        return item;
    }

    private Div wrapHero(com.vaadin.flow.component.Component poster, VerticalLayout info) {
        HorizontalLayout heroContent = new HorizontalLayout(poster, info);
        heroContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        heroContent.setSpacing(true);
        heroContent.getStyle().set("max-width", "1100px").set("margin", "0 auto")
                .set("padding", "var(--lumo-space-xl) var(--lumo-space-l)");
        heroContent.setWidth("100%");
        Div hero = new Div(heroContent);
        hero.setWidth("100%");
        hero.getStyle().set("background", "linear-gradient(180deg, #0f3460 0%, #16213e 100%)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");
        return hero;
    }

    private Div wrapContent(com.vaadin.flow.component.Component... sections) {
        VerticalLayout content = new VerticalLayout(sections);
        content.setPadding(true); content.setSpacing(false);
        content.getStyle().set("max-width", "1100px").set("margin", "0 auto")
                .set("gap", "var(--lumo-space-xl)");
        content.setWidth("100%");
        Div wrapper = new Div(content);
        wrapper.setWidth("100%");
        return wrapper;
    }

    private H2 sectionHeading(String text) {
        H2 h = new H2(text);
        h.getStyle().set("margin", "0 0 var(--lumo-space-m) 0")
                .set("font-size", "var(--lumo-font-size-l)");
        return h;
    }

    private VerticalLayout sectionCard(com.vaadin.flow.component.Component... components) {
        VerticalLayout card = new VerticalLayout(components);
        card.setPadding(false); card.setSpacing(false);
        card.getStyle().set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)").set("box-shadow", "var(--lumo-box-shadow-xs)");
        card.setWidth("100%");
        return card;
    }

    private void showNotFound() {
        VerticalLayout nf = new VerticalLayout();
        nf.setSizeFull();
        nf.setAlignItems(FlexComponent.Alignment.CENTER);
        nf.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        H2 msg = new H2("Movie not found");
        msg.getStyle().set("color", "var(--lumo-secondary-text-color)");
        Button back = new Button("← Back to Home");
        back.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        back.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));
        nf.add(msg, back);
        add(nf);
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