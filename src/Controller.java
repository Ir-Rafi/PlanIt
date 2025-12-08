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
            img = new Image(getClass().getResourceAsStream("calender.png"));
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
        try {
            FXMLLoader loader = new FXMLLoader(Controller.class.getResource("/button.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                Controller.class.getResource("/login_style.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle("Login_page");
            stage.show();

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
}
