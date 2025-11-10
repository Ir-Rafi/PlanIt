import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainOrganizerView {

    public MainOrganizerView(Stage stage, Scene eventListScene) {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));

        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Label title = new Label("Main Organizer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #fff; -fx-font-weight: bold;");

        Button assignRolesBtn = new Button("Assign Roles to Members");
        Button todoBtn = new Button("Create To-Do List");
        Button bookPlacesBtn = new Button("Book Event Places");
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10;");

        assignRolesBtn.setOnAction(e -> showAlert("Assign Roles", "You can assign roles to your team members here."));
        todoBtn.setOnAction(e -> {
            AdvancedTodoListApp todoView = new AdvancedTodoListApp();
            try {
                todoView.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        bookPlacesBtn.setOnAction(e -> showAlert("Booking", "You can book venues for your event."));
        backBtn.setOnAction(e -> stage.setScene(eventListScene));

        VBox buttonContainer = new VBox(20, assignRolesBtn, todoBtn, bookPlacesBtn);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.prefWidthProperty().bind(layout.widthProperty().multiply(0.35));

        layout.getChildren().addAll(title, buttonContainer, backBtn);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("cssfororganizer.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    private void showAlert(String header, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Main Organizer Action");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
