import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ViewerView {

    public ViewerView(Stage stage, Scene eventsScene) {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        // Title
        Label title = new Label("Viewer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #fff; -fx-font-weight: bold;");

        // Load saved events
        List<EventController.EventData> events = EventController.loadEventsFromDB();

        // Container for event cards
        VBox eventsContainer = new VBox(20);
        eventsContainer.setPadding(new Insets(10));
        eventsContainer.setAlignment(Pos.TOP_CENTER);

        for (EventController.EventData event : events) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: #2C3E50; -fx-background-radius: 12;");
            card.setMaxWidth(600);

            // Event name
            Label name = new Label("📌 " + event.name);
            name.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
            // Start and end date+time
            Label startDateTime = new Label("🟢 Start: " + event.date.format(dtf) + " " + event.startTime);
            startDateTime.setStyle("-fx-text-fill: #EAEAEA;");

            Label endDateTime = new Label("🔴 End: " + event.endDate.format(dtf) + " " + event.endTime);
            endDateTime.setStyle("-fx-text-fill: #EAEAEA;");

            // Duration in days
            Label duration = new Label("⏳ Duration: " + event.durationDays + " day(s)");
            duration.setStyle("-fx-text-fill: #EAEAEA;");

            // Location
            Label location = new Label("📍 Venue: " + event.location);
            location.setStyle("-fx-text-fill: #EAEAEA;");

            // Organizers
            VBox organizersBox = new VBox(5);
            Label orgTitle = new Label("👥 Organizers:");
            orgTitle.setStyle("-fx-text-fill: #F1C40F; -fx-font-weight: bold;");
            organizersBox.setPadding(new Insets(0, 0, 0, 15));

            if (event.organizers != null && !event.organizers.isEmpty()) {
                for (EventController.Organizer org : event.organizers) {
                    Label orgLabel = new Label("• " + org.name + " (ID: " + org.registrationCode + ")");
                    orgLabel.setStyle("-fx-text-fill: #EAEAEA;");
                    organizersBox.getChildren().add(orgLabel);
                }
            } else {
                Label noOrg = new Label("No organizers added");
                noOrg.setStyle("-fx-text-fill: #AAAAAA; -fx-font-style: italic;");
                organizersBox.getChildren().add(noOrg);
            }

            card.getChildren().addAll(name, startDateTime, endDateTime, duration, location, orgTitle, organizersBox);
            eventsContainer.getChildren().add(card);
        }

        // ScrollPane to enable scrolling
        ScrollPane scrollPane = new ScrollPane(eventsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Back button
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10;");
        backBtn.setOnAction(e -> stage.setScene(eventsScene));

        layout.getChildren().addAll(title, scrollPane, backBtn);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("cssfororganizer.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
