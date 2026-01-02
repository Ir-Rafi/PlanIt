import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FormController extends Controller {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox page1, page2, page3;

    @FXML
    private Button continue1, continue2, continue3;

    @FXML
    public void initialize() {
        showPage(1);

        continue1.setOnAction(e -> showPage(2));
        continue2.setOnAction(e -> showPage(3));
        continue3.setOnAction(e -> {
            System.out.println("Continue3 clicked");
            
            Stage stage = (Stage) rootPane.getScene().getWindow();
            
            // ✅ GET ACTUAL USERNAME FROM SESSION - NOT HARDCODED!
            String username = Session.getUsername();
            
            System.out.println("Session Username: " + username);
            System.out.println("Session UserID: " + Session.getUserId());
            
            if (username != null && !username.isEmpty()) {
                System.out.println("Loading dashboard with username: " + username);
                Controller.loadDashboardWithLoading(stage, username);
            } else {
                System.out.println("ERROR: Username is null or empty!");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Session Error");
                    alert.setContentText("User session not found. Please log in again.");
                    alert.showAndWait();
                });
            }
        });
    }

    private void showPage(int page) {
        rootPane.getChildren().clear();
        if (page == 1) rootPane.getChildren().add(page1);
        else if (page == 2) rootPane.getChildren().add(page2);
        else if (page == 3) rootPane.getChildren().add(page3);
    }
}