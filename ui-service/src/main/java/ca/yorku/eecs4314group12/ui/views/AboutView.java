package ca.yorku.eecs4314group12.ui.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * About / Credits page.
 *
 * Contains the required TMDB attribution notice as per TMDB API terms of service:
 * https://www.themoviedb.org/api-terms-of-use
 */
@Route(value = "about", layout = MainLayout.class)
@PageTitle("About | Absolute Cinema")
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "800px").set("margin", "0 auto").set("gap", "var(--lumo-space-xl)");

        add(buildAboutSection(), buildTmdbSection(), buildTeamSection());
    }

    // -------------------------------------------------------------------------
    // About the project
    // -------------------------------------------------------------------------

    private Div buildAboutSection() {
        H2 heading = new H2("About Absolute Cinema");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-s) 0");

        Paragraph description = new Paragraph(
                "Absolute Cinema is a movie review platform built as part of EECS 4314 (Group 12) " +
                "at York University. It is a microservices application built with Spring Boot, " +
                "Vaadin 25, and PostgreSQL.");
        description.getStyle().set("line-height", "1.7").set("margin", "0");

        return card(heading, description);
    }

    // -------------------------------------------------------------------------
    // TMDB attribution — required by TMDB API terms of service
    // -------------------------------------------------------------------------

    private Div buildTmdbSection() {
        H3 heading = new H3("Data & Image Credits");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");

        // TMDB logo
        Image tmdbLogo = new Image(
            "https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_2-d537fb228cf3ded904ef09b136fe3fec72548ebc1fea3fbbd1ad9e36364db38b.svg",
            "The Movie Database (TMDB)");
        tmdbLogo.getStyle()
                .set("height", "60px")
                .set("width", "60px")
                .set("margin-bottom", "var(--lumo-space-m)");

        Anchor tmdbLogoLink = new Anchor("https://www.themoviedb.org", tmdbLogo);
        tmdbLogoLink.setTarget("_blank");

        Paragraph notice = new Paragraph(
                "This website uses the TMDB API but is not endorsed or certified by TMDB.");
        notice.getStyle()
                .set("font-weight", "600")
                .set("margin", "0 0 var(--lumo-space-s) 0");

        Paragraph details = new Paragraph();
        details.getStyle().set("line-height", "1.7").set("margin", "0 0 var(--lumo-space-s) 0");
        details.setText(
                "All movie data including titles, descriptions, release dates, ratings, genres, " +
                "cast information, and images are provided by The Movie Database (TMDB). " +
                "TMDB is a community-built movie and TV database.");

        Anchor apiLink = new Anchor("https://www.themoviedb.org/api-terms-of-use", "TMDB API Terms of Use");
        apiLink.setTarget("_blank");
        apiLink.getStyle().set("font-size", "var(--lumo-font-size-s)");

        Anchor tmdbSiteLink = new Anchor("https://www.themoviedb.org", "Visit themoviedb.org");
        tmdbSiteLink.setTarget("_blank");
        tmdbSiteLink.getStyle().set("font-size", "var(--lumo-font-size-s)");

        Span separator = new Span(" · ");
        separator.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Div links = new Div(tmdbSiteLink, separator, apiLink);

        return card(heading, tmdbLogoLink, notice, details, links);
    }

    // -------------------------------------------------------------------------
    // Team
    // -------------------------------------------------------------------------

    private Div buildTeamSection() {
        H3 heading = new H3("Project Team");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-s) 0");

        Paragraph team = new Paragraph("EECS 4314 — Group 12 — York University");
        team.getStyle().set("margin", "0");

        return card(heading, team);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Div card(com.vaadin.flow.component.Component... components) {
        Div card = new Div(components);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("width", "100%");
        return card;
    }
}