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

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewerView extends after_login {

    public ViewerView(Stage stage, Scene eventsScene) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1f1c2c, #928dab);");

        Label title = new Label("Viewer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #fff; -fx-font-weight: bold;");

        // Load events from DB

        // GridPane for event cards (3 per row)
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(15));
        grid.setAlignment(Pos.TOP_CENTER);

        Label loading = new Label("Loading events...");
        loading.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        grid.add(loading, 0, 0);

// Run DB query in background
        new Thread(() -> {
            List<EventController.EventData> events = EventController.loadEventsFromDB();

            // When done, update UI safely
            Platform.runLater(() -> {
                grid.getChildren().clear();

                int column = 0;
                int row = 0;

                for (EventController.EventData event : events) {
                    VBox card = createEventCard(event, DateTimeFormatter.ofPattern("dd MMM yyyy"));
                    grid.add(card, column, row);

                    column++;
                    if (column == 3) { column = 0; row++; }
                }
            });
        }).start();



        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-padding: 8 20;");
        backBtn.setOnAction(e -> stage.setScene(eventsScene));

        Button chatBtn = new Button("Chat with organizers");
        applyHoverEffect(chatBtn,          "#7E57C2", "#5E35B1");
        chatBtn.setOnAction(e -> {
            // Sub organizer is a client
            chatWindows.openClientChat("Viewer");
        });

        layout.getChildren().addAll(title, chatBtn, scrollPane, backBtn);

        Scene scene = new Scene(layout, 1200, 700);
        stage.setScene(scene);
        stage.setMaximized(true);
    }


    private VBox createEventCard(EventController.EventData event, DateTimeFormatter dtf) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(340);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10,0,0,5);");

        // --- Image or Color block ---
        StackPane visualSlot = new StackPane();
        visualSlot.setPrefSize(340, 150);
        visualSlot.setStyle("-fx-background-radius: 10;");

        if (event.eventImagePath != null && event.eventImagePath.exists()) {
            try {
                Image image = new Image(event.eventImagePath.toURI().toString(), 340, 150, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(340);
                imageView.setFitHeight(150);
                Rectangle clip = new Rectangle(340, 150);
                clip.setArcWidth(20);
                clip.setArcHeight(20);
                imageView.setClip(clip);
                visualSlot.getChildren().add(imageView);
            } catch (Exception e) {
                visualSlot.setStyle("-fx-background-color: " + event.color + "; -fx-background-radius: 10;");
            }
        } else {
            visualSlot.setStyle("-fx-background-color: " + event.color + "; -fx-background-radius: 10;");
        }

        Label name = new Label("📌 " + event.name);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label startDateTime = new Label("🟢 " + event.date.format(dtf) + " " + event.startTime);
        startDateTime.setStyle("-fx-text-fill: #EAEAEA; -fx-font-size: 13px;");
        Label endDateTime = new Label("🔴 " + event.endDate.format(dtf) + " " + event.endTime);
        endDateTime.setStyle("-fx-text-fill: #EAEAEA; -fx-font-size: 13px;");

        Label duration = new Label("⏳ " + event.durationDays + " day(s)");
        duration.setStyle("-fx-text-fill: #EAEAEA; -fx-font-size: 13px;");
        Label location = new Label("📍 " + event.location);
        location.setStyle("-fx-text-fill: #EAEAEA; -fx-font-size: 13px;");

       // Organizers (main + sub)
VBox organizersBox = new VBox(5);
Label orgTitle = new Label("👥 Organizers:");
orgTitle.setStyle("-fx-text-fill: #F1C40F; -fx-font-weight: bold; -fx-font-size: 13px;");
organizersBox.getChildren().add(orgTitle);

if (event.organizers != null && !event.organizers.isEmpty()) {
    // Main organizer = first one in list (added from session)
    EventController.Organizer mainOrg = event.organizers.get(0);
    Label mainOrgLabel = new Label("⭐ Main: " + mainOrg.name + " (ID: " + mainOrg.registrationCode + ")");
    mainOrgLabel.setStyle("-fx-text-fill: #00FFAA; -fx-font-size: 12.5px; -fx-font-weight: bold;");
    organizersBox.getChildren().add(mainOrgLabel);

    // Sub-organizers (if any)
    if (event.organizers.size() > 1) {
        Label subTitle = new Label("— Sub Organizers —");
        subTitle.setStyle("-fx-text-fill: #EAEAEA; -fx-font-size: 12px; -fx-font-style: italic;");
        organizersBox.getChildren().add(subTitle);

        for (int i = 1; i < event.organizers.size(); i++) {
            EventController.Organizer sub = event.organizers.get(i);
            Label subLabel = new Label("• " + sub.name + " (ID: " + sub.registrationCode + ")");
            subLabel.setStyle("-fx-text-fill: #CCCCCC; -fx-font-size: 12px;");
            organizersBox.getChildren().add(subLabel);
        }
    }
} else {
    Label noOrg = new Label("No organizers found");
    noOrg.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px; -fx-font-style: italic;");
    organizersBox.getChildren().add(noOrg);
}


        card.getChildren().addAll(visualSlot, name, startDateTime, endDateTime, duration, location, organizersBox);

        // --- Hover effect ---
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 15,0,0,8); -fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10,0,0,5); -fx-scale-x: 1; -fx-scale-y: 1;"));

        // --- Clickable action ---
        card.setOnMouseClicked(e -> {
            System.out.println("Clicked on event: " + event.name);
            // TODO: open event details window if needed
        });

        return card;
    }
}

