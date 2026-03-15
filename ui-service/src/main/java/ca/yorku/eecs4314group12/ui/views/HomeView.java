package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.MovieListItemDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

/**
 * Home / discovery page.
 *
 * Shows two live sections from movie-service:
 *   1. Now Playing  → GET /movie/nowplaying
 *   2. Trending     → GET /movie/trending
 *
 * Search bar hits → GET /movie/search/{query} and replaces both sections
 * with results. Clearing the search restores the two sections.
 *
 * Each card shows the TMDB poster image and navigates to /movie/{id} on click.
 */
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Home | Absolute Cinema")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private static final String TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w342";

    private final BackendClientService backendClient;
    private final TextField searchField;
    private final VerticalLayout contentArea;

    public HomeView(BackendClientService backendClient) {
        this.backendClient = backendClient;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "1200px").set("margin", "0 auto");

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

        // ---- Content area ----
        contentArea = new VerticalLayout();
        contentArea.setPadding(false);
        contentArea.setSpacing(false);
        contentArea.setWidthFull();

        add(searchRow, contentArea);
        showSections();
    }

    // -------------------------------------------------------------------------
    // Sections (Now Playing + Trending)
    // -------------------------------------------------------------------------

    private void showSections() {
        contentArea.removeAll();
        contentArea.add(buildSection("Now Playing", backendClient.getNowPlaying()));
        contentArea.add(buildSection("Trending", backendClient.getTrending()));
    }

    // -------------------------------------------------------------------------
    // Search
    // -------------------------------------------------------------------------

    private void doSearch() {
        String query = searchField.getValue();
        if (query == null || query.isBlank()) {
            showSections();
            return;
        }

        contentArea.removeAll();
        List<MovieListItemDTO> results = backendClient.searchMovies(query);

        H2 heading = new H2("Results for \"" + query + "\"");
        heading.getStyle().set("margin", "var(--lumo-space-m) 0 var(--lumo-space-s) 0");

        if (results.isEmpty()) {
            Paragraph empty = new Paragraph("No movies found for \"" + query + "\".");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            contentArea.add(heading, empty);
        } else {
            contentArea.add(heading, buildGrid(results));
        }
    }

    // -------------------------------------------------------------------------
    // Section builder
    // -------------------------------------------------------------------------

    private VerticalLayout buildSection(String title, List<MovieListItemDTO> movies) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.setWidthFull();
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
    // Grid of movie cards
    // -------------------------------------------------------------------------

    private FlexLayout buildGrid(List<MovieListItemDTO> movies) {
        FlexLayout grid = new FlexLayout();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(160px, 1fr))")
                .set("gap", "var(--lumo-space-m)")
                .set("width", "100%");

        for (MovieListItemDTO movie : movies) {
            grid.add(buildCard(movie));
        }

        return grid;
    }

    private Div buildCard(MovieListItemDTO movie) {
        Div card = new Div();
        card.addClassName("movie-card");
        card.getStyle().set("cursor", "pointer");
        card.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("movie/" + movie.getId())));

        // Poster image
        Div posterWrap = new Div();
        posterWrap.addClassName("movie-card__poster");
        posterWrap.getStyle()
                .set("width", "100%")
                .set("aspect-ratio", "2/3")
                .set("overflow", "hidden")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("background", "var(--lumo-contrast-10pct)");

        String posterPath = movie.getPoster_path();
        if (posterPath != null && !posterPath.isBlank()) {
            String url = posterPath.startsWith("http") ? posterPath : TMDB_IMAGE_BASE + posterPath;
            Image img = new Image(url, movie.getTitle() != null ? movie.getTitle() : "");
            img.getStyle()
                    .set("width", "100%")
                    .set("height", "100%")
                    .set("object-fit", "cover")
                    .set("display", "block");
            posterWrap.add(img);
        } else {
            // Fallback: title initial
            Div fallback = new Div();
            fallback.getStyle()
                    .set("width", "100%").set("height", "100%")
                    .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                    .set("font-size", "2rem");
            String initial = movie.getTitle() != null && !movie.getTitle().isBlank()
                    ? movie.getTitle().substring(0, 1).toUpperCase() : "?";
            fallback.add(new Span(initial));
            posterWrap.add(fallback);
        }

        // Overlay with title + meta
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
        card.add(posterWrap, overlay);

        return card;
    }
}