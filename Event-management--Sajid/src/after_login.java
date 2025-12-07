import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class after_login {

    private Stage stage;
    private Scene dashboardScene;

    public void applyHoverEffect(Button btn, String normalColor, String hoverColor) {

        btn.setStyle("-fx-background-color: " + normalColor + "; -fx-text-fill: white; -fx-background-radius: 10;");

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-background-radius: 10;");
            btn.setCursor(javafx.scene.Cursor.HAND);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: " + normalColor + "; -fx-text-fill: white; -fx-background-radius: 10;");
        });
    }

    public void openEventPortal(Stage stage, Scene dashboardScene) {
        this.stage = stage;
        this.dashboardScene = dashboardScene;

        showHomeScreen();
    }

    // ---------------- HOME SCREEN ----------------
    private void showHomeScreen() {

        VBox homeLayout = new VBox(20);
        homeLayout.setAlignment(Pos.CENTER);
        homeLayout.setPadding(new Insets(50));

        Button dashboardBack = new Button("← Back to Dashboard");
        dashboardBack.setFont(Font.font("Poppins", 16));
        dashboardBack.setStyle(
                "-fx-background-color: #555; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 8 20 8 20;"
        );

        applyHoverEffect(dashboardBack, "#555555", "#333333");

        dashboardBack.setOnAction(e -> {
            stage.setScene(dashboardScene);
            stage.setMaximized(true);
        });

        Text title = new Text("Welcome to Event Portal");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 30));
        title.setFill(Color.web("#2E86DE"));

        Button eventsButton = new Button("Your Events");
        eventsButton.setFont(Font.font("Poppins", 18));
        eventsButton.setStyle(
                "-fx-background-color: #2E86DE; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 10 25 10 25;"
        );
        applyHoverEffect(eventsButton, "#2E86DE", "#1B4F72");

        homeLayout.getChildren().addAll(dashboardBack, title, eventsButton);
        VBox.setVgrow(eventsButton, Priority.ALWAYS);

        Scene homeScene = new Scene(homeLayout, 1000, 700);
        Scene eventsScene = createEventsPage(homeScene);

        eventsButton.setOnAction(e -> {
            FadeTransition fade = new FadeTransition(Duration.seconds(0.6), eventsScene.getRoot());
            fade.setFromValue(0);
            fade.setToValue(1);
            stage.setScene(eventsScene);
            stage.setMaximized(true);
            fade.play();
        });

        stage.setScene(homeScene);
        stage.setMaximized(true);
    }

    // ---------------- EVENTS PAGE ----------------
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

        // Top bar WITHOUT Create Event button
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        Button backButton = new Button("← Back");
        backButton.setFont(Font.font("Poppins", 14));
        backButton.setStyle(
                "-fx-background-color: #2E86DE; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 8 20 8 20;"
        );
        applyHoverEffect(backButton, "#2E86DE", "#1B4F72");

        backButton.setOnAction(e -> stage.setScene(homeScene));

        topBar.getChildren().addAll(backButton);
        eventRoot.getChildren().add(0, topBar);

        String[][] eventData = {
                {"Event Creation Dashboard", "Main Organizer"},
                {"Sub Organizer's Dashboard", "Sub Organizer"},
                {"All Events", "Viewer"}
        };

        for (int i = 0; i < eventData.length; i++) {
            VBox card = createEventCard(eventData[i][0], eventData[i][1], eventsScene);
            eventsLayout.add(card, i % 3, i / 3);
        }

        return eventsScene;
    }

    private VBox createEventCard(String eventName, String role, Scene eventsScene) {

        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(250, 150);
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ccc; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);"
        );

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
        openBtn.setStyle(
                "-fx-background-color: #2E86DE; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 8 15 8 15;"
        );

        applyHoverEffect(openBtn, "#2E86DE", "#1B4F72");

        openBtn.setOnAction(e -> {
            try {
                switch (role) {
                    case "Main Organizer" -> {
                        String mainName = Session.getUserName();
                        int mainId = Session.getUserId();
                        new MainOrganizerView(stage, eventsScene, mainName, mainId);
                    }
                    case "Sub Organizer" -> {
                        String subName = Session.getUserName();
                        new SubOrganizerView(stage, eventsScene, subName);
                    }
                    default -> new ViewerView(stage, eventsScene);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                        "View open korte gele error holo:\n" + ex.getMessage())
                        .showAndWait();
            }
        });

        card.getChildren().addAll(eventTitle, roleText, openBtn);
        return card;
    }
}
