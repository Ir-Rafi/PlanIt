import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Myscene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        scene.getStylesheets().add(getClass().getResource("style2.css").toExternalForm());
        stage.setTitle("Event Management App");
        stage.setScene(scene);
        //stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
