package ca.yorku.eecs4314group12.ui.views;

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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
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
import ca.yorku.eecs4314group12.ui.security.UserSessionService;

@Route(value = "movie/:movieId", layout = MainLayout.class)
@PageTitle("Movie | Absolute Cinema")
@AnonymousAllowed
public class MovieView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {

    private static final String TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/";

    private final BackendClientService backendClient;
    private final DummyDataService dummyDataService;
    private final UserSessionService userSessionService;

    public MovieView(BackendClientService backendClient, DummyDataService dummyDataService,
                     UserSessionService userSessionService) {
        this.backendClient = backendClient;
        this.dummyDataService = dummyDataService;
        this.userSessionService = userSessionService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }
   
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String param = event.getRouteParameters().get("movieId").orElse("0");
        int movieId;
        try { movieId = Integer.parseInt(param); }
        catch (NumberFormatException e) { showNotFound(); return; }

        Optional<MovieDTO> realMovie = backendClient.getMovieById(movieId);
        if (realMovie.isPresent()) {
            buildPageFromDTO(realMovie.get());
        } else {
            dummyDataService.getMovieById(movieId).ifPresentOrElse(
                    this::buildPageFromDummy, this::showNotFound);
        }
    }
    
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
    	getUI().ifPresent(ui ->
        	ui.getPage().executeJs(
        		"const div = Array.from(document.querySelector('vaadin-app-layout').shadowRoot.querySelectorAll('div'))" +
        		           "  .find(d => d.scrollHeight > d.clientHeight);" +
        		           "if (div) div.scrollTop = 0;"
        	)	
    	);
    }

    private void buildPageFromDTO(MovieDTO movie) {
        getUI().ifPresent(ui -> {
            ui.getPage().setTitle(movie.getTitle() + " | Absolute Cinema");
        });
        List<ReviewDTO> reviews = backendClient.getReviewsForMovie(movie.getId());
        double userScore = backendClient.getReviewStats(movie.getId())
                .map(ReviewStatsDTO::getAverageRating).orElse(0.0);
        add(buildHeroBannerDTO(movie, userScore));
        if (movie.hasGallery()) add(buildGallerySection(movie));
        add(buildContentAreaDTO(movie, reviews));
    }

    private void buildPageFromDummy(ca.yorku.eecs4314group12.ui.data.Movie movie) {
        getUI().ifPresent(ui -> {
            ui.getPage().setTitle(movie.getTitle() + " | Absolute Cinema");
            ui.getPage().executeJs("window.scrollTo(0, 0)");
        });
        List<ReviewDTO> reviews = backendClient.getReviewsForMovie(movie.getId());
        double userScore = backendClient.getReviewStats(movie.getId())
                .map(ReviewStatsDTO::getAverageRating).orElse(movie.getUserScore());
        add(buildHeroBannerDummy(movie, userScore), buildContentAreaDummy(movie, reviews));
    }

    // =========================================================================
    // Gallery (Steam-style) — trailers first, then backdrop images
    // =========================================================================

    /**
     * Steam-style gallery: a large featured viewport above a horizontal
     * thumbnail strip. Trailers (YouTube embeds) come first, then backdrop
     * images. Clicking a thumbnail swaps the featured view.
     */
    private Div buildGallerySection(MovieDTO movie) {
        List<String> videos = movie.getVideos() != null ? movie.getVideos() : List.of();
        List<String> images = movie.getImages() != null ? movie.getImages() : List.of();

        record GalleryItem(String type, String src, String thumb) {}
        java.util.List<GalleryItem> items = new java.util.ArrayList<>();
        for (String key : videos) {
            String embed = "https://www.youtube.com/embed/" + key + "?rel=0";
            String thumb = "https://img.youtube.com/vi/" + key + "/mqdefault.jpg";
            items.add(new GalleryItem("video", embed, thumb));
        }
        for (String path : images) {
            String url = "https://image.tmdb.org/t/p/w1280" + path;
            String thumbUrl = "https://image.tmdb.org/t/p/w300" + path;
            items.add(new GalleryItem("image", url, thumbUrl));
        }

        if (items.isEmpty()) return new Div();

        // ---- Featured viewport ----
        Div featured = new Div();
        featured.getStyle()
                .set("width", "100%")
                .set("aspect-ratio", "16/9")
                .set("background", "#000")
                .set("position", "relative")
                .set("overflow", "hidden");

        GalleryItem first = items.get(0);
        if ("video".equals(first.type())) {
            featured.add(makeIframe(first.src()));
        } else {
            featured.add(makeFeaturedImage(first.src()));
        }

        // ---- Thumbnail strip ----
        Div strip = new Div();
        strip.getStyle()
                .set("display", "flex")
                .set("flex-direction", "row")
                .set("gap", "6px")
                .set("overflow-x", "auto")
                .set("padding", "8px 0 6px 0")
                .set("background", "#0a0a0a")
                .set("scrollbar-width", "thin")
                .set("align-items", "center");

        for (int i = 0; i < items.size(); i++) {
            GalleryItem item = items.get(i);
            Div thumb = new Div();
            thumb.getStyle()
                    .set("flex-shrink", "0")
                    .set("width", "80px")
                    .set("aspect-ratio", "16/9")
                    .set("cursor", "pointer")
                    .set("overflow", "hidden")
                    .set("border-radius", "4px")
                    .set("border", i == 0
                            ? "2px solid var(--lumo-primary-color)"
                            : "2px solid rgba(255,255,255,0.15)")
                    .set("position", "relative")
                    .set("background", "#111");

            Image thumbImg = new Image(item.thumb(), "");
            thumbImg.getStyle()
                    .set("width", "100%").set("height", "100%")
                    .set("object-fit", "cover").set("display", "block");
            thumb.add(thumbImg);

            if ("video".equals(item.type())) {
                Div playIcon = new Div();
                playIcon.setText("▶");
                playIcon.getStyle()
                        .set("position", "absolute")
                        .set("top", "50%").set("left", "50%")
                        .set("transform", "translate(-50%, -50%)")
                        .set("color", "white").set("font-size", "18px")
                        .set("text-shadow", "0 1px 4px rgba(0,0,0,0.8)")
                        .set("pointer-events", "none");
                thumb.add(playIcon);
            }

            final String itemSrc = item.src();
            final String itemType = item.type();

            thumb.addClickListener(e -> {
                featured.removeAll();
                if ("video".equals(itemType)) {
                    featured.add(makeIframe(itemSrc));
                } else {
                    featured.add(makeFeaturedImage(itemSrc));
                }
                strip.getChildren().forEach(child -> {
                    if (child instanceof Div t)
                        t.getStyle().set("border", "2px solid rgba(255,255,255,0.15)");
                });
                thumb.getStyle().set("border", "2px solid var(--lumo-primary-color)");
            });

            strip.add(thumb);
        }

        // ---- Full wrapper ----
        H3 heading = new H3("Gallery");
        heading.getStyle()
                .set("margin", "0 0 12px 0")
                .set("color", "var(--lumo-body-text-color)")
                .set("font-size", "1.25rem")
                .set("font-weight", "600")
                .set("letter-spacing", "0.02em");
        
        Div inner = new Div(heading, featured, strip);
        inner.getStyle()
                .set("max-width", "720px")
                .set("margin", "0 auto")
                .set("padding", "var(--lumo-space-m) var(--lumo-space-l) var(--lumo-space-m) var(--lumo-space-l)");

        Div wrapper = new Div(inner);
        wrapper.setWidth("100%");
        wrapper.getStyle()
                .set("background", "#0a0a0a")
                .set("border-bottom", "none")
                .set("margin-bottom", "0")
                .set("padding-bottom", "0");
        return wrapper;
    }

    private com.vaadin.flow.component.Component makeIframe(String src) {
        com.vaadin.flow.component.html.IFrame iframe =
                new com.vaadin.flow.component.html.IFrame(src);
        iframe.setWidth("100%");
        iframe.setHeight("100%");
        iframe.getStyle().set("border", "none")
                .set("position", "absolute")
                .set("top", "0").set("left", "0");
        iframe.getElement().setAttribute("allowfullscreen", true);
        iframe.getElement().setAttribute("allow",
                "accelerometer; autoplay; clipboard-write; encrypted-media; " +
                "gyroscope; picture-in-picture; web-share");
        return iframe;
    }

    private Image makeFeaturedImage(String src) {
        Image img = new Image(src, "");
        img.getStyle()
                .set("width", "100%").set("height", "100%")
                .set("object-fit", "contain")
                .set("position", "absolute")
                .set("top", "0").set("left", "0")
                .set("cursor", "zoom-in");

        img.addClickListener(e -> {
            // Fullscreen overlay
            Div overlay = new Div();
            overlay.getStyle()
                    .set("position", "fixed")
                    .set("top", "0").set("left", "0")
                    .set("width", "100vw").set("height", "100vh")
                    .set("background", "rgba(0,0,0,0.92)")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center")
                    .set("z-index", "9999")
                    .set("cursor", "zoom-out");

            Image full = new Image(src.replace("w1280", "original"), "");
            full.getStyle()
                    .set("max-width", "95vw")
                    .set("max-height", "95vh")
                    .set("object-fit", "contain")
                    .set("border-radius", "4px")
                    .set("box-shadow", "0 8px 40px rgba(0,0,0,0.8)");

            overlay.add(full);
            overlay.addClickListener(ev -> overlay.removeFromParent());
            img.getUI().ifPresent(ui ->
                    ui.getElement().appendChild(overlay.getElement()));
        });

        return img;
    }

    // =========================================================================
    // Hero banners
    // =========================================================================

    private Div buildHeroBannerDTO(MovieDTO movie, double userScore) {
        com.vaadin.flow.component.Component poster =
                buildPosterComponent(movie.getPoster_path(), "🎬", movie.getTitle());

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

        Div metaLine = new Div();
        metaLine.getStyle().set("display", "flex").set("align-items", "center")
                .set("gap", "var(--lumo-space-s)").set("flex-wrap", "wrap");
        Span metaSpan = new Span(movie.getYear() + "  ·  " + movie.getRuntimeFormatted()
                + "  ·  " + (movie.getStatus() != null ? movie.getStatus() : ""));
        metaSpan.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");
        metaLine.add(metaSpan);
     
        Span rating = new Span(movie.getAge_rating());
        rating.getStyle()
        	.set("border", "1px solid var(--lumo-contrast-30pct)")
        	.set("border-radius", "var(--lumo-border-radius-s)")
        	.set("padding", "1px 6px")
        	.set("font-size", "var(--lumo-font-size-xs)")
        	.set("color", "var(--lumo-secondary-text-color)");
        metaLine.add(rating);
        

        HorizontalLayout scores = new HorizontalLayout(buildScoreBadge("👥 Users", userScore, true));
        scores.setSpacing(true);

        Span director = new Span("Directed by  " + movie.getDirector());
        director.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        HorizontalLayout actionRow = new HorizontalLayout();
        actionRow.setSpacing(true);
        if (isLoggedIn() && userSessionService.getUserId() != null) {
            long userId = userSessionService.getUserId();
            boolean inWatchlist = backendClient.isInWatchlist(userId, movie.getId());
            boolean inFavourites = backendClient.isInFavourites(userId, movie.getId());
            actionRow.add(
                    buildWatchlistButton(userId, movie.getId(), inWatchlist),
                    buildFavouriteButton(userId, movie.getId(), inFavourites),
                    buildWatchedButton(userId, movie.getId())
            );
        }

        List<String> genres = movie.getGenres() != null ? movie.getGenres() : List.of();
        VerticalLayout info = new VerticalLayout(
                title, taglineDiv, buildGenreChips(genres), metaLine, scores, director, actionRow);
        info.setPadding(false); info.setSpacing(false);
        info.getStyle().set("gap", "var(--lumo-space-s)");

        return wrapHero(poster, info, movie.getBackdrop_path());
    }

    private Div buildHeroBannerDummy(ca.yorku.eecs4314group12.ui.data.Movie movie, double userScore) {
        com.vaadin.flow.component.Component poster =
                buildPosterComponent(null, movie.getPosterEmoji(), movie.getTitle());

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

        return wrapHero(poster, info, null);
    }

    // =========================================================================
    // Action buttons
    // =========================================================================

    private Button buildWatchlistButton(long userId, int movieId, boolean inWatchlist) {
        Button btn = inWatchlist
                ? new Button("In Watchlist", VaadinIcon.BOOKMARK.create())
                : new Button("Watchlist", VaadinIcon.BOOKMARK_O.create());
        btn.addThemeVariants(inWatchlist ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_PRIMARY);
        btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btn.addClickListener(e -> {
            if (inWatchlist) {
            	backendClient.removeFromWatchlist(userId, movieId);
            	btn.setText("Watchlist");
            	btn.setIcon(VaadinIcon.BOOKMARK_O.create());
            }
            else { 
            	backendClient.addToWatchlist(userId, movieId);
            	btn.setText("In Watchlist");
            	btn.setIcon(VaadinIcon.BOOKMARK.create());
            }
        });
        return btn;
    }

    private Button buildFavouriteButton(long userId, int movieId, boolean inFavourites) {
        Button btn = inFavourites
                ? new Button("Favourited", VaadinIcon.HEART.create())
                : new Button("Favourite", VaadinIcon.HEART_O.create());
        btn.addThemeVariants(inFavourites ? ButtonVariant.LUMO_ERROR : ButtonVariant.LUMO_TERTIARY);
        btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btn.addClickListener(e -> {
            if (inFavourites) {
            	backendClient.removeFromFavourites(userId, movieId);
            	btn.setText("Favourite");
            	btn.setIcon(VaadinIcon.HEART_O.create());
            }
            else {
            	backendClient.addToFavourites(userId, movieId);
            	btn.setText("Favourited");
            	btn.setIcon(VaadinIcon.HEART.create());
            	
            }
        });
        return btn;
    }

    private Button buildWatchedButton(long userId, int movieId) {
        Button btn = new Button("Mark as Watched", VaadinIcon.CHECK.create());
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        btn.addClickListener(e -> {
            backendClient.addToWatchHistory(userId, movieId);
            Notification n = Notification.show("Marked as watched!", 2000,
                    Notification.Position.BOTTOM_START);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        return btn;
    }

    // =========================================================================
    // Content areas
    // =========================================================================

    private Div buildContentAreaDTO(MovieDTO movie, List<ReviewDTO> reviews) {
        Paragraph overview = new Paragraph(movie.getOverview());
        overview.getStyle().set("line-height", "1.7").set("margin", "0");

        // All cast and crew — no limit, scroll shows 12 at a time
        List<MovieDTO.CastMemberDTO> cast = movie.getCast() != null ? movie.getCast() : List.of();
        List<MovieDTO.CrewMemberDTO> crew = movie.getCrew() != null ? movie.getCrew() : List.of();

        VerticalLayout castCrewSection = new VerticalLayout();
        castCrewSection.setPadding(false); castCrewSection.setSpacing(false);
        castCrewSection.getStyle().set("gap", "var(--lumo-space-m)");

        if (!cast.isEmpty()) {
            H3 castHeading = new H3("Cast");
            castHeading.getStyle().set("margin", "0 0 var(--lumo-space-s) 0")
                    .set("font-size", "var(--lumo-font-size-m)");
            Div castScroll = buildScrollRow();
            cast.forEach(c -> castScroll.add(
                    buildPersonCard(c.getName(), c.getCharacter(), c.getProfile_path())));
            castCrewSection.add(castHeading, castScroll);
        }

        if (!crew.isEmpty()) {
            H3 crewHeading = new H3("Crew");
            crewHeading.getStyle().set("margin", "var(--lumo-space-m) 0 var(--lumo-space-s) 0")
                    .set("font-size", "var(--lumo-font-size-m)");
            Div crewScroll = buildScrollRow();

            // Duplicate crew by name, combining all jobs into one card
            java.util.LinkedHashMap<String, java.util.List<String>> crewByName = new java.util.LinkedHashMap<>();
            java.util.HashMap<String, String> profilePaths = new java.util.HashMap<>();
            crew.forEach(c -> {
                crewByName.computeIfAbsent(c.getName(), k -> new java.util.ArrayList<>()).add(c.getJob());
                profilePaths.putIfAbsent(c.getName(), c.getProfile_path());
            });

            crewByName.forEach((crewName, jobs) -> {
                String combinedRoles = String.join(" / ", jobs);
                crewScroll.add(buildPersonCard(crewName, combinedRoles, profilePaths.get(crewName)));
            });

            castCrewSection.add(crewHeading, crewScroll);
        }

        Div detailsGrid = buildDetailsGrid(
                movie.getBudget(), movie.getRevenue(), movie.getCompanyNames(),
                movie.getOriginal_title(),
                movie.getOriginal_language(),
                movie.getRelease_date(), movie.getRuntimeFormatted());

        return wrapContent(
                sectionCard(sectionHeading("Overview"), overview),
                sectionCard(sectionHeading("Cast & Crew"), castCrewSection),
                sectionCard(sectionHeading("Film Details"), detailsGrid),
                buildReviewsSection(reviews, movie.getId(), movie.getTitle()));
    }

    private Div buildContentAreaDummy(ca.yorku.eecs4314group12.ui.data.Movie movie,
                                      List<ReviewDTO> reviews) {
        Paragraph overview = new Paragraph(movie.getOverview());
        overview.getStyle().set("line-height", "1.7").set("margin", "0");

        VerticalLayout castCrewSection = new VerticalLayout();
        castCrewSection.setPadding(false); castCrewSection.setSpacing(false);
        castCrewSection.getStyle().set("gap", "var(--lumo-space-m)");

        if (!movie.getCast().isEmpty()) {
            H3 castHeading = new H3("Cast");
            castHeading.getStyle().set("margin", "0 0 var(--lumo-space-s) 0")
                    .set("font-size", "var(--lumo-font-size-m)");
            Div castScroll = buildScrollRow();
            movie.getCast().forEach(n -> castScroll.add(buildPersonCard(n, null, null)));
            castCrewSection.add(castHeading, castScroll);
        }

        Div detailsGrid = buildDetailsGrid(
                movie.getBudget(), movie.getRevenue(), movie.getProductionCompanies(),
                movie.getOriginalTitle(), movie.getOriginalLanguage().toUpperCase(),
                movie.getReleaseDate(), movie.getRuntimeFormatted());

        return wrapContent(
                sectionCard(sectionHeading("Overview"), overview),
                sectionCard(sectionHeading("Cast & Crew"), castCrewSection),
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
        if (companies != null && !companies.isEmpty())
            grid.add(detailItem("Production", String.join(", ", companies)));
        if (originalTitle != null && !originalTitle.isBlank())
            grid.add(detailItem("Original Title", originalTitle));
        grid.add(detailItem("Language", language));
        if (releaseDate != null && !releaseDate.isBlank())
            grid.add(detailItem("Release Date", releaseDate));
        grid.add(detailItem("Runtime", runtime));
        return grid;
    }

    // =========================================================================
    // Reviews section
    // =========================================================================

    private VerticalLayout buildReviewsSection(List<ReviewDTO> reviews, int movieId, String movieTitle) {
        boolean loggedIn = isLoggedIn();

        Button writeBtn = new Button("Write a Review");
        writeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        if (loggedIn) {
            writeBtn.addClickListener(e -> {
                ReviewDialog dialog = new ReviewDialog(backendClient, userSessionService, movieId, movieTitle);
                dialog.setOnSuccess(() -> {
                    removeAll();
                    backendClient.getMovieById(movieId).ifPresent(this::buildPageFromDTO);
                });
                dialog.open();
            });
        } else {
            writeBtn.addClickListener(e -> Notification.show("Please log in to write a review.",
                    3000, Notification.Position.MIDDLE));
        }

        HorizontalLayout header = new HorizontalLayout(sectionHeading("Reviews"), writeBtn);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidthFull();

        VerticalLayout reviewCards = new VerticalLayout();
        reviewCards.setPadding(false); reviewCards.setSpacing(true);

        if (reviews.isEmpty()) {
            reviewCards.add(new Span("No reviews yet. Be the first!"));
        } else {
            reviews.stream()
                    .max(java.util.Comparator.comparingInt(
                            r -> r.getHelpfulCount() != null ? r.getHelpfulCount() : 0))
                    .ifPresent(top -> reviewCards.add(buildReviewCard(top, true, loggedIn, movieId)));
            reviews.stream()
                    .sorted(java.util.Comparator.comparing(ReviewDTO::getCreatedAt,
                            java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                    .limit(3)
                    .forEach(r -> reviewCards.add(buildReviewCard(r, false, loggedIn, movieId)));
        }

        VerticalLayout section = new VerticalLayout(header, reviewCards);
        section.setPadding(true); section.setSpacing(false);
        section.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
        section.setWidth("100%");
        return section;
    }

    private Div buildReviewCard(ReviewDTO review, boolean isTop, boolean loggedIn, int movieId) {
        Div card = new Div();
        card.getStyle()
                .set("background", isTop ? "var(--lumo-primary-color-10pct)" : "var(--lumo-contrast-5pct)")
                .set("border", isTop ? "1px solid var(--lumo-primary-color-50pct)" : "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("display", "flex").set("flex-direction", "column")
                .set("gap", "var(--lumo-space-xs)");
        card.setWidth("100%");

        H4 reviewTitle = new H4((isTop ? "⭐ " : "") + review.getTitle());
        reviewTitle.getStyle().set("margin", "0");

        Span rating = new Span("★ " + review.getRating() + "/10");
        rating.getStyle().set("color", "var(--lumo-primary-color)").set("font-weight", "bold");

        Paragraph content = new Paragraph(review.getContent());
        content.getStyle().set("margin", "0").set("line-height", "1.6");

        Span meta = new Span("User " + review.getUserId()
                + (review.getCreatedAt() != null
                        ? "  ·  " + review.getCreatedAt().toString().substring(0, 10) : ""));
        meta.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-tertiary-text-color)");

        HorizontalLayout voteRow = buildVoteRow(review, loggedIn);

        card.add(reviewTitle, rating, content, meta, voteRow);

        Long sessionUserId = userSessionService.getUserId();
        boolean isOwner = sessionUserId != null && review.getUserId() != null
                && sessionUserId.equals(review.getUserId());
        boolean isAdmin = "ADMIN".equals(userSessionService.getRole());
        if (loggedIn && review.getId() != null && sessionUserId != null && (isOwner || isAdmin)) {
            Button del = new Button("Delete review", VaadinIcon.TRASH.create());
            del.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            del.addClickListener(e -> {
                if (backendClient.deleteReview(review.getId(), sessionUserId, userSessionService.getRole())) {
                    Notification.show("Review deleted.", 2500, Notification.Position.BOTTOM_START)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    removeAll();
                    backendClient.getMovieById(movieId).ifPresentOrElse(this::buildPageFromDTO,
                            () -> dummyDataService.getMovieById(movieId).ifPresentOrElse(
                                    this::buildPageFromDummy, this::showNotFound));
                } else {
                    Notification.show("Could not delete this review.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            card.add(del);
        }

        return card;
    }

    /**
     * Builds the helpful vote row for a review card.
     * Wired to POST /api/reviews/{id}/helpful in review-service.
     */
    private HorizontalLayout buildVoteRow(ReviewDTO review, boolean loggedIn) {
        int helpfulCount = review.getHelpfulCount() != null ? review.getHelpfulCount() : 0;

        Span likeCount = new Span(String.valueOf(helpfulCount));
        likeCount.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)");

        Button likeBtn = new Button("Helpful", VaadinIcon.THUMBS_UP.create());
        likeBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        likeBtn.getStyle().set("color", "var(--lumo-success-color)");

        if (loggedIn && review.getId() != null) {
            likeBtn.addClickListener(e -> {
                backendClient.markReviewHelpful(review.getId()).ifPresent(updated -> {
                    int newCount = updated.getHelpfulCount() != null ? updated.getHelpfulCount() : 0;
                    likeCount.setText(String.valueOf(newCount));
                    likeBtn.setEnabled(false);
                    Notification n = Notification.show("Marked as helpful!", 2000,
                            Notification.Position.BOTTOM_START);
                    n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                });
            });
        } else {
            likeBtn.setEnabled(false);
            likeBtn.setTooltipText("Log in to mark reviews as helpful");
        }

        HorizontalLayout row = new HorizontalLayout(likeBtn, likeCount);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setSpacing(false);
        row.getStyle().set("gap", "4px").set("margin-top", "var(--lumo-space-xs)");
        return row;
    }

    // =========================================================================
    // Shared UI helpers
    // =========================================================================

    private com.vaadin.flow.component.Component buildPosterComponent(
            String posterPath, String fallbackEmoji, String altText) {
        if (posterPath != null && !posterPath.isBlank()) {
            String url = posterPath.startsWith("http") ? posterPath : TMDB_IMAGE_BASE + "w342" + posterPath;
            Image img = new Image(url, altText);
            img.getStyle().set("width", "160px").set("min-width", "160px")
                    .set("height", "240px").set("object-fit", "cover")
                    .set("border-radius", "var(--lumo-border-radius-l)")
                    .set("box-shadow", "var(--lumo-box-shadow-m)");
            return img;
        }
        Div poster = new Div();
        poster.setText(fallbackEmoji != null ? fallbackEmoji : "🎬");
        poster.getStyle().set("font-size", "6rem").set("width", "160px").set("min-width", "160px")
                .set("height", "240px").set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                .set("box-shadow", "var(--lumo-box-shadow-m)");
        return poster;
    }

    private FlexLayout buildGenreChips(List<String> genres) {
        FlexLayout chips = new FlexLayout();
        chips.getStyle().set("gap", "var(--lumo-space-xs)").set("flex-wrap", "wrap");
        if (genres == null) return chips;
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

    /**
     * Horizontal scrollable row. Cards are 96px wide with 12px gaps.
     * ~12 visible at once on a 1100px container: (96+12)*12 = 1296px of content visible.
     */
    private Div buildScrollRow() {
        Div row = new Div();
        row.getStyle()
                .set("display", "flex")
                .set("flex-direction", "row")
                .set("gap", "12px")
                .set("overflow-x", "auto")
                .set("overflow-y", "hidden")
                .set("padding-bottom", "var(--lumo-space-s)")
                .set("scrollbar-width", "thin")
                .set("width", "100%")
                .set("align-items", "flex-start");
        return row;
    }

    /**
     * Card showing photo/avatar → role/character → name (top to bottom).
     * Text wraps to 2 lines max and truncates with ellipsis beyond that.
     */
    private VerticalLayout buildPersonCard(String name, String role, String profilePath) {
        com.vaadin.flow.component.Component avatarComponent;
        if (profilePath != null && !profilePath.isBlank()) {
            String url = profilePath.startsWith("http") ? profilePath : TMDB_IMAGE_BASE + "w92" + profilePath;
            Image img = new Image(url, name);
            img.getStyle()
                    .set("width", "72px").set("height", "72px")
                    .set("border-radius", "50%").set("object-fit", "cover")
                    .set("flex-shrink", "0");
            avatarComponent = img;
        } else {
            Avatar avatar = new Avatar(name);
            avatar.setColorIndex(Math.abs(name.hashCode()) % 7);
            avatar.getStyle().set("width", "72px").set("height", "72px").set("flex-shrink", "0");
            avatarComponent = avatar;
        }

        VerticalLayout card = new VerticalLayout(avatarComponent);
        card.setPadding(false); card.setSpacing(false);
        card.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        card.getStyle()
                .set("gap", "4px")
                .set("width", "96px")
                .set("min-width", "96px")
                .set("flex-shrink", "0")
                .set("text-align", "center");

        if (role != null && !role.isBlank()) {
            Span roleSpan = new Span(role);
            roleSpan.getElement().setAttribute("title", role);
            roleSpan.getStyle()
                    .set("font-size", "var(--lumo-font-size-xs)")
                    .set("color", "var(--lumo-primary-color)")
                    .set("font-style", "italic")
                    .set("display", "block")
                    .set("width", "96px")
                    .set("overflow-wrap", "break-word")
                    .set("word-break", "break-word")
                    .set("overflow", "hidden")
                    .set("text-overflow", "ellipsis")
                    .set("white-space", "normal");
            card.add(roleSpan);
        }

        Span nameSpan = new Span(name != null ? name : "");
        nameSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
                .set("display", "block")
                .set("width", "96px")
                .set("overflow-wrap", "break-word")
                .set("word-break", "break-word")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "normal");
        card.add(nameSpan);

        return card;
    }

    // Legacy overload used by dummy data path
    private VerticalLayout buildActorCard(String name, String profilePath) {
        return buildPersonCard(name, null, profilePath);
    }

    private Div detailItem(String label, String value) {
        Span l = new Span(label);
        l.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("text-transform", "uppercase").set("letter-spacing", "0.08em").set("font-weight", "600");
        Span v = new Span(value);
        v.getStyle().set("font-size", "var(--lumo-font-size-s)");
        Div item = new Div(l, v);
        item.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "4px");
        return item;
    }

    private Div wrapHero(com.vaadin.flow.component.Component poster, VerticalLayout info,
                         String backdropPath) {
        HorizontalLayout heroContent = new HorizontalLayout(poster, info);
        heroContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        heroContent.setSpacing(true);
        heroContent.getStyle()
                .set("max-width", "1100px").set("margin", "0 auto")
                .set("padding", "var(--lumo-space-xl) var(--lumo-space-l)")
                .set("position", "relative").set("z-index", "1");
        heroContent.setWidth("100%");

        Div hero = new Div();
        hero.setWidth("100%");
        hero.getStyle().set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("position", "relative");

        if (backdropPath != null && !backdropPath.isBlank()) {
            String backdropUrl = backdropPath.startsWith("http")
                    ? backdropPath
                    : "https://image.tmdb.org/t/p/w780" + backdropPath;
            hero.getStyle()
            		.set("background-image", "url('" + backdropUrl + "')")                  
                    .set("background-size", "auto auto")
                    .set("background-repeat", "no-repeat")
                    .set("background-position", "center center");

            Div dimOverlay = new Div();
            dimOverlay.getStyle()
            		.set("position", "absolute")
            		.set("top", "-1px").set("left", "-1px")
            		.set("width", "calc(100% + 2px)").set("height", "calc(100% + 2px)")
            		.set("background",
            				"radial-gradient(circle at center, rgba(0,0,0,0.35) 0%, rgba(0,0,0,0.75) 65%, rgba(0,0,0,0.95) 100%), " +
            				"linear-gradient(180deg, rgba(10,10,15,0.85) 0%, rgba(0,0,0,0.6) 40%, rgba(0,0,0,0.9) 100%)")
            		.set("pointer-events", "none")
            		.set("z-index", "0");
            hero.add(dimOverlay);
        } else {
            hero.getStyle().set("background",
                    "linear-gradient(180deg, #0a0a0f 0%, #000000 100%)");
        }

        hero.add(heroContent);
        return hero;
    }

    private Div wrapContent(com.vaadin.flow.component.Component... sections) {
        VerticalLayout content = new VerticalLayout(sections);
        content.setPadding(false); content.setSpacing(false);
        content.getStyle()
                .set("max-width", "1100px").set("margin", "0 auto")
                .set("gap", "var(--lumo-space-xl)")
                .set("padding", "0 var(--lumo-space-l) var(--lumo-space-xl) var(--lumo-space-l)");
        content.setWidth("100%");
        Div wrapper = new Div(content);
        wrapper.setWidth("100%");
        wrapper.getStyle().set("margin-top", "0").set("padding-top", "0");
        return wrapper;
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

    private H2 sectionHeading(String text) {
        H2 h = new H2(text);
        h.getStyle().set("margin", "0 0 var(--lumo-space-m) 0").set("font-size", "var(--lumo-font-size-l)");
        return h;
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

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");
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