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
        getStyle().set("max-width", "1100px").set("margin", "0 auto");

        resultsGrid = new FlexLayout();
        resultsGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        resultsGrid.getStyle().set("gap", "var(--lumo-space-m)");

        resultCount = new Span();
        resultCount.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        filterPanel = buildFilterPanel();
        filterPanel.setVisible(false);

        add(buildSearchBar(), filterPanel, buildResultsSection());
        runSearch(); // populate grid with full catalogue on load
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

        Select<String> sortSelect = new Select<>();
        sortSelect.setItems("Title", "Year", "User Score", "TMDB Score", "Runtime");
        sortSelect.setValue("Title");
        sortSelect.setWidth("160px");
        sortSelect.addValueChangeListener(e -> {
            currentSort = switch (e.getValue()) {
                case "Year"       -> "year";
                case "User Score" -> "userScore";
                case "TMDB Score" -> "tmdbScore";
                case "Runtime"    -> "runtime";
                default           -> "title";
            };
            runSearch();
        });

        HorizontalLayout controls = new HorizontalLayout(searchField, filterToggle, sortSelect);
        controls.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        controls.setWidth("100%");
        controls.expand(searchField);

        H2 heading = new H2("What are you watching tonight?");
        heading.getStyle().set("margin-bottom", "var(--lumo-space-xs)");

        Paragraph sub = new Paragraph("Browse reviews, discover new films, and share your opinions.");
        sub.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0");

        VerticalLayout bar = new VerticalLayout(heading, sub, controls);
        bar.setPadding(false);
        bar.setSpacing(false);
        bar.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-m)")
                .set("gap", "var(--lumo-space-s)");
        return bar;
    }

    // -------------------------------------------------------------------------
    // Filter panel
    // -------------------------------------------------------------------------

    private Div buildFilterPanel() {
        // Genre
        ComboBox<String> genreBox = new ComboBox<>("Genre");
        genreBox.setItems(ALL_GENRES);
        genreBox.setPlaceholder("Any");
        genreBox.setClearButtonVisible(true);
        genreBox.addValueChangeListener(e -> {
            currentGenre = e.getValue() == null ? "" : e.getValue();
            runSearch();
        });

        // Year range
        TextField yearFrom = new TextField("Year from");
        yearFrom.setPlaceholder("e.g. 1990");
        yearFrom.setWidth("120px");
        yearFrom.addValueChangeListener(e -> { currentYearFrom = e.getValue(); runSearch(); });

        TextField yearTo = new TextField("Year to");
        yearTo.setPlaceholder("e.g. 2024");
        yearTo.setWidth("120px");
        yearTo.addValueChangeListener(e -> { currentYearTo = e.getValue(); runSearch(); });

        HorizontalLayout yearRange = new HorizontalLayout(yearFrom, yearTo);
        yearRange.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        yearRange.setSpacing(true);

        // Min user score
        NumberField minUserScore = new NumberField("Min User Score");
        minUserScore.setPlaceholder("0–10");
        minUserScore.setMin(0);
        minUserScore.setMax(10);
        minUserScore.setStep(0.5);
        minUserScore.setWidth("140px");
        minUserScore.setHelperText("Based on our users' reviews");
        minUserScore.addValueChangeListener(e -> {
            currentMinUser = e.getValue() == null ? 0 : e.getValue();
            runSearch();
        });

        // Min TMDB score
        NumberField minTmdbScore = new NumberField("Min TMDB Score");
        minTmdbScore.setPlaceholder("0–10");
        minTmdbScore.setMin(0);
        minTmdbScore.setMax(10);
        minTmdbScore.setStep(0.5);
        minTmdbScore.setWidth("140px");
        minTmdbScore.setHelperText("From The Movie Database");
        minTmdbScore.addValueChangeListener(e -> {
            currentMinTmdb = e.getValue() == null ? 0 : e.getValue();
            runSearch();
        });

        // Language
        ComboBox<String> langBox = new ComboBox<>("Original Language");
        langBox.setItems(LANGUAGES);
        langBox.setPlaceholder("Any");
        langBox.setClearButtonVisible(true);
        langBox.addValueChangeListener(e -> {
            String val = e.getValue();
            if (val == null || val.equals("Any")) {
                currentLang = "";
            } else {
                // Extract the ISO code from "English (en)" → "en"
                int start = val.lastIndexOf('(');
                int end   = val.lastIndexOf(')');
                currentLang = (start >= 0 && end > start)
                        ? val.substring(start + 1, end)
                        : val;
            }
            runSearch();
        });

        // Quick score presets
        Button btn7User = new Button("User ≥ 7", e -> {
            currentMinUser = 7;
            minUserScore.setValue(7.0);
        });
        btn7User.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        Button btn8User = new Button("User ≥ 8", e -> {
            currentMinUser = 8;
            minUserScore.setValue(8.0);
        });
        btn8User.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        Button btn9User = new Button("User ≥ 9", e -> {
            currentMinUser = 9;
            minUserScore.setValue(9.0);
        });
        btn9User.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        Button clearAll = new Button("Clear All Filters", VaadinIcon.CLOSE_SMALL.create());
        clearAll.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        clearAll.addClickListener(e -> {
            currentGenre = ""; currentYearFrom = ""; currentYearTo = "";
            currentMinUser = 0; currentMinTmdb = 0; currentLang = "";
            genreBox.clear(); yearFrom.clear(); yearTo.clear();
            minUserScore.clear(); minTmdbScore.clear(); langBox.clear();
            runSearch();
        });

        HorizontalLayout presets = new HorizontalLayout(
                new Span("Quick filters:"), btn7User, btn8User, btn9User, clearAll);
        presets.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        presets.getStyle().set("flex-wrap", "wrap").set("gap", "var(--lumo-space-xs)");

        FlexLayout row1 = new FlexLayout(genreBox, yearRange, langBox);
        row1.getStyle().set("gap", "var(--lumo-space-l)").set("flex-wrap", "wrap")
                .set("align-items", "flex-end");

        FlexLayout row2 = new FlexLayout(minUserScore, minTmdbScore);
        row2.getStyle().set("gap", "var(--lumo-space-l)").set("flex-wrap", "wrap")
                .set("align-items", "flex-end");

        VerticalLayout inner = new VerticalLayout(row1, row2, presets);
        inner.setPadding(false);
        inner.setSpacing(false);
        inner.getStyle().set("gap", "var(--lumo-space-m)");

        Div panel = new Div(inner);
        panel.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-m)");
        panel.setWidth("100%");
        return panel;
    }

    // -------------------------------------------------------------------------
    // Results section
    // -------------------------------------------------------------------------

    private VerticalLayout buildResultsSection() {
        H3 sectionTitle = new H3("Films");
        sectionTitle.getStyle().set("margin-bottom", "0");

        HorizontalLayout header = new HorizontalLayout(sectionTitle, resultCount);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.getStyle().set("margin-bottom", "var(--lumo-space-m)").set("gap", "var(--lumo-space-m)");

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
        resultCount.setText(count + " film" + (count == 1 ? "" : "s") + " found");
    }

    // -------------------------------------------------------------------------
    // Movie card
    // -------------------------------------------------------------------------

    private Div buildMovieCard(Movie movie) {
        Div poster = new Div();
        poster.getStyle()
                .set("background", "var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "3.5rem")
                .set("height", "160px");
        poster.setText(movie.getPosterEmoji());

        Span title = new Span(movie.getTitle());
        title.getStyle()
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-m)");

        Span meta = new Span(movie.getYear() + " · " + movie.getGenre());
        meta.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)");

        // Dual score badges
        Div scores = buildScoreBadges(movie);

        VerticalLayout info = new VerticalLayout(title, meta, scores);
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("gap", "4px");

        Div card = new Div(poster, info);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("width", "200px")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("cursor", "pointer")
                .set("transition", "box-shadow 0.2s, transform 0.15s");

        card.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("movie/" + movie.getId())));

        card.getElement().addEventListener("mouseover",
                e -> card.getStyle()
                        .set("box-shadow", "var(--lumo-box-shadow-m)")
                        .set("transform", "translateY(-2px)"));
        card.getElement().addEventListener("mouseout",
                e -> card.getStyle()
                        .set("box-shadow", "var(--lumo-box-shadow-xs)")
                        .set("transform", "translateY(0)"));

        return card;
    }

    /**
     * Renders two small score badges on each card:
     *   🎬  TMDB score  (grey/secondary)
     *   👥  User score  (primary colour, only shown if reviews exist)
     */
    private Div buildScoreBadges(Movie movie) {
        Div badges = new Div();
        badges.getStyle().set("display", "flex").set("gap", "4px").set("flex-wrap", "wrap");

        // TMDB score
        if (movie.getTmdbScore() > 0) {
            Span tmdb = new Span("🎬 " + movie.getTmdbScore());
            tmdb.getStyle()
                    .set("background", "var(--lumo-contrast-10pct)")
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("border-radius", "999px")
                    .set("padding", "1px 7px")
                    .set("font-size", "var(--lumo-font-size-xs)")
                    .set("font-weight", "600");
            badges.add(tmdb);
        }

        // User score — only shown when reviews exist
        if (movie.getUserScore() > 0) {
            Span user = new Span("👥 " + movie.getUserScore());
            user.getStyle()
                    .set("background", "var(--lumo-primary-color-10pct)")
                    .set("color", "var(--lumo-primary-color)")
                    .set("border-radius", "999px")
                    .set("padding", "1px 7px")
                    .set("font-size", "var(--lumo-font-size-xs)")
                    .set("font-weight", "600");
            badges.add(user);
        }

        return badges;
    }
}