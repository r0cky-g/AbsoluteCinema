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
import ca.yorku.eecs4314group12.ui.security.UserSessionService;

/**
 * Modal dialog for writing a review.
 */
public class ReviewDialog extends Dialog {

    private static final long FALLBACK_USER_ID = 1L;

    private final BackendClientService backendClient;
    private final UserSessionService userSessionService;
    private final long movieId;
    private Runnable onSuccess;

    private final IntegerField ratingField;
    private final TextField titleField;
    private final TextArea contentField;
    private final Checkbox spoilerCheck;
    private final Span errorMsg;

    public ReviewDialog(BackendClientService backendClient, UserSessionService userSessionService,
                        long movieId, String movieTitle) {
        this.backendClient = backendClient;
        this.userSessionService = userSessionService;
        this.movieId = movieId;

        setWidth("520px");
        setMaxWidth("95vw");
        setCloseOnOutsideClick(true);
        setCloseOnEsc(true);

        H2 heading = new H2("Write a Review");
        heading.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Reviewing: " + movieTitle);
        subtitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin", "0 0 var(--lumo-space-m) 0")
                .set("font-size", "var(--lumo-font-size-s)");

        ratingField = new IntegerField("Rating");
        ratingField.setMin(1); ratingField.setMax(10); ratingField.setValue(7);
        ratingField.setStepButtonsVisible(true);
        ratingField.setHelperText("1 (worst) – 10 (best)");
        ratingField.setWidth("160px"); ratingField.setRequired(true);

        Span starPreview = new Span(starsFor(7));
        starPreview.getStyle().set("font-size", "1.4rem").set("align-self", "center")
                .set("padding-top", "20px");
        ratingField.addValueChangeListener(e -> {
            int val = e.getValue() == null ? 1 : e.getValue();
            starPreview.setText(starsFor(val));
        });

        HorizontalLayout ratingRow = new HorizontalLayout(ratingField, starPreview);
        ratingRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        ratingRow.setWidthFull();

        titleField = new TextField("Review Title");
        titleField.setWidthFull(); titleField.setRequired(true); titleField.setMaxLength(200);

        contentField = new TextArea("Your Review");
        contentField.setWidthFull(); contentField.setRequired(true);
        contentField.setMinHeight("120px");
        contentField.setMaxHeight("240px");
        contentField.setHelperText("10–5000 characters");

        spoilerCheck = new Checkbox("Contains spoilers");

        errorMsg = new Span();
        errorMsg.getStyle().set("color", "var(--lumo-error-color)")
                .set("font-size", "var(--lumo-font-size-s)");
        errorMsg.setVisible(false);

        Button submitBtn = new Button("Submit Review");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.addClickListener(e -> submitReview());

        Button cancelBtn = new Button("Cancel", e -> close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(submitBtn, cancelBtn);
        buttons.setWidthFull();

        VerticalLayout layout = new VerticalLayout(
                heading, subtitle, ratingRow, titleField, contentField,
                spoilerCheck, errorMsg, buttons);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.getStyle().set("box-sizing", "border-box");

        add(layout);
    }

    public ReviewDialog(BackendClientService backendClient, long movieId, String movieTitle) {
        this(backendClient, null, movieId, movieTitle);
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    private void submitReview() {
        String title = titleField.getValue().trim();
        String content = contentField.getValue().trim();
        Integer rating = ratingField.getValue();

        if (title.isBlank()) { showError("Review title is required."); return; }
        if (content.length() < 10) { showError("Review must be at least 10 characters."); return; }
        if (rating == null || rating < 1 || rating > 10) {
            showError("Rating must be between 1 and 10."); return;
        }

        long userId = (userSessionService != null && userSessionService.getUserId() != null)
                ? userSessionService.getUserId() : FALLBACK_USER_ID;

        ReviewDTO dto = new ReviewDTO();
        dto.setUserId(userId);
        dto.setMovieId(movieId);
        dto.setRating(rating);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setIsSpoiler(spoilerCheck.getValue());

        boolean success = backendClient.createReview(dto);
        if (success) {
            close();
            Notification n = Notification.show("Review submitted!", 3000,
                    Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if (onSuccess != null) onSuccess.run();
        } else {
            showError("Submission failed. You may have already reviewed this film.");
        }
    }

    private void showError(String message) {
        errorMsg.setText(message);
        errorMsg.setVisible(true);
    }

    private String starsFor(int rating) {
        int stars = (int) Math.round(rating / 2.0);
        return "★".repeat(Math.max(0, Math.min(stars, 5)))
             + "☆".repeat(Math.max(0, 5 - Math.min(stars, 5)));
    }
}