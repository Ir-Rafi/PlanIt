import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("dashboard_style.css").toExternalForm());

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