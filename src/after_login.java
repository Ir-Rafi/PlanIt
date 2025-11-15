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

public class after_login {

    private Stage stage;
    private Scene dashboardScene;

    public void openEventPortal(Stage stage, Scene dashboardScene) {
        this.stage = stage;
        this.dashboardScene = dashboardScene;

        showHomeScreen();
    }

    private void showHomeScreen() {

        VBox homeLayout = new VBox(20);
        homeLayout.setAlignment(Pos.CENTER);
        homeLayout.setPadding(new Insets(50));

        // BACK TO DASHBOARD BUTTON
        Button dashboardBack = new Button("← Back to Dashboard");
        dashboardBack.setFont(Font.font("Poppins", 16));
        dashboardBack.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-background-radius: 10;");

        dashboardBack.setOnMouseEntered(e -> dashboardBack.setStyle(
                "-fx-background-color: #333; -fx-text-fill: white; -fx-background-radius: 10;"
        ));
        dashboardBack.setOnMouseExited(e -> dashboardBack.setStyle(
                "-fx-background-color: #555; -fx-text-fill: white; -fx-background-radius: 10;"
        ));

        dashboardBack.setOnAction(e -> stage.setScene(dashboardScene));

        Text title = new Text("Welcome to Event Portal");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 30));
        title.setFill(Color.web("#2E86DE"));

        Button eventsButton = new Button("Your Events");
        eventsButton.setFont(Font.font("Poppins", 18));
        eventsButton.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 12;");

        homeLayout.getChildren().addAll(dashboardBack, title, eventsButton);

        Scene homeScene = new Scene(homeLayout, 1000, 700);

        // EVENTS SCREEN setup
        Scene eventsScene = createEventsPage(homeScene);

        eventsButton.setOnAction(e -> {
            FadeTransition fade = new FadeTransition(Duration.seconds(0.6));
            fade.setFromValue(0);
            fade.setToValue(1);
            stage.setScene(eventsScene);
            stage.setMaximized(true);
            fade.play();
        });

        stage.setScene(homeScene);
        stage.setMaximized(true);
    }

    private Scene createEventsPage(Scene homeScene) {

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

        // Top bar
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back");
        backButton.setFont(Font.font("Poppins", 14));
        backButton.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10;");
        backButton.setOnAction(e -> stage.setScene(homeScene));

        topBar.getChildren().add(backButton);
        eventRoot.getChildren().add(0, topBar);

        // Example data
        String[][] eventData = {
                {"Tech Conference", "Main Organizer"},
                {"Cultural Fest", "Sub Organizer"},
                {"Sports Meet", "Viewer"}
        };

        for (int i = 0; i < eventData.length; i++) {
            VBox card = createEventCard(eventData[i][0], eventData[i][1]);
            eventsLayout.add(card, i % 3, i / 3);
        }

        return eventsScene;
    }

    private VBox createEventCard(String eventName, String role) {

        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(250, 150);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10;");

        Text eventTitle = new Text(eventName);
        eventTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        eventTitle.setFill(Color.web("#2C3E50"));

        Text roleText = new Text(role);
        roleText.setFont(Font.font("Poppins", 14));

        card.getChildren().addAll(eventTitle, roleText);
        return card;
    }
}
