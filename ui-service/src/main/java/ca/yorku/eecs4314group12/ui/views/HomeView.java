package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.Movie;
import ca.yorku.eecs4314group12.ui.data.MovieSearchService;
import ca.yorku.eecs4314group12.ui.data.SearchFilter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

/**
 * Home / discovery page.
 *
 * Features a live search bar and collapsible advanced filter panel.
 * Filter logic is delegated to {@link MovieSearchService}.
 *
 * Searchable fields: title, original title, director, cast, tagline,
 *                    overview, genre, year, production company.
 * Filterable by: genre, year range, min user review score, min TMDB score,
 *                original language.
 * Sortable by: title, year, user score, TMDB score, runtime.
 *
 * TODO: Replace DummyDataService-backed MovieSearchService with WebClient
 *       calls to movie-service once the search/filter API is finalized.
 */
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Home | Absolute Cinema")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private static final List<String> ALL_GENRES = List.of(
            "Action", "Adventure", "Animation", "Comedy", "Crime",
            "Documentary", "Drama", "Fantasy", "Horror", "Mystery",
            "Romance", "Sci-Fi", "Thriller", "Western"
    );

    private static final List<String> LANGUAGES = List.of(
            "Any", "English (en)", "Korean (ko)", "French (fr)",
            "Spanish (es)", "Japanese (ja)", "German (de)", "Italian (it)"
    );

    private final MovieSearchService searchService;

    // Filter state
    private String currentQuery    = "";
    private String currentGenre    = "";
    private String currentYearFrom = "";
    private String currentYearTo   = "";
    private double currentMinUser  = 0;
    private double currentMinTmdb  = 0;
    private String currentLang     = "";
    private String currentSort     = "title";

    // UI refs that need to be updated
    private FlexLayout resultsGrid;
    private Span resultCount;
    private Div filterPanel;
    private boolean filtersVisible = false;

    public HomeView(MovieSearchService searchService) {
        this.searchService = searchService;
        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("gap", "24px");

        // Grid using CSS class for proper grid layout
        resultsGrid = new FlexLayout();
        resultsGrid.addClassName("movies-grid");
        resultsGrid.getStyle().set("width", "100%");

        resultCount = new Span();
        resultCount.addClassName("result-count-badge");

        filterPanel = buildFilterPanel();
        filterPanel.setVisible(false);

        add(buildSearchBar(), filterPanel, buildResultsSection());
        runSearch();
    }

    // -------------------------------------------------------------------------
    // Search bar
    // -------------------------------------------------------------------------

    private VerticalLayout buildSearchBar() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search movies, genres, directors, cast…");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("100%");
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> {
            currentQuery = e.getValue();
            runSearch();
        });

        Button filterToggle = new Button("Filters", VaadinIcon.FILTER.create());
        filterToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        filterToggle.addClickListener(e -> {
            filtersVisible = !filtersVisible;
            filterPanel.setVisible(filtersVisible);
            filterToggle.setText(filtersVisible ? "Hide Filters" : "Filters");
        });

        HorizontalLayout row = new HorizontalLayout(searchField, filterToggle);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.expand(searchField);

        VerticalLayout wrap = new VerticalLayout(row);
        wrap.addClassName("home-search-wrap");
        wrap.setPadding(false);
        wrap.setSpacing(false);
        return wrap;
    }

    // -------------------------------------------------------------------------
    // Filter panel
    // -------------------------------------------------------------------------

    private Div buildFilterPanel() {
        // Genre
        ComboBox<String> genreBox = new ComboBox<>("Genre");
        genreBox.setItems(ALL_GENRES);
        genreBox.setClearButtonVisible(true);
        genreBox.setPlaceholder("Any genre");
        genreBox.addValueChangeListener(e -> {
            currentGenre = e.getValue() == null ? "" : e.getValue();
            runSearch();
        });

        // Year from/to
        TextField yearFrom = new TextField("From year");
        yearFrom.setPlaceholder("e.g. 1990");
        yearFrom.setWidth("120px");
        yearFrom.addValueChangeListener(e -> { currentYearFrom = e.getValue(); runSearch(); });

        TextField yearTo = new TextField("To year");
        yearTo.setPlaceholder("e.g. 2024");
        yearTo.setWidth("120px");
        yearTo.addValueChangeListener(e -> { currentYearTo = e.getValue(); runSearch(); });

        // Min scores
        NumberField minUserScore = new NumberField("Min user score");
        minUserScore.setMin(0); minUserScore.setMax(10); minUserScore.setStep(0.5);
        minUserScore.setPlaceholder("0–10");
        minUserScore.setWidth("130px");
        minUserScore.addValueChangeListener(e -> {
            currentMinUser = e.getValue() == null ? 0 : e.getValue();
            runSearch();
        });

        NumberField minTmdbScore = new NumberField("Min TMDB score");
        minTmdbScore.setMin(0); minTmdbScore.setMax(10); minTmdbScore.setStep(0.5);
        minTmdbScore.setPlaceholder("0–10");
        minTmdbScore.setWidth("130px");
        minTmdbScore.addValueChangeListener(e -> {
            currentMinTmdb = e.getValue() == null ? 0 : e.getValue();
            runSearch();
        });

        // Language
        ComboBox<String> langBox = new ComboBox<>("Language");
        langBox.setItems(LANGUAGES);
        langBox.setValue("Any");
        langBox.addValueChangeListener(e -> {
            String val = e.getValue();
            if (val == null || val.equals("Any")) {
                currentLang = "";
            } else {
                currentLang = val.replaceAll(".*\\((.*)\\)", "$1");
            }
            runSearch();
        });

        // Sort
        Select<String> sortSelect = new Select<>();
        sortSelect.setLabel("Sort by");
        sortSelect.setItems("Title", "Year", "User score", "TMDB score", "Runtime");
        sortSelect.setValue("Title");
        sortSelect.addValueChangeListener(e -> {
            currentSort = switch (e.getValue()) {
                case "Year"       -> "year";
                case "User score" -> "userScore";
                case "TMDB score" -> "tmdbScore";
                case "Runtime"    -> "runtime";
                default           -> "title";
            };
            runSearch();
        });

        // Reset
        Button reset = new Button("Reset filters");
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        reset.addClickListener(e -> {
            genreBox.clear(); yearFrom.clear(); yearTo.clear();
            minUserScore.clear(); minTmdbScore.clear();
            langBox.setValue("Any"); sortSelect.setValue("Title");
            currentGenre = ""; currentYearFrom = ""; currentYearTo = "";
            currentMinUser = 0; currentMinTmdb = 0; currentLang = ""; currentSort = "title";
            runSearch();
        });

        FlexLayout controls = new FlexLayout(
                genreBox, yearFrom, yearTo, minUserScore, minTmdbScore, langBox, sortSelect, reset);
        controls.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        controls.getStyle().set("gap", "var(--lumo-space-m)").set("align-items", "flex-end");

        Div panel = new Div(controls);
        panel.addClassName("home-filter-panel");
        return panel;
    }

    // -------------------------------------------------------------------------
    // Results section
    // -------------------------------------------------------------------------

    private VerticalLayout buildResultsSection() {
        H3 sectionTitle = new H3("Films");
        sectionTitle.addClassName("section-heading");
        sectionTitle.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(sectionTitle, resultCount);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("margin-bottom", "var(--lumo-space-m)")
                .set("gap", "var(--lumo-space-m)");

        VerticalLayout section = new VerticalLayout(header, resultsGrid);
        section.setPadding(false);
        section.setSpacing(false);
        return section;
    }

    // -------------------------------------------------------------------------
    // Search execution
    // -------------------------------------------------------------------------

    private void runSearch() {
        SearchFilter filter = new SearchFilter(
                currentQuery, currentGenre, currentYearFrom, currentYearTo,
                currentMinUser, currentMinTmdb, currentLang, currentSort
        );
        List<Movie> results = searchService.search(filter);

        resultsGrid.removeAll();
        results.forEach(m -> resultsGrid.add(buildMovieCard(m)));

        int count = results.size();
        resultCount.setText(count + " film" + (count == 1 ? "" : "s"));
    }

    // -------------------------------------------------------------------------
    // Movie card — poster-first cinematic layout
    // -------------------------------------------------------------------------

    private Div buildMovieCard(Movie movie) {
        // Poster area fills the card
        Div poster = new Div();
        poster.addClassName("movie-card__poster");
        poster.setText(movie.getPosterEmoji());

        // Gradient overlay — scores + title + meta at bottom
        Div overlay = new Div();
        overlay.addClassName("movie-card__overlay");

        Div scores = buildScoreBadges(movie);
        scores.addClassName("movie-card__scores");

        Span title = new Span(movie.getTitle());
        title.addClassName("movie-card__title");

        Span meta = new Span(movie.getYear() + " · " + movie.getGenre());
        meta.addClassName("movie-card__meta");

        overlay.add(scores, title, meta);

        Div card = new Div(poster, overlay);
        card.addClassName("movie-card");
        card.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("movie/" + movie.getId())));

        return card;
    }

    /**
     * Two small score badges: TMDB (grey) and user score (gold).
     */
    private Div buildScoreBadges(Movie movie) {
        Div badges = new Div();
        badges.getStyle().set("display", "flex").set("gap", "5px").set("flex-wrap", "wrap");

        if (movie.getTmdbScore() > 0) {
            Span tmdb = new Span("🎬 " + movie.getTmdbScore());
            tmdb.addClassNames("score-badge", "score-badge--tmdb");
            badges.add(tmdb);
        }

        if (movie.getUserScore() > 0) {
            Span user = new Span("★ " + movie.getUserScore());
            user.addClassNames("score-badge", "score-badge--user");
            badges.add(user);
        }

        return badges;
    }
}