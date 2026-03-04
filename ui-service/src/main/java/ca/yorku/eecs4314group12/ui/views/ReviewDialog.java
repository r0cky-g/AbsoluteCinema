package ca.yorku.eecs4314group12.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import ca.yorku.eecs4314group12.ui.data.BackendClientService;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewDTO;

/**
 * Modal dialog for writing a review.
 *
 * Submits to review-service POST /api/reviews via BackendClientService.
 *
 * Usage:
 *   ReviewDialog dialog = new ReviewDialog(backendClient, movieId, movieTitle);
 *   dialog.setOnSuccess(() -> reloadReviews()); // optional callback to refresh page
 *   dialog.open();
 *
 * TODO: Replace hardcoded userId = 1L with the real logged-in user's ID once
 *       auth is wired up (user-service integration, stored in Spring Security principal).
 */
public class ReviewDialog extends Dialog {

    private static final long PLACEHOLDER_USER_ID = 1L;

    private final BackendClientService backendClient;
    private final long movieId;
    private Runnable onSuccess;

    // Form fields kept as instance vars so the submit handler can read them
    private final IntegerField ratingField;
    private final TextField titleField;
    private final TextArea contentField;
    private final Checkbox spoilerCheck;
    private final Span errorMsg;

    public ReviewDialog(BackendClientService backendClient, long movieId, String movieTitle) {
        this.backendClient = backendClient;
        this.movieId = movieId;

        setWidth("560px");
        setCloseOnOutsideClick(true);
        setCloseOnEsc(true);

        // ---- Header ----
        H2 heading = new H2("Write a Review");
        heading.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Reviewing: " + movieTitle);
        subtitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin", "0 0 var(--lumo-space-m) 0")
                .set("font-size", "var(--lumo-font-size-s)");

        // ---- Rating ----
        ratingField = new IntegerField("Rating");
        ratingField.setMin(1);
        ratingField.setMax(10);
        ratingField.setValue(7);
        ratingField.setStepButtonsVisible(true);
        ratingField.setHelperText("1 (worst) – 10 (best)");
        ratingField.setWidth("160px");
        ratingField.setRequired(true);

        // Live star preview next to rating
        Span starPreview = new Span(starsFor(7));
        starPreview.getStyle()
                .set("font-size", "1.4rem")
                .set("align-self", "center")
                .set("padding-top", "20px"); // align with field label offset
        ratingField.addValueChangeListener(e -> {
            int val = e.getValue() == null ? 0 : e.getValue();
            starPreview.setText(starsFor(val));
        });

        HorizontalLayout ratingRow = new HorizontalLayout(ratingField, starPreview);
        ratingRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        ratingRow.setSpacing(true);

        // ---- Title ----
        titleField = new TextField("Review Title");
        titleField.setPlaceholder("Summarise your thoughts in a line");
        titleField.setMaxLength(200);
        titleField.setWidth("100%");
        titleField.setRequired(true);

        // ---- Content ----
        contentField = new TextArea("Review");
        contentField.setPlaceholder("What did you think? (10–5000 characters)");
        contentField.setMinLength(10);
        contentField.setMaxLength(5000);
        contentField.setWidth("100%");
        contentField.setMinHeight("140px");
        contentField.setRequired(true);

        // ---- Spoiler ----
        spoilerCheck = new Checkbox("Contains spoilers");

        // ---- Error message (hidden until needed) ----
        errorMsg = new Span();
        errorMsg.getStyle()
                .set("color", "var(--lumo-error-color)")
                .set("font-size", "var(--lumo-font-size-s)");
        errorMsg.setVisible(false);

        // ---- Buttons ----
        Button submitBtn = new Button("Submit Review");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.addClickListener(e -> handleSubmit());

        Button cancelBtn = new Button("Cancel");
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelBtn.addClickListener(e -> close());

        HorizontalLayout buttons = new HorizontalLayout(submitBtn, cancelBtn);
        buttons.setSpacing(true);
        buttons.getStyle().set("margin-top", "var(--lumo-space-m)");

        // ---- Layout ----
        VerticalLayout content = new VerticalLayout(
                heading, subtitle, ratingRow, titleField, contentField, spoilerCheck, errorMsg, buttons);
        content.setPadding(true);
        content.setSpacing(false);
        content.getStyle().set("gap", "var(--lumo-space-m)");

        add(content);
    }

    /**
     * Optional callback invoked after a successful submission.
     * Use this to refresh the reviews list on the parent page.
     */
    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    // -------------------------------------------------------------------------
    // Submit handler
    // -------------------------------------------------------------------------

    private void handleSubmit() {
        errorMsg.setVisible(false);

        // Client-side validation
        String title = titleField.getValue().trim();
        String content = contentField.getValue().trim();
        Integer rating = ratingField.getValue();

        if (title.isBlank()) {
            showError("Please enter a review title.");
            return;
        }
        if (content.length() < 10) {
            showError("Review must be at least 10 characters.");
            return;
        }
        if (rating == null || rating < 1 || rating > 10) {
            showError("Rating must be between 1 and 10.");
            return;
        }

        // Build DTO
        ReviewDTO dto = new ReviewDTO();
        dto.setUserId(PLACEHOLDER_USER_ID); // TODO: replace with real user ID from auth principal
        dto.setMovieId(movieId);
        dto.setRating(rating);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setIsSpoiler(spoilerCheck.getValue());

        // Submit
        boolean success = backendClient.createReview(dto);

        if (success) {
            close();
            Notification n = Notification.show("Review submitted!", 3000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if (onSuccess != null) onSuccess.run();
        } else {
            // BackendClientService returns false on conflict (duplicate) or server error
            showError("Submission failed. You may have already reviewed this film, or the review service is unavailable.");
        }
    }

    private void showError(String message) {
        errorMsg.setText(message);
        errorMsg.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Star string helper
    // -------------------------------------------------------------------------

    private String starsFor(int rating) {
        // Map 1–10 to 1–5 half-stars displayed as full stars for simplicity
        int stars = (int) Math.round(rating / 2.0);
        return "★".repeat(Math.max(0, Math.min(stars, 5)))
             + "☆".repeat(Math.max(0, 5 - Math.min(stars, 5)));
    }
}