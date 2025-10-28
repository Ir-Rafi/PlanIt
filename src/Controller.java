import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller {

    @FXML

    private Text continueText;

    public void initialize() {
        // Timeline to toggle visibility every 500ms
        FadeTransition ft = new FadeTransition(Duration.seconds(1), continueText);
        ft.setFromValue(1.0);  // fully visible
        ft.setToValue(0.0);    // fully transparent
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.setAutoReverse(true); // fade in & out
        ft.play();

        continueText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleContinue);
            } 
            else if (oldScene != null) {
                oldScene.setOnKeyPressed(null);
            }
});
    }
    
    private void handleContinue(KeyEvent event) {
        try {
            Stage stage = (Stage) continueText.getScene().getWindow();
            loadLoginPage(stage);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadLoginPage(Stage stage) {
    try {
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource("button.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Controller.class.getResource("login_style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Login_page");
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}

