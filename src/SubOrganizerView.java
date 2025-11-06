import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SubOrganizerView {

    public SubOrganizerView(Stage stage, Scene eventsScene) {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));

        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Label title = new Label("Sub Organizer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #fff; -fx-font-weight: bold;");

        Button assignedTasksBtn = new Button("View Assigned Tasks");
        Button completeTaskBtn = new Button("Mark Task as Complete");
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10;");

        assignedTasksBtn.setOnAction(e -> showAlert("Assigned Tasks", "These are your assigned tasks from the Main Organizer."));
        completeTaskBtn.setOnAction(e -> showAlert("Complete Task", "You can mark assigned tasks as completed."));
        backBtn.setOnAction(e -> stage.setScene(eventsScene));

        VBox buttonContainer = new VBox(20, assignedTasksBtn, completeTaskBtn);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.prefWidthProperty().bind(layout.widthProperty().multiply(0.35));

        layout.getChildren().addAll(title, buttonContainer, backBtn);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("cssfororganizer.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sub Organizer Action");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
