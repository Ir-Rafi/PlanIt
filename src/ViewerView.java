import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;



public class ViewerView extends after_login {

    public ViewerView(Stage stage, Scene eventsScene) {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30));
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b);");

        // Title
        Label title = new Label("Viewer Dashboard");
        title.setStyle("-fx-font-size: 36px; -fx-text-fill: #a78bfa; -fx-font-weight: bold;");

        // Chat button
        Button chatBtn = new Button("💬  Chat with organizers");
        chatBtn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #22d3ee; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 25; " +
                "-fx-border-color: #334155; -fx-border-radius: 25; -fx-padding: 10 25; -fx-cursor: hand;");
        chatBtn.setOnMouseEntered(e -> chatBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: #22d3ee; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 25; " +
                "-fx-border-color: #22d3ee; -fx-border-radius: 25; -fx-padding: 10 25; -fx-cursor: hand;"));
        chatBtn.setOnMouseExited(e -> chatBtn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #22d3ee; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 25; " +
                "-fx-border-color: #334155; -fx-border-radius: 25; -fx-padding: 10 25; -fx-cursor: hand;"));
        chatBtn.setOnAction(e -> chatWindows.openClientChat("Viewer"));

        // GridPane for event cards (3 per row)
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.TOP_CENTER);

        Label loading = new Label("Loading events...");
        loading.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");
        grid.add(loading, 0, 0);

        // Run DB query in background
        new Thread(() -> {
            List<EventController.EventData> events = EventController.loadEventsFromDB();

            Platform.runLater(() -> {
                grid.getChildren().clear();

                int column = 0;
                int row = 0;

                for (EventController.EventData event : events) {
                    VBox card = createEventCard(event, DateTimeFormatter.ofPattern("dd MMM yyyy"));
                    grid.add(card, column, row);

                    column++;
                    if (column == 3) {
                        column = 0;
                        row++;
                    }
                }
            });
        }).start();

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Back button
        Button backBtn = new Button("← Back to Home");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; " +
                "-fx-font-size: 14px; -fx-cursor: hand;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #e2e8f0; -fx-font-size: 14px; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-cursor: hand;"));

        backBtn.setOnAction(e -> {
    System.out.println("[ViewerView] Back clicked");
    System.out.println("[ViewerView] eventsScene=" + eventsScene + " id=" + System.identityHashCode(eventsScene));
    System.out.println("[ViewerView] BEFORE setScene: isFull=" + stage.isFullScreen()
            + ", isMax=" + stage.isMaximized()
            + ", stageSize=" + stage.getWidth() + "x" + stage.getHeight()
            + ", rootPref=" + eventsScene.getRoot().prefWidth(-1) + "x" + eventsScene.getRoot().prefHeight(-1));

    // Switch scene immediately
    stage.setScene(eventsScene);

    // Allow the scene to be applied/layouted on the FX thread, then adjust window
    Platform.runLater(() -> {
        System.out.println("[ViewerView] AFTER setScene (tick1): isFull=" + stage.isFullScreen()
                + ", isMax=" + stage.isMaximized()
                + ", stageSize=" + stage.getWidth() + "x" + stage.getHeight());

        // request layout and focus to help sizing
        eventsScene.getRoot().requestLayout();
        eventsScene.getRoot().requestFocus();

        // Next tick: maximize + fullscreen
        Platform.runLater(() -> {
            System.out.println("[ViewerView] BEFORE maximize/fullscreen (tick2): isFull=" + stage.isFullScreen()
                    + ", isMax=" + stage.isMaximized()
                    + ", stageSize=" + stage.getWidth() + "x" + stage.getHeight());
            try {
                stage.setMaximized(true);   // make window full-size
                stage.setFullScreen(true);  // if you want real fullscreen
                stage.setFullScreenExitHint("");
            } catch (Exception ex) {
                System.out.println("[ViewerView] Exception while maximizing/fullscreen: " + ex);
                try { stage.setMaximized(true); } catch (Exception ignored) {}
            }
            System.out.println("[ViewerView] AFTER maximize/fullscreen: isFull=" + stage.isFullScreen()
                    + ", isMax=" + stage.isMaximized()
                    + ", stageSize=" + stage.getWidth() + "x" + stage.getHeight());
        });
    });
});

        layout.getChildren().addAll(title, chatBtn, scrollPane, backBtn);

        Scene scene = new Scene(layout);
        stage.setScene(scene);

        // Ensure the viewer itself opens fullscreen reliably
        Platform.runLater(() -> {
            try {
                stage.setMaximized(true);
                stage.setFullScreen(true);
                stage.setFullScreenExitHint("");
            } catch (Exception ignored) {
                try { stage.setMaximized(true); } catch (Exception ignored2) {}
            }
        });
    }

    private VBox createEventCard(EventController.EventData event, DateTimeFormatter dtf) {
        VBox card = new VBox(0);
        card.setPrefWidth(380);
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16; " +
                "-fx-border-color: #334155; -fx-border-radius: 16; -fx-border-width: 1;");

        // --- Image/Color Header Section ---
        StackPane visualSlot = new StackPane();
        visualSlot.setPrefSize(380, 140);
        visualSlot.setAlignment(Pos.BOTTOM_CENTER);

        // Default gradient colors based on event color or custom
        String headerColor = event.color != null ? event.color : "#8b5cf6";
        String headerStyle = "-fx-background-color: linear-gradient(to bottom right, " + headerColor + ", " + adjustColor(headerColor) + "); " +
                "-fx-background-radius: 16 16 0 0;";

        if (event.eventImagePath != null && event.eventImagePath.exists()) {
            try {
                Image image = new Image(event.eventImagePath.toURI().toString(), 380, 140, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(380);
                imageView.setFitHeight(140);
                Rectangle clip = new Rectangle(380, 140);
                clip.setArcWidth(32);
                clip.setArcHeight(32);
                imageView.setClip(clip);
                visualSlot.getChildren().add(imageView);
            } catch (Exception ex) {
                Region colorBlock = new Region();
                colorBlock.setPrefSize(380, 140);
                colorBlock.setStyle(headerStyle);
                visualSlot.getChildren().add(colorBlock);
            }
        } else {
            Region colorBlock = new Region();
            colorBlock.setPrefSize(380, 140);
            colorBlock.setStyle(headerStyle);
            visualSlot.getChildren().add(colorBlock);
        }

        // Event name badge
        Label nameBadge = new Label("📌 " + event.name);
        nameBadge.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20;");
        StackPane.setAlignment(nameBadge, Pos.BOTTOM_CENTER);
        StackPane.setMargin(nameBadge, new Insets(0, 0, -15, 0));
        visualSlot.getChildren().add(nameBadge);

        // --- Content Section ---
        VBox content = new VBox(10);
        content.setPadding(new Insets(25, 20, 15, 20));
        content.setAlignment(Pos.TOP_LEFT);

        // Parse times
        String startTimeStr = event.startTime;
        String endTimeStr = event.endTime;
        if (startTimeStr.length() == 8) startTimeStr = startTimeStr.substring(0, 5);
        if (endTimeStr.length() == 8) endTimeStr = endTimeStr.substring(0, 5);

        LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime eventStartTime = event.date.atTime(startTime);
        LocalDateTime eventEndTime = event.endDate.atTime(endTime);

        // Start date/time
        HBox startBox = new HBox(8);
        startBox.setAlignment(Pos.CENTER_LEFT);
        Label startDot = new Label("●");
        startDot.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 10px;");
        Label startLabel = new Label(event.date.format(dtf) + " " + event.startTime);
        startLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
        startBox.getChildren().addAll(startDot, startLabel);

        // End date/time
        HBox endBox = new HBox(8);
        endBox.setAlignment(Pos.CENTER_LEFT);
        Label endDot = new Label("●");
        endDot.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 10px;");
        Label endLabel = new Label(event.endDate.format(dtf) + " " + event.endTime);
        endLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
        endBox.getChildren().addAll(endDot, endLabel);

        // Duration and Location row
        HBox infoRow = new HBox();
        infoRow.setAlignment(Pos.CENTER_LEFT);

        Label durationLabel = new Label("⏱ " + event.durationDays + " day(s)");
        durationLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label locationLabel = new Label("📍 " + event.location);
        locationLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        infoRow.getChildren().addAll(durationLabel, spacer, locationLabel);

        // Status badge
        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(5, 0, 5, 0));

        Label statusLabel = new Label();
        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.isBefore(eventStartTime)) {
            statusLabel.setText("● Upcoming");
            statusLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 12px; -fx-font-weight: bold; " +
                    "-fx-background-color: rgba(34, 197, 94, 0.15); -fx-padding: 5 15; -fx-background-radius: 15; " +
                    "-fx-border-color: #22c55e; -fx-border-radius: 15;");
        } else if (currentTime.isAfter(eventEndTime)) {
            statusLabel.setText("✕ Ended");
            statusLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 12px; -fx-font-weight: bold; " +
                    "-fx-background-color: rgba(248, 113, 113, 0.15); -fx-padding: 5 15; -fx-background-radius: 15; " +
                    "-fx-border-color: #f87171; -fx-border-radius: 15;");
        } else {
            statusLabel.setText("● Ongoing");
            statusLabel.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 12px; -fx-font-weight: bold; " +
                    "-fx-background-color: rgba(59, 130, 246, 0.15); -fx-padding: 5 15; -fx-background-radius: 15; " +
                    "-fx-border-color: #3b82f6; -fx-border-radius: 15;");
        }
        statusBox.getChildren().add(statusLabel);

        // Organizers section
        VBox organizersBox = new VBox(5);
        organizersBox.setPadding(new Insets(10));
        organizersBox.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 10;");

        Label orgTitle = new Label("👥 Organizers:");
        orgTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        organizersBox.getChildren().add(orgTitle);

        if (event.organizers != null && !event.organizers.isEmpty()) {
            EventController.Organizer mainOrg = event.organizers.get(0);
            Label mainOrgLabel = new Label("Main: " + mainOrg.name + " (ID: " + mainOrg.registrationCode + ")");
            mainOrgLabel.setStyle("-fx-text-fill: #22d3ee; -fx-font-size: 11px;");
            organizersBox.getChildren().add(mainOrgLabel);

            if (event.organizers.size() > 1) {
                for (int i = 1; i < event.organizers.size(); i++) {
                    EventController.Organizer sub = event.organizers.get(i);
                    Label subLabel = new Label("• " + sub.name + " (ID: " + sub.registrationCode + ")");
                    subLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                    organizersBox.getChildren().add(subLabel);
                }
            }
        } else {
            Label noOrg = new Label("No organizers found");
            noOrg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
            organizersBox.getChildren().add(noOrg);
        }

        content.getChildren().addAll(startBox, endBox, infoRow, statusBox, organizersBox);
        card.getChildren().addAll(visualSlot, content);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16; " +
                "-fx-border-color: #6366f1; -fx-border-radius: 16; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(99, 102, 241, 0.4), 20, 0, 0, 5);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16; " +
                "-fx-border-color: #334155; -fx-border-radius: 16; -fx-border-width: 1;"));

        card.setOnMouseClicked(e -> System.out.println("Clicked on event: " + event.name));

        return card;
    }

    private String adjustColor(String hexColor) {
        // Returns a slightly different shade for gradient
        try {
            Color color = Color.web(hexColor);
            Color darker = color.deriveColor(0, 1.0, 0.7, 1.0);
            return String.format("#%02x%02x%02x",
                    (int) (darker.getRed() * 255),
                    (int) (darker.getGreen() * 255),
                    (int) (darker.getBlue() * 255));
        } catch (Exception e) {
            return "#6366f1";
        }
    }
}