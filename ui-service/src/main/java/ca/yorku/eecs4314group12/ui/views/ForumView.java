package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.ForumPostDTO;
import com.vaadin.flow.component.Key;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Forum directory — lists all categories as clickable tiles.
 * Includes a search bar that searches across all posts by title.
 */
@Route(value = "forum", layout = MainLayout.class)
@PageTitle("Forum | Absolute Cinema")
@AnonymousAllowed
public class ForumView extends VerticalLayout {

    private final BackendClientService backendClient;
    private final VerticalLayout contentArea;

    public ForumView(BackendClientService backendClient) {
        this.backendClient = backendClient;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "860px").set("margin", "0 auto");

        H2 heading = new H2("Forum");
        heading.getStyle().set("margin", "var(--lumo-space-m) 0 var(--lumo-space-xs) 0");

        Paragraph subtitle = new Paragraph(
                "Browse discussions by category, or start a new conversation.");
        subtitle.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin", "0 0 var(--lumo-space-m) 0");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search posts by title…");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("100%");

        Button searchBtn = new Button("Search");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout searchRow = new HorizontalLayout(searchField, searchBtn);
        searchRow.setWidthFull();
        searchRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        searchRow.getStyle().set("margin-bottom", "var(--lumo-space-l)");

        contentArea = new VerticalLayout();
        contentArea.setPadding(false); contentArea.setSpacing(false);
        contentArea.setWidthFull();

        Runnable doSearch = () -> {
            String q = searchField.getValue();
            if (q == null || q.isBlank()) showDirectory();
            else showSearchResults(q.trim());
        };

        searchBtn.addClickListener(e -> doSearch.run());
        searchField.addKeyPressListener(Key.ENTER, e -> doSearch.run());
        searchField.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().isBlank()) showDirectory();
        });

        add(heading, subtitle, searchRow, contentArea);
        showDirectory();
    }

    // -------------------------------------------------------------------------
    // Directory
    // -------------------------------------------------------------------------

    private void showDirectory() {
        contentArea.removeAll();
        contentArea.add(buildDirectoryGrid());
    }

    private FlexLayout buildDirectoryGrid() {
        List<ForumPostDTO> allPosts = backendClient.getAllPosts();

        LinkedHashMap<String, CategoryInfo> categories = new LinkedHashMap<>();
        categories.put("general", new CategoryInfo("General", 0));

        for (ForumPostDTO post : allPosts) {
            String key = post.getCategoryKey();
            String display = post.getCategoryDisplay();
            categories.computeIfAbsent(key, k -> new CategoryInfo(display, 0)).increment();
        }

        FlexLayout grid = new FlexLayout();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(200px, 1fr))")
                .set("gap", "var(--lumo-space-m)")
                .set("width", "100%");

        for (Map.Entry<String, CategoryInfo> entry : categories.entrySet()) {
            grid.add(buildCategoryTile(entry.getValue().displayName, entry.getKey(),
                    entry.getValue().count));
        }
        return grid;
    }

    private Div buildCategoryTile(String displayName, String categoryKey, int postCount) {
        Div tile = new Div();
        tile.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("cursor", "pointer")
                .set("transition", "box-shadow 0.15s, border-color 0.15s")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");

        tile.getElement().addEventListener("mouseover", e ->
                tile.getStyle().set("border-color", "var(--lumo-primary-color)")
                        .set("box-shadow", "var(--lumo-box-shadow-m)"));
        tile.getElement().addEventListener("mouseout", e ->
                tile.getStyle().set("border-color", "var(--lumo-contrast-10pct)")
                        .set("box-shadow", "var(--lumo-box-shadow-xs)"));

        tile.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("forum/" + categoryKey)));

        Span icon = new Span(getCategoryIcon(categoryKey));
        icon.getStyle().set("font-size", "2rem").set("display", "block")
                .set("margin-bottom", "var(--lumo-space-s)");

        H3 name = new H3(displayName);
        name.getStyle().set("margin", "0 0 var(--lumo-space-xs) 0")
                .set("font-size", "var(--lumo-font-size-m)");

        Span count = new Span(postCount + (postCount == 1 ? " post" : " posts"));
        count.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-secondary-text-color)");

        tile.add(icon, name, count);
        return tile;
    }

    // -------------------------------------------------------------------------
    // Search results
    // -------------------------------------------------------------------------

    private void showSearchResults(String keyword) {
        contentArea.removeAll();

        List<ForumPostDTO> results = backendClient.searchPosts(keyword);

        H3 resultsHeading = new H3("Results for \"" + keyword + "\"");
        resultsHeading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");
        contentArea.add(resultsHeading);

        if (results.isEmpty()) {
            Paragraph empty = new Paragraph("No posts found matching \"" + keyword + "\".");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)");
            contentArea.add(empty);
            return;
        }

        for (ForumPostDTO post : results) {
            Div card = new Div();
            card.getStyle()
                    .set("background", "var(--lumo-base-color)")
                    .set("border", "1px solid var(--lumo-contrast-10pct)")
                    .set("border-radius", "var(--lumo-border-radius-l)")
                    .set("padding", "var(--lumo-space-m) var(--lumo-space-l)")
                    .set("margin-bottom", "var(--lumo-space-s)")
                    .set("cursor", "pointer");

            card.getElement().addEventListener("mouseover", e ->
                    card.getStyle().set("border-color", "var(--lumo-primary-color)"));
            card.getElement().addEventListener("mouseout", e ->
                    card.getStyle().set("border-color", "var(--lumo-contrast-10pct)"));

            H3 title = new H3(post.getTitle());
            title.getStyle().set("margin", "0 0 var(--lumo-space-xs) 0");

            Span meta = new Span(post.getCategoryDisplay() + "  ·  User " + post.getUserId());
            meta.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                    .set("color", "var(--lumo-tertiary-text-color)");

            Paragraph preview = new Paragraph(post.getContent());
            preview.getStyle().set("margin", "var(--lumo-space-xs) 0 0 0")
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");

            card.add(title, meta, preview);
            card.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate("forum/" + post.getCategoryKey())));
            contentArea.add(card);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String getCategoryIcon(String categoryKey) {
        return switch (categoryKey) {
            case "general" -> "💬";
            case "trending" -> "🔥";
            case "horror" -> "👻";
            case "comedy" -> "😂";
            case "action" -> "💥";
            case "drama" -> "🎭";
            case "sci-fi", "scifi" -> "🚀";
            case "romance" -> "❤️";
            case "animation" -> "🎨";
            case "documentary" -> "🎥";
            default -> "🎬";
        };
    }

    private static class CategoryInfo {
        String displayName;
        int count;
        CategoryInfo(String displayName, int count) {
            this.displayName = displayName;
            this.count = count;
        }
        void increment() { count++; }
    }
}