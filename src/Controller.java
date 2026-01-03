import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller {

    @FXML
    private Button buttonYourEvents;

    @FXML
    private ImageView calendarIcon;

    // If you have a Continue button in your FXML, hook it here:
    @FXML
    private Button continueButton;

    public void initialize() {
        // Load icon image
        Image img = null;
        try {
            img = new Image(getClass().getResourceAsStream("img/calender.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (img != null) {
            calendarIcon.setImage(img);
        }

        // Handle YourEvents button click
        if (buttonYourEvents != null) {
            buttonYourEvents.setOnAction(this::handleYourEventsClick);
        }

        // Remove any keyboard-event listener (you asked to remove it)
        // So nothing here for key events
    }

    @FXML
    private void handleYourEventsClick(ActionEvent event) {
        try {
            Stage stage = (Stage) buttonYourEvents.getScene().getWindow();
            loadingLoginPage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // New method to handle Continue button click
    @FXML
    private void handleContinueButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) continueButton.getScene().getWindow();
            loadingLoginPage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadingLoginPage(Stage stage) {
        if (stage == null) {
            System.err.println("Error: Stage is null in loadingLoginPage");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(Controller.class.getResource("fxml/button.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                Controller.class.getResource("/css/login_style.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle("Login_page");
            stage.setMaximized(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Resource Error");
            alert.setHeaderText("Login Page Load Failed");
            alert.setContentText("Unable to load required UI files.");
            alert.showAndWait();

            e.printStackTrace();

            VBox fallback = new VBox(10);
            fallback.setAlignment(Pos.CENTER);
            fallback.setPadding(new Insets(20));
            fallback.getChildren().add(new Label("Failed to load login page resources"));

            Scene fallbackScene = new Scene(fallback, 400, 200);
            stage.setScene(fallbackScene);
            stage.setTitle("Error");
            stage.show();
        }
    }

    public static void loadDashboardWithLoading(Stage currentStage, String username) {
    javafx.application.Platform.runLater(() -> {
        // Show loading window
        Stage loadingStage = new Stage();
        loadingStage.setTitle("Loading Dashboard");
        loadingStage.setWidth(500);
        loadingStage.setHeight(300);
        loadingStage.setResizable(false);
        loadingStage.centerOnScreen();

        // Create loading screen UI
        VBox loadingPane = new VBox(15);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 30;");

        javafx.scene.control.ProgressIndicator spinner = new javafx.scene.control.ProgressIndicator();
        spinner.setPrefSize(80, 80);
        spinner.setStyle("-fx-progress-color: #2E86DE;");

        Label loadingLabel = new Label("Loading Dashboard...");
        loadingLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333; -fx-font-weight: bold;");

        Label statusLabel = new Label("Fetching your data...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        loadingPane.getChildren().addAll(spinner, loadingLabel, statusLabel);

        Scene loadingScene = new Scene(loadingPane);
        loadingStage.setScene(loadingScene);
        loadingStage.show();

        // Load dashboard in background thread
        Thread dashboardThread = new Thread(() -> {
            try {
                // Update status
                javafx.application.Platform.runLater(() -> statusLabel.setText("Loading dashboard interface..."));

                // Load FXML in background
                FXMLLoader loader = new FXMLLoader(Controller.class.getResource("fxml/dashboard.fxml"));
                Parent root = loader.load();
                DashboardController controller = loader.getController();

                // Update status
                javafx.application.Platform.runLater(() -> statusLabel.setText("Initializing your profile..."));

                // Switch to UI thread for scene creation and display
                javafx.application.Platform.runLater(() -> {
                    try {
                        // Set username on controller (triggers async data loading)
                        controller.setLoggedInUsername(username);

                        // Create scene
                        Scene scene = new Scene(root);

                        currentStage.setScene(scene);
                        currentStage.setFullScreen(true);
                        currentStage.setFullScreenExitHint("");
                        currentStage.show();

                        // Close loading window
                        loadingStage.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Failed to load dashboard: " + e.getMessage());
                        alert.showAndWait();
                        loadingStage.close();
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to load dashboard: " + e.getMessage());
                    alert.showAndWait();
                    loadingStage.close();
                });
                e.printStackTrace();
            }
        });

        dashboardThread.setDaemon(true);
        dashboardThread.start();
    });
}
}
