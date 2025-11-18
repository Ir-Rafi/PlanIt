import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Cursor;

import java.io.IOException;


public class MainOrganizerView extends after_login {

    private final String mainOrganizerName;
    private final int mainOrganizerId;

    public MainOrganizerView(Stage stage, Scene eventListScene, String mainOrganizerName, int mainOrganizerId) {
        this.mainOrganizerName = mainOrganizerName;
        this.mainOrganizerId = mainOrganizerId;

        VBox layout = new VBox(25);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #E3F2FD, #90CAF9);");

        Label title = new Label("Main Organizer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #0D47A1; -fx-font-weight: bold;");

        Button assignRolesBtn = new Button("Assign Roles to Members");
        Button todoBtn = new Button("Create To-Do List");
        Button bookPlacesBtn = new Button("Book Event Places");
        Button progressReportBtn = new Button("Manage Progress Reports");
        Button chatBtn = new Button("Open Chat");
        Button backBtn = new Button("← Back");

// 🔹 Base styles
        String baseButtonStyle =
                "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 18 8 18;";

// 🔹 Individual normal styles
        applyHoverEffect(assignRolesBtn,    "#1565C0", "#0D47A1");
        applyHoverEffect(todoBtn,          "#42A5F5", "#1E88E5");
        applyHoverEffect(bookPlacesBtn,    "#29B6F6", "#0288D1");
        applyHoverEffect(progressReportBtn,"#26C6DA", "#00838F");
        applyHoverEffect(chatBtn,          "#7E57C2", "#5E35B1");
        applyHoverEffect(backBtn,          "#1E88E5", "#0D47A1");

        // ✅ Corrected call to AssignRolesWindow
        assignRolesBtn.setOnAction(e -> {
            new AssignRolesWindow(mainOrganizerName, mainOrganizerId).show();
        });

        todoBtn.setOnAction(e -> {
            // Save the current organizer dashboard scene
            Scene organizerScene = stage.getScene();

            // Open the advanced to-do list, with a working Back button
            AdvancedTodoListApp todoApp = new AdvancedTodoListApp(organizerScene);
            todoApp.start(stage);
        });

        bookPlacesBtn.setOnAction(e -> {
            int eventId = 0; // optional placeholder
            new BookingPage(stage, stage.getScene(), eventId);
        });

        progressReportBtn.setOnAction(e -> {
            ProgressReportWindow reportWindow = new ProgressReportWindow();
            reportWindow.show();
        });

        backBtn.setOnAction(e -> stage.setScene(eventListScene));

        chatBtn.setOnAction(e -> {
            chatWindows.openServerChat(mainOrganizerName);
        });

        VBox buttonContainer = new VBox(20, assignRolesBtn, todoBtn, bookPlacesBtn, progressReportBtn, chatBtn);
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
