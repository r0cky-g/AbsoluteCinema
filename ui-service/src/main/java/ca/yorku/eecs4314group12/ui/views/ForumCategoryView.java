package ca.yorku.eecs4314group12.ui.views;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.ForumCommentDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ForumPostDTO;
import ca.yorku.eecs4314group12.ui.security.UserSessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Forum category view — shows all posts in a given category.
 * Route: /forum/{category}
 *
 * Category matching is case-insensitive and space-trimmed server-side.
 * The category key in the URL is the normalised (lowercased, trimmed) form.
 */
@Route(value = "forum/:category", layout = MainLayout.class)
@PageTitle("Forum | Absolute Cinema")
@AnonymousAllowed
public class ForumCategoryView extends VerticalLayout implements BeforeEnterObserver {

    private static final long FALLBACK_USER_ID = 1L;
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    // Predefined category options shown in the "New Post" dialog
    private static final List<String> PRESET_CATEGORIES = List.of(
            "General", "Trending", "Horror", "Comedy", "Action",
            "Drama", "Sci-Fi", "Romance", "Animation", "Documentary"
    );

    private final BackendClientService backendClient;
    private final UserSessionService userSessionService;
    private final VerticalLayout postsContainer;

    private String categoryKey;    // normalised (lowercase, trimmed)
    private String categoryDisplay; // display name

    public ForumCategoryView(BackendClientService backendClient,
                              UserSessionService userSessionService) {
        this.backendClient = backendClient;
        this.userSessionService = userSessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "860px").set("margin", "0 auto");

        postsContainer = new VerticalLayout();
        postsContainer.setPadding(false); postsContainer.setSpacing(true);
        postsContainer.setWidthFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        categoryKey = event.getRouteParameters().get("category").orElse("general")
                .trim().toLowerCase();
        // Title-case display: "general" → "General", "sci-fi" → "Sci-fi"
        categoryDisplay = categoryKey.substring(0, 1).toUpperCase()
                + categoryKey.substring(1);

        // Back link
        Button backBtn = new Button("All Categories", VaadinIcon.ARROW_LEFT.create());
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ForumView.class)));

        H2 heading = new H2(categoryDisplay);
        heading.getStyle().set("margin", "var(--lumo-space-s) 0 var(--lumo-space-s) 0");

        Button newPostBtn = new Button("+ New Post");
        newPostBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newPostBtn.addClickListener(e -> {
            if (!isLoggedIn()) {
                Notification.show("You must be logged in to create a post.", 3000,
                        Notification.Position.MIDDLE);
                return;
            }
            openNewPostDialog();
        });

        HorizontalLayout headerRow = new HorizontalLayout(heading, newPostBtn);
        headerRow.setWidthFull();
        headerRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        headerRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Search bar — searches by title, filtered to this category
        com.vaadin.flow.component.textfield.TextField searchField =
                new com.vaadin.flow.component.textfield.TextField();
        searchField.setPlaceholder("Search posts in " + categoryDisplay + "…");
        searchField.setPrefixComponent(com.vaadin.flow.component.icon.VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("100%");

        Runnable doSearch = () -> {
            String q = searchField.getValue();
            if (q == null || q.isBlank()) {
                loadPosts();
            } else {
                loadSearchResults(q.trim());
            }
        };

        searchField.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> doSearch.run());
        searchField.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().isBlank()) loadPosts();
        });

        add(backBtn, headerRow, searchField, postsContainer);
        loadPosts();
    }

    // -------------------------------------------------------------------------
    // Load posts
    // -------------------------------------------------------------------------

    private void loadPosts() {
        postsContainer.removeAll();
        List<ForumPostDTO> posts = new java.util.ArrayList<>(
                backendClient.getPostsByCategory(categoryKey));

        // For "general", also include posts with null/blank category
        if (categoryKey.equals("general")) {
            List<ForumPostDTO> allPosts = backendClient.getAllPosts();
            java.util.Set<Long> existingIds = posts.stream()
                    .map(ForumPostDTO::getId)
                    .collect(java.util.stream.Collectors.toSet());
            allPosts.stream()
                    .filter(p -> (p.getCategory() == null || p.getCategory().isBlank())
                            && !existingIds.contains(p.getId()))
                    .forEach(posts::add);
        }

        if (posts.isEmpty()) {
            Paragraph empty = new Paragraph(
                    "No posts in " + categoryDisplay + " yet. Be the first to start a discussion!");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)")
                    .set("padding", "var(--lumo-space-l)");
            postsContainer.add(empty);
            return;
        }

        for (ForumPostDTO post : posts) {
            postsContainer.add(buildPostCard(post));
        }
    }

    private void loadSearchResults(String keyword) {
        postsContainer.removeAll();
        // Search globally then filter to this category
        List<ForumPostDTO> results = backendClient.searchPosts(keyword).stream()
                .filter(p -> p.getCategoryKey().equals(categoryKey))
                .toList();

        if (results.isEmpty()) {
            Paragraph empty = new Paragraph(
                    "No posts found matching \"" + keyword + "\" in " + categoryDisplay + ".");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)")
                    .set("padding", "var(--lumo-space-l)");
            postsContainer.add(empty);
            return;
        }

        for (ForumPostDTO post : results) {
            postsContainer.add(buildPostCard(post));
        }
    }

    private Div buildPostCard(ForumPostDTO post) {
        Long currentUserId = currentUserId();
        boolean isOwner = currentUserId != null
                && post.getUserId() != null
                && post.getUserId().equals(currentUserId);

        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m) var(--lumo-space-l)")
                .set("width", "100%");

        H3 title = new H3(post.getTitle());
        title.getStyle().set("margin", "0 0 var(--lumo-space-xs) 0");

        String authorLabel = post.getUserId() != null 
                ? getUsernameById(post.getUserId()) 
                : "Unknown";
        Span authorSpan = new Span("Posted by " + authorLabel);
        authorSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-tertiary-text-color)")
                .set("display", "block").set("margin-bottom", "var(--lumo-space-s)");

        Paragraph content = new Paragraph(post.getContent());
        content.getStyle().set("margin", "0 0 var(--lumo-space-m) 0")
                .set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout commentsLayout = new VerticalLayout();
        commentsLayout.setPadding(false); commentsLayout.setSpacing(true);

        Details commentsSection = new Details("Comments", commentsLayout);
        commentsSection.getStyle().set("width", "100%");
        commentsSection.addOpenedChangeListener(e -> {
            if (e.isOpened()) loadComments(post.getId(), commentsLayout);
        });

        Button commentBtn = new Button("Add Comment");
        commentBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        commentBtn.addClickListener(e -> {
            if (!isLoggedIn()) {
                Notification.show("You must be logged in to comment.", 3000,
                        Notification.Position.MIDDLE);
                return;
            }
            openCommentDialog(post.getId(), commentsLayout, commentsSection);
        });

        HorizontalLayout actions = new HorizontalLayout(commentBtn);
        actions.setSpacing(true);

        if (isOwner) {
            Button editBtn = new Button("Edit");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> openEditPostDialog(post));
            actions.add(editBtn);
        }

        if (isOwner || canModerateForum()) {
            Button deleteBtn = new Button("Delete");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL,
                    ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> deletePost(post.getId()));
            actions.add(deleteBtn);
        }

        card.add(title, authorSpan, content, commentsSection, actions);
        return card;
    }

    // -------------------------------------------------------------------------
    // Comments
    // -------------------------------------------------------------------------

    private void loadComments(long postId, VerticalLayout target) {
        target.removeAll();
        List<ForumCommentDTO> comments = backendClient.getCommentsForPost(postId);

        if (comments.isEmpty()) {
            Paragraph empty = new Paragraph("No comments yet.");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            target.add(empty);
            return;
        }

        Long actingUserId = currentUserId();
        String actingRole = userSessionService.getRole();

        for (ForumCommentDTO comment : comments) {
            Div bubble = new Div();
            bubble.getStyle()
                    .set("background", "var(--lumo-contrast-5pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s) var(--lumo-space-m)");
            Paragraph text = new Paragraph(comment.getContent());
            text.getStyle().set("margin", "0 0 var(--lumo-space-xs) 0");
            String dateStr = comment.getCreatedAt() != null
                    ? comment.getCreatedAt().format(DATE_FMT) : "";
            String username = comment.getUserId() != null 
                    ? getUsernameById(comment.getUserId()) 
                    : "Unknown";
            Span meta = new Span(username + " · " + dateStr);
            meta.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                    .set("color", "var(--lumo-tertiary-text-color)");

            HorizontalLayout row = new HorizontalLayout(meta);
            row.setWidthFull();
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            boolean commentOwner = actingUserId != null && comment.getUserId() != null
                    && comment.getUserId().equals(actingUserId);
            if (comment.getId() != null && actingUserId != null
                    && (commentOwner || canModerateForum())) {
                Button delComment = new Button("Delete", VaadinIcon.TRASH.create());
                delComment.addThemeVariants(ButtonVariant.LUMO_SMALL,
                        ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
                long cid = comment.getId();
                delComment.addClickListener(e -> {
                    boolean ok = backendClient.deleteForumComment(cid,
                            actingUserId != null ? actingUserId : FALLBACK_USER_ID, actingRole);
                    if (ok) {
                        loadComments(postId, target);
                        Notification.show("Comment deleted.", 2500, Notification.Position.BOTTOM_START);
                    } else {
                        Notification.show("Could not delete comment.", 3000, Notification.Position.BOTTOM_START)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                });
                row.add(delComment);
            }

            bubble.add(text, row);
            target.add(bubble);
        }
    }

    private boolean canModerateForum() {
        String r = userSessionService.getRole();
        return "MODERATOR".equals(r) || "ADMIN".equals(r);
    }

    // -------------------------------------------------------------------------
    // Dialogs
    // -------------------------------------------------------------------------

    private void openNewPostDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("520px");
        dialog.setCloseOnOutsideClick(true); dialog.setCloseOnEsc(true);

        H2 heading = new H2("New Post");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");

        TextField titleField = new TextField("Title");
        titleField.setWidthFull(); titleField.setRequired(true); titleField.setMaxLength(120);

        TextArea contentField = new TextArea("Content");
        contentField.setWidthFull(); contentField.setRequired(true);
        contentField.setMinHeight("120px");

        // Category selector — pre-selected to current category
        Select<String> categorySelect = new Select<>();
        categorySelect.setLabel("Category");
        categorySelect.setItems(PRESET_CATEGORIES);
        categorySelect.setWidthFull();
        // Pre-select current category if it matches a preset
        PRESET_CATEGORIES.stream()
                .filter(p -> p.trim().toLowerCase().equals(categoryKey))
                .findFirst()
                .ifPresentOrElse(categorySelect::setValue,
                        () -> categorySelect.setValue("General"));

        TextField customCategory = new TextField("Custom category (optional)");
        customCategory.setWidthFull();
        customCategory.setPlaceholder("Leave blank to use selection above");

        Span errorMsg = new Span();
        errorMsg.getStyle().set("color", "var(--lumo-error-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Button submitBtn = new Button("Post");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.addClickListener(e -> {
            String t = titleField.getValue().trim();
            String c = contentField.getValue().trim();
            if (t.isEmpty() || c.isEmpty()) {
                errorMsg.setText("Title and content are required."); return;
            }
            String customCat = customCategory.getValue();
            String cat;
            if (customCat != null && !customCat.isBlank()) {
                cat = customCat.trim();
            } else {
                String selected = categorySelect.getValue();
                cat = (selected != null && !selected.isBlank()) ? selected : categoryKey;
            }
            boolean ok = backendClient.createPost(t, c, currentUserId(), cat).isPresent();
            if (ok) {
                dialog.close(); loadPosts();
                Notification.show("Post created!", 3000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                errorMsg.setText("Failed to create post. Try again.");
            }
        });

        Button cancelBtn = new Button("Cancel", ev -> dialog.close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout layout = new VerticalLayout(heading, titleField, contentField,
                categorySelect, customCategory, errorMsg,
                new HorizontalLayout(submitBtn, cancelBtn));
        layout.setPadding(false); layout.setSpacing(true);
        dialog.add(layout);
        dialog.open();
    }

    private void openEditPostDialog(ForumPostDTO post) {
        Dialog dialog = new Dialog();
        dialog.setWidth("520px");
        dialog.setCloseOnOutsideClick(true); dialog.setCloseOnEsc(true);

        H2 heading = new H2("Edit Post");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");

        TextField titleField = new TextField("Title");
        titleField.setWidthFull(); titleField.setValue(post.getTitle());
        titleField.setRequired(true); titleField.setMaxLength(120);

        TextArea contentField = new TextArea("Content");
        contentField.setWidthFull(); contentField.setValue(post.getContent());
        contentField.setRequired(true); contentField.setMinHeight("120px");

        Span errorMsg = new Span();
        errorMsg.getStyle().set("color", "var(--lumo-error-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Button submitBtn = new Button("Save");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.addClickListener(e -> {
            String t = titleField.getValue().trim();
            String c = contentField.getValue().trim();
            if (t.isEmpty() || c.isEmpty()) {
                errorMsg.setText("Title and content are required."); return;
            }
            Long userId = currentUserId();
            String role = userSessionService.getRole();
            if (userId == null) {
                errorMsg.setText("You must be logged in to edit posts."); return;
            }
            boolean ok = backendClient.updatePost(post.getId(), t, c, userId, role).isPresent();
            if (ok) {
                dialog.close(); loadPosts();
                Notification.show("Post updated!", 3000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                errorMsg.setText("Failed to update post. Try again.");
            }
        });

        Button cancelBtn = new Button("Cancel", ev -> dialog.close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout layout = new VerticalLayout(heading, titleField, contentField,
                errorMsg, new HorizontalLayout(submitBtn, cancelBtn));
        layout.setPadding(false); layout.setSpacing(true);
        dialog.add(layout);
        dialog.open();
    }

    private void openCommentDialog(long postId, VerticalLayout commentsLayout,
                                    Details commentsSection) {
        Dialog dialog = new Dialog();
        dialog.setWidth("480px");
        dialog.setCloseOnOutsideClick(true); dialog.setCloseOnEsc(true);

        H2 heading = new H2("Add Comment");
        heading.getStyle().set("margin", "0 0 var(--lumo-space-m) 0");

        TextArea contentField = new TextArea("Comment");
        contentField.setWidthFull(); contentField.setRequired(true);
        contentField.setMinHeight("100px");

        Span errorMsg = new Span();
        errorMsg.getStyle().set("color", "var(--lumo-error-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Button submitBtn = new Button("Submit");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.addClickListener(e -> {
            String c = contentField.getValue().trim();
            if (c.isEmpty()) { errorMsg.setText("Comment cannot be empty."); return; }
            long userId = currentUserId() != null ? currentUserId() : FALLBACK_USER_ID;
            boolean ok = backendClient.createComment(postId, userId, c).isPresent();
            if (ok) {
                dialog.close();
                if (commentsSection.isOpened()) loadComments(postId, commentsLayout);
                else commentsSection.setOpened(true);
                Notification.show("Comment added!", 3000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                errorMsg.setText("Failed to add comment. Try again.");
            }
        });

        Button cancelBtn = new Button("Cancel", ev -> dialog.close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout layout = new VerticalLayout(heading, contentField, errorMsg,
                new HorizontalLayout(submitBtn, cancelBtn));
        layout.setPadding(false); layout.setSpacing(true);
        dialog.add(layout);
        dialog.open();
    }

    // -------------------------------------------------------------------------
    // Delete post
    // -------------------------------------------------------------------------

    private void deletePost(long postId) {
        Long userId = currentUserId();
        String role = userSessionService.getRole();
        boolean ok = backendClient.deletePost(postId,
                userId != null ? userId : FALLBACK_USER_ID, role);
        if (ok) {
            loadPosts();
            Notification.show("Post deleted.", 3000, Notification.Position.BOTTOM_START);
        } else {
            Notification.show("Could not delete post.", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // -------------------------------------------------------------------------
    // Auth helpers
    // -------------------------------------------------------------------------

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");
    }

    private Long currentUserId() {
        return userSessionService.getUserId();
    }

    private String getUsernameById(Long userId) {
        if (userId == null) return "Unknown";
        return backendClient.getUserData(userId)
                .map(user -> user.getUsername())
                .orElse("User " + userId);
    }
}