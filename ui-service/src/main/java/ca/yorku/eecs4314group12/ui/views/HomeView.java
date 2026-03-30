package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.MovieListItemDTO;
import ca.yorku.eecs4314group12.ui.data.dto.FavouriteMovieDTO;
import ca.yorku.eecs4314group12.ui.data.dto.WatchlistDTO;
import ca.yorku.eecs4314group12.ui.security.UserSessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Home / discovery page.
 *
 * Shows:
 *   - Search bar wired to movie-service GET /movie/search/{query}
 *   - Recommended for You (logged-in users with watchlist items only)
 *   - Now Playing  → GET /movie/nowplaying
 *   - Trending     → GET /movie/trending
 *
 * Cards show a blue diagonal "W" badge if the movie is already in
 * the logged-in user's watchlist (fetched once on load).
 */
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Home | Absolute Cinema")
@AnonymousAllowed
public class HomeView extends VerticalLayout implements AfterNavigationObserver {

    private static final String TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w342";

    private final BackendClientService backendClient;
    private final UserSessionService userSessionService;
    private final TextField searchField;
    private final VerticalLayout contentArea;

    // Watchlist IDs fetched once on load — empty set for anonymous users
    private Set<Integer> watchlistIds = Set.of();
    // Favourites IDs fetched once on load — empty set for anonymous users
    private Set<Integer> favouriteIds = Set.of();

    public HomeView(BackendClientService backendClient, UserSessionService userSessionService) {
        this.backendClient = backendClient;
        this.userSessionService = userSessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "1200px").set("margin", "0 auto");

        // Pre-fetch watchlist and favourites for logged-in users
        if (isLoggedIn() && userSessionService.getUserId() != null) {
            long userId = userSessionService.getUserId();
            List<WatchlistDTO> watchlist = backendClient.getWatchlist(userId);
            watchlistIds = watchlist.stream()
                    .map(WatchlistDTO::getMovieId)
                    .collect(Collectors.toSet());
            watchlistIds = watchlistIds == null ? Set.of() : watchlistIds;

            List<ca.yorku.eecs4314group12.ui.data.dto.FavouriteMovieDTO> favourites =
                    backendClient.getFavourites(userId);
            favouriteIds = favourites.stream()
                    .map(ca.yorku.eecs4314group12.ui.data.dto.FavouriteMovieDTO::getMovieId)
                    .collect(Collectors.toSet());
            favouriteIds = favouriteIds == null ? Set.of() : favouriteIds;
        }

        // ---- Search bar ----
        searchField = new TextField();
        searchField.setPlaceholder("Search movies…");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("100%");
        searchField.getStyle().set("max-width", "600px");

        Button searchBtn = new Button("Search");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.addClickListener(e -> doSearch());
        searchField.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> doSearch());
        searchField.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().isBlank()) showSections();
        });

        HorizontalLayout searchRow = new HorizontalLayout(searchField, searchBtn);
        searchRow.setWidthFull();
        searchRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        searchRow.getStyle().set("padding", "var(--lumo-space-m) 0");

        contentArea = new VerticalLayout();
        contentArea.setPadding(false);
        contentArea.setSpacing(false);
        contentArea.setWidthFull();

        add(searchRow, contentArea);
        showSections();
    }

    // -------------------------------------------------------------------------
    // Sections
    // -------------------------------------------------------------------------

    private void showSections() {
        contentArea.removeAll();

        if (isLoggedIn() && userSessionService.getUserId() != null) {
            List<MovieListItemDTO> recommendations =
                    backendClient.getRecommendations(userSessionService.getUserId());
            if (!recommendations.isEmpty()) {
                contentArea.add(buildSection("Recommended for You ✨", recommendations));
            }
        }

        contentArea.add(buildSection("Now Playing", backendClient.getNowPlaying()));
        contentArea.add(buildSection("Trending", backendClient.getTrending()));
    }

    // -------------------------------------------------------------------------
    // Search
    // -------------------------------------------------------------------------

    private void doSearch() {
        String query = searchField.getValue();
        if (query == null || query.isBlank()) { showSections(); return; }

        contentArea.removeAll();
        List<MovieListItemDTO> results = backendClient.searchMovies(query);

        H2 heading = new H2("Results for \"" + query + "\"");
        heading.getStyle().set("margin", "var(--lumo-space-m) 0 var(--lumo-space-s) 0");

        if (results.isEmpty()) {
            Paragraph empty = new Paragraph("No movies found for \"" + query + "\".");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            contentArea.add(heading, empty);
            return;
        }

        // Collect all unique genres from results for the filter dropdown
        java.util.List<String> allGenres = results.stream()
                .filter(m -> m.getGenres() != null)
                .flatMap(m -> m.getGenres().stream())
                .filter(g -> g != null && !g.isBlank())
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());

        com.vaadin.flow.component.select.Select<String> genreFilter =
                new com.vaadin.flow.component.select.Select<>();
        genreFilter.setLabel("Filter by genre");
        java.util.List<String> genreOptions = new java.util.ArrayList<>();
        genreOptions.add("All genres");
        genreOptions.addAll(allGenres);
        genreFilter.setItems(genreOptions);
        genreFilter.setValue("All genres");
        genreFilter.getStyle().set("min-width", "180px");

        FlexLayout grid = buildGrid(results);

        genreFilter.addValueChangeListener(e -> {
            String selected = e.getValue();
            grid.removeAll();
            List<MovieListItemDTO> filtered = "All genres".equals(selected) ? results
                    : results.stream()
                            .filter(m -> m.getGenres() != null && m.getGenres().contains(selected))
                            .collect(java.util.stream.Collectors.toList());
            filtered.forEach(m -> grid.add(buildCard(m)));
        });

        HorizontalLayout resultsHeader = new HorizontalLayout(heading, genreFilter);
        resultsHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        resultsHeader.setWidthFull();
        resultsHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        contentArea.add(resultsHeader, grid);
    }

    // -------------------------------------------------------------------------
    // Section builder
    // -------------------------------------------------------------------------

    private VerticalLayout buildSection(String title, List<MovieListItemDTO> movies) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false); section.setSpacing(false); section.setWidthFull();
        section.getStyle().set("margin-bottom", "var(--lumo-space-xl)");

        H2 heading = new H2(title);
        heading.getStyle().set("margin", "var(--lumo-space-m) 0 var(--lumo-space-s) 0");

        if (movies.isEmpty()) {
            Paragraph empty = new Paragraph("No movies available right now.");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            section.add(heading, empty);
        } else {
            section.add(heading, buildGrid(movies));
        }
        return section;
    }

    // -------------------------------------------------------------------------
    // Grid
    // -------------------------------------------------------------------------

    private FlexLayout buildGrid(List<MovieListItemDTO> movies) {
        FlexLayout grid = new FlexLayout();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(160px, 1fr))")
                .set("gap", "var(--lumo-space-m)").set("width", "100%");
        for (MovieListItemDTO movie : movies) {
            grid.add(buildCard(movie));
        }
        return grid;
    }

    private Div buildCard(MovieListItemDTO movie) {
        boolean inWatchlist = watchlistIds.contains(movie.getId());

        // Outer wrapper — position:relative so the badge can be absolute inside
        Div wrapper = new Div();
        wrapper.getStyle()
                .set("position", "relative")
                .set("cursor", "pointer")
                .set("overflow", "hidden")
                .set("border-radius", "var(--lumo-border-radius-m)");
        wrapper.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("movie/" + movie.getId())));

        // Poster
        Div posterWrap = new Div();
        posterWrap.getStyle()
                .set("width", "100%").set("aspect-ratio", "2/3")
                .set("background", "var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("overflow", "hidden");

        String posterPath = movie.getPoster_path();
        if (posterPath != null && !posterPath.isBlank()) {
            String url = posterPath.startsWith("http") ? posterPath : TMDB_IMAGE_BASE + posterPath;
            Image img = new Image(url, movie.getTitle() != null ? movie.getTitle() : "");
            img.getStyle().set("width", "100%").set("height", "100%")
                    .set("object-fit", "cover").set("display", "block");
            posterWrap.add(img);
        } else {
            Div fallback = new Div();
            fallback.getStyle()
                    .set("width", "100%").set("height", "100%")
                    .set("display", "flex").set("align-items", "center")
                    .set("justify-content", "center").set("font-size", "2rem");
            String initial = movie.getTitle() != null && !movie.getTitle().isBlank()
                    ? movie.getTitle().substring(0, 1).toUpperCase() : "?";
            fallback.add(new Span(initial));
            posterWrap.add(fallback);
        }

        // Title / meta overlay
        Div overlay = new Div();
        overlay.addClassName("movie-card__overlay");
        Span title = new Span(movie.getTitle() != null ? movie.getTitle() : "Unknown");
        title.addClassName("movie-card__title");
        String year = movie.getYear();
        String genreStr = movie.getGenres() != null && !movie.getGenres().isEmpty()
                ? movie.getGenres().get(0) : "";
        String metaText = year.isBlank() ? genreStr : (genreStr.isBlank() ? year : year + " · " + genreStr);
        Span meta = new Span(metaText);
        meta.addClassName("movie-card__meta");
        overlay.add(title, meta);

        wrapper.add(posterWrap, overlay);

        // Watchlist badge — blue diagonal strip in top-right corner
        if (inWatchlist) {
            Div badge = new Div();
            badge.getStyle()
                    .set("position", "absolute")
                    .set("top", "0")
                    .set("right", "0")
                    .set("width", "80px")
                    .set("height", "80px")
                    .set("overflow", "hidden")
                    .set("pointer-events", "none");

            Div strip = new Div();
            strip.setText("W");
            strip.getStyle()
                    .set("position", "absolute")
                    .set("top", "20px")
                    .set("right", "-30px")
                    .set("width", "120px")
                    .set("background", "#1565C0")
                    .set("color", "white")
                    .set("font-size", "10px")
                    .set("font-weight", "700")
                    .set("text-align", "center")
                    .set("padding", "3px 0")
                    .set("transform", "rotate(45deg)")
                    .set("letter-spacing", "0.05em");

            badge.add(strip);
            wrapper.add(badge);
        }

        // Favourite star — top-left corner, logged-in users only
        // Hollow ☆ for non-favourited, filled yellow ★ for favourited
        if (isLoggedIn()) {
            boolean inFavourites = favouriteIds.contains(movie.getId());
            Div starBadge = new Div();
            starBadge.setText(inFavourites ? "★" : "☆");
            starBadge.getStyle()
                    .set("position", "absolute")
                    .set("top", "6px")
                    .set("left", "8px")
                    .set("font-size", "20px")
                    .set("color", inFavourites ? "#FFD600" : "rgba(255,255,255,0.7)")
                    .set("text-shadow", "0 1px 3px rgba(0,0,0,0.6)")
                    .set("pointer-events", "none")
                    .set("line-height", "1");
            wrapper.add(starBadge);
        }

        return wrapper;
    }

    // -------------------------------------------------------------------------
    // After navigation — reload badges when returning to home page
    // -------------------------------------------------------------------------

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (isLoggedIn() && userSessionService.getUserId() != null) {
            long userId = userSessionService.getUserId();
            List<WatchlistDTO> watchlist = backendClient.getWatchlist(userId);
            watchlistIds = watchlist.stream()
                    .map(WatchlistDTO::getMovieId)
                    .collect(Collectors.toSet());

            List<FavouriteMovieDTO> favourites = backendClient.getFavourites(userId);
            favouriteIds = favourites.stream()
                    .map(FavouriteMovieDTO::getMovieId)
                    .collect(Collectors.toSet());

            showSections();
        }
    }

    // -------------------------------------------------------------------------
    // Auth helper
    // -------------------------------------------------------------------------

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");
    }
}