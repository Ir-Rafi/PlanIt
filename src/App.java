import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Event Management App");

        // --- HOME SCREEN ---
        VBox homeLayout = new VBox(20);
        homeLayout.setAlignment(Pos.CENTER);
        homeLayout.setPadding(new Insets(50));

        Text title = new Text("Welcome to Event Portal");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 30));
        title.setFill(Color.web("#2E86DE"));

        Button eventsButton = new Button("Your Events");
        eventsButton.setFont(Font.font("Poppins", 18));
        eventsButton.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 10 25 10 25;");
        eventsButton.setOnMouseEntered(e -> eventsButton.setStyle("-fx-background-color: #1B4F72; -fx-text-fill: white; -fx-background-radius: 12;"));
        eventsButton.setOnMouseExited(e -> eventsButton.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 12;"));

        homeLayout.getChildren().addAll(title, eventsButton);
        VBox.setVgrow(eventsButton, Priority.ALWAYS);

        Scene homeScene = new Scene(homeLayout, 1000, 700);

        // --- EVENTS SCREEN ---
        GridPane eventsLayout = new GridPane();
        eventsLayout.setAlignment(Pos.CENTER);
        eventsLayout.setHgap(20);
        eventsLayout.setVgap(20);
        eventsLayout.setPadding(new Insets(40));
        eventsLayout.setStyle("-fx-background-color: #f9f9f9;");

        VBox eventRoot = new VBox(20);
        eventRoot.setPadding(new Insets(20));
        eventRoot.getChildren().add(eventsLayout);

        Scene eventsScene = new Scene(eventRoot, 1000, 700);

        // Event data
        String[][] eventData = {
            {"Tech Conference", "Main Organizer"},
            {"Cultural Fest", "Sub Organizer"},
            {"Sports Meet", "Viewer"}
        };

        for (int i = 0; i < eventData.length; i++) {
            VBox card = createEventCard(eventData[i][0], eventData[i][1], stage, eventsScene);
            eventsLayout.add(card, i % 3, i / 3);
        }

        Button backButton = new Button("← Back");
        backButton.setFont(Font.font("Poppins", 14));
        backButton.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10;");
        backButton.setOnAction(e -> stage.setScene(homeScene));
        eventRoot.getChildren().add(0, backButton);

        // Transition effect
        eventsButton.setOnAction(e -> {
            FadeTransition fade = new FadeTransition(Duration.seconds(0.6), eventRoot);
            fade.setFromValue(0);
            fade.setToValue(1);
            stage.setScene(eventsScene);
            stage.setMaximized(true);
            fade.play();
        });

        stage.setScene(homeScene);
        stage.setMaximized(true);
        stage.show();
    }

    private VBox createEventCard(String eventName, String role, Stage stage, Scene eventsScene) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(250, 150);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Text eventTitle = new Text(eventName);
        eventTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        eventTitle.setFill(Color.web("#2C3E50"));

        Text roleText = new Text(role);
        roleText.setFont(Font.font("Poppins", 14));
        Color roleColor = switch (role) {
            case "Main Organizer" -> Color.web("#27AE60");
            case "Sub Organizer" -> Color.web("#F39C12");
            default -> Color.web("#7F8C8D");
        };
        roleText.setFill(roleColor);

        Button openBtn = new Button("Open");
        openBtn.setFont(Font.font("Poppins", 14));
        openBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10;");
        openBtn.setOnAction(e -> {
            switch (role) {
                case "Main Organizer" -> new MainOrganizerView(stage, eventsScene);
                case "Sub Organizer" -> new SubOrganizerView(stage, eventsScene);
                case "Viewer" -> new ViewerView(stage, eventsScene);
            }
        });

        card.getChildren().addAll(eventTitle, roleText, openBtn);
        return card;
    }

    public static void main(String[] args) {
        launch();
    }
}
