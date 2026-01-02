import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/dashboard.fxml"));
        Parent root = loader.load();

        try {
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setLoggedInUsername(Session.getUserName());
            }
        } catch (Throwable t) {
            // Non-fatal: log/print if controller couldn't be set (keeps backward compatibility)
            System.err.println("Warning: could not set Dashboard controller username: " + t.getMessage());
        }

        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("css/dashboard_style.css").toExternalForm());

        stage.setTitle("PlanIt!  - Dashboard");
        stage.setScene(scene);
        //stage. setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}