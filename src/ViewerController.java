import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ViewerController implements Initializable {

    @FXML private VBox eventsContainer;
    @FXML private Button backButton;

    private Stage stage;
    private Scene eventsScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<EventController.EventData> events = EventController.loadEvents();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy"); // e.g., 10 Nov 2025

        for (EventController.EventData event : events) {
            VBox card = new VBox(10);
            card.getStyleClass().add("event-card");
            card.setStyle("-fx-padding: 15; -fx-background-radius: 12; -fx-background-color: #2C3E50;");

            Label name = new Label("📌 " + event.name);
            name.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

            Label startDate = new Label("📅 Start Date: " + event.date.format(formatter));
            startDate.setStyle("-fx-text-fill: #EAEAEA;");

            Label endDate = new Label("🗓️ End Date: " + event.endDate.format(formatter));
            endDate.setStyle("-fx-text-fill: #EAEAEA;");

            Label duration = new Label("⏳ Duration: " + event.durationDays + " day(s)");
            duration.setStyle("-fx-text-fill: #EAEAEA;");

            Label timeRange = new Label("🕒 Time: " + event.startTime + " - " + event.endTime);
            timeRange.setStyle("-fx-text-fill: #EAEAEA;");

            Label location = new Label("📍 Venue: " + event.location);
            location.setStyle("-fx-text-fill: #EAEAEA;");

            // Organizers section
            VBox organizersBox = new VBox(5);
            Label orgTitle = new Label("👥 Organizers:");
            orgTitle.setStyle("-fx-text-fill: #F1C40F; -fx-font-weight: bold;");
            organizersBox.setStyle("-fx-padding: 0 0 0 15;");

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

            card.getChildren().addAll(name, startDate, endDate, duration, timeRange, location, orgTitle, organizersBox);
            eventsContainer.getChildren().add(card);
        }
    }

    public void setStageAndScene(Stage stage, Scene eventsScene) {
        this.stage = stage;
        this.eventsScene = eventsScene;
        backButton.setOnAction(e -> stage.setScene(eventsScene));
    }
}

