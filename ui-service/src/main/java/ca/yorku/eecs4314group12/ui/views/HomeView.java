package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.DummyDataService;
import ca.yorku.eecs4314group12.ui.data.Movie;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

/**
 * Home / discovery page.
 *
 * Shows a search bar and a grid of featured movies.
 * Movie data currently comes from DummyDataService.
 * TODO: Wire to movie-service REST API once contract is finalized.
 */
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Home | Absolute Cinema")
@PermitAll
public class HomeView extends VerticalLayout {

    private final DummyDataService dataService;

    public HomeView(DummyDataService dataService) {
        this.dataService = dataService;
        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "1100px").set("margin", "0 auto");

        add(buildHero(), buildMovieGrid());
    }

    // -------------------------------------------------------------------------
    // Hero / search section
    // -------------------------------------------------------------------------

    private VerticalLayout buildHero() {
        H2 heading = new H2("What are you watching tonight?");
        heading.getStyle().set("margin-bottom", "var(--lumo-space-xs)");

        Paragraph sub = new Paragraph("Browse reviews, discover new films, and share your opinions.");
        sub.getStyle().set("color", "var(--lumo-secondary-text-color)")
                      .set("margin-top", "0");

        TextField search = new TextField();
        search.setPlaceholder("Search movies, genres, directors…");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setWidth("100%");
        search.setMaxWidth("520px");
        // TODO: wire search to movie-service when available

        VerticalLayout hero = new VerticalLayout(heading, sub, search);
        hero.setPadding(false);
        hero.setSpacing(false);
        hero.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-l)");
        return hero;
    }

    // -------------------------------------------------------------------------
    // Featured movies grid
    // -------------------------------------------------------------------------

    private VerticalLayout buildMovieGrid() {
        H3 sectionTitle = new H3("Featured Films");
        sectionTitle.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        FlexLayout grid = new FlexLayout();
        grid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        grid.getStyle().set("gap", "var(--lumo-space-m)");

        for (Movie movie : dataService.getFeaturedMovies()) {
            grid.add(buildMovieCard(movie));
        }

        VerticalLayout section = new VerticalLayout(sectionTitle, grid);
        section.setPadding(false);
        section.setSpacing(false);
        return section;
    }

    private Div buildMovieCard(Movie movie) {
        // Poster placeholder
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
        title.getStyle().set("font-weight", "600").set("font-size", "var(--lumo-font-size-m)");

        Span meta = new Span(movie.getYear() + " · " + movie.getGenre());
        meta.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)");

        Span stars = new Span(buildStarString(movie.getRating()) + "  " + movie.getRating());
        stars.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-primary-text-color)");

        VerticalLayout info = new VerticalLayout(title, meta, stars);
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
                .set("transition", "box-shadow 0.2s");

        card.getElement().addEventListener("mouseover",
                e -> card.getStyle().set("box-shadow", "var(--lumo-box-shadow-m)"));
        card.getElement().addEventListener("mouseout",
                e -> card.getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)"));

        return card;
    }

    private String buildStarString(double rating) {
        int full = (int) Math.round(rating);
        return "★".repeat(full) + "☆".repeat(Math.max(0, 5 - full));
    }
}