import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
        Button createNewEventBtn = new Button("Create New Event"); // ★ NEW BUTTON
        Button backBtn = new Button("← Back");

        // Styles
        applyHoverEffect(assignRolesBtn, "#1565C0", "#0D47A1");
        applyHoverEffect(todoBtn, "#42A5F5", "#1E88E5");
        applyHoverEffect(bookPlacesBtn, "#29B6F6", "#0288D1");
        applyHoverEffect(progressReportBtn, "#26C6DA", "#00838F");
        applyHoverEffect(chatBtn, "#7E57C2", "#5E35B1");
        applyHoverEffect(createNewEventBtn, "#27AE60", "#1E8449");  // NEW STYLE
        applyHoverEffect(backBtn, "#1E88E5", "#0D47A1");

        // Actions
        assignRolesBtn.setOnAction(e -> new AssignRolesWindow(mainOrganizerName, mainOrganizerId).show());

        todoBtn.setOnAction(e -> {
            Scene organizerScene = stage.getScene();
            new AdvancedTodoListApp(organizerScene).start(stage);
        });

        bookPlacesBtn.setOnAction(e -> {
            int eventId = 0;
            new BookingPage(stage, stage.getScene(), eventId);
        });

        progressReportBtn.setOnAction(e -> new ProgressReportWindow().show());

        chatBtn.setOnAction(e -> chatWindows.openServerChat(mainOrganizerName));

        backBtn.setOnAction(e -> stage.setScene(eventListScene));

        // ★ NEW EVENT BUTTON FUNCTIONALITY
        createNewEventBtn.setOnAction(e -> {
            try {
                EventController.openEventForm(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox buttonContainer = new VBox(20,
                assignRolesBtn,
                todoBtn,
                bookPlacesBtn,
                progressReportBtn,
                chatBtn,
                createNewEventBtn   // ★ ADDED HERE
        );

        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.prefWidthProperty().bind(layout.widthProperty().multiply(0.35));

        layout.getChildren().addAll(title, buttonContainer, backBtn);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("cssfororganizer.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
