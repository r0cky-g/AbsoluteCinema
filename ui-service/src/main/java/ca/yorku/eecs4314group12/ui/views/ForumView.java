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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Forum page — lists all posts, lets users create posts and add comments.
 *
 * Uses real userId from UserSessionService when available.
 * Ownership check: Edit/Delete only shown to the post's owner (by userId).
 */
@Route(value = "forum", layout = MainLayout.class)
@PageTitle("Forum | Absolute Cinema")
@AnonymousAllowed
public class ForumView extends VerticalLayout {

    private static final long FALLBACK_USER_ID = 1L;
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private final BackendClientService backendClient;
    private final UserSessionService userSessionService;
    private final VerticalLayout postsContainer;

    public ForumView(BackendClientService backendClient, UserSessionService userSessionService) {
        this.backendClient = backendClient;
        this.userSessionService = userSessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("max-width", "860px").set("margin", "0 auto");

        H2 heading = new H2("Forum");
        heading.getStyle().set("margin", "var(--lumo-space-m) 0 var(--lumo-space-s) 0");

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

        postsContainer = new VerticalLayout();
        postsContainer.setPadding(false); postsContainer.setSpacing(true); postsContainer.setWidthFull();

        add(headerRow, postsContainer);
        loadPosts();
    }

    // -------------------------------------------------------------------------
    // Load & render posts
    // -------------------------------------------------------------------------

    private void loadPosts() {
        postsContainer.removeAll();
        List<ForumPostDTO> posts = backendClient.getAllPosts();

        if (posts.isEmpty()) {
            Paragraph empty = new Paragraph("No posts yet. Be the first to start a discussion!");
            empty.getStyle().set("color", "var(--lumo-secondary-text-color)")
                    .set("padding", "var(--lumo-space-l)");
            postsContainer.add(empty);
            return;
        }

        for (ForumPostDTO post : posts) {
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

        String authorLabel = post.getUserId() != null ? "User " + post.getUserId() : "Unknown";
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

            Button deleteBtn = new Button("Delete");
            deleteBtn.addThemeVariants(
                    ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> deletePost(post.getId()));

            actions.add(editBtn, deleteBtn);
        }

        card.add(title, authorSpan, content, commentsSection, actions);
        return card;
    }

    // -------------------------------------------------------------------------
    // Load comments
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
            Span meta = new Span("User " + comment.getUserId() + " · " + dateStr);
            meta.getStyle().set("font-size", "var(--lumo-font-size-xs)")
                    .set("color", "var(--lumo-tertiary-text-color)");
            bubble.add(text, meta);
            target.add(bubble);
        }
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
        contentField.setWidthFull(); contentField.setRequired(true); contentField.setMinHeight("120px");

        Span errorMsg = new Span();
        errorMsg.getStyle().set("color", "var(--lumo-error-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Button submitBtn = new Button("Post");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.addClickListener(e -> {
            String t = titleField.getValue().trim();
            String c = contentField.getValue().trim();
            if (t.isEmpty() || c.isEmpty()) { errorMsg.setText("Title and content are required."); return; }
            boolean ok = backendClient.createPost(t, c, currentUserId()).isPresent();
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

        VerticalLayout layout = new VerticalLayout(heading, titleField, contentField, errorMsg,
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
            if (t.isEmpty() || c.isEmpty()) { errorMsg.setText("Title and content are required."); return; }
            boolean ok = backendClient.updatePost(post.getId(), t, c).isPresent();
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

        VerticalLayout layout = new VerticalLayout(heading, titleField, contentField, errorMsg,
                new HorizontalLayout(submitBtn, cancelBtn));
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
        contentField.setWidthFull(); contentField.setRequired(true); contentField.setMinHeight("100px");

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
}