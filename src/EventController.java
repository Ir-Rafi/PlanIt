import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventController {

    @FXML private TextField eventNameField;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField mainOrganizerNameField;
    @FXML private TextField mainOrganizerNumberField;
    @FXML private VBox organizersContainer;
    @FXML private Label successLabel;
    @FXML private Button backButton;

    @FXML
    private void handleAddOrganizer() {
        HBox organizerBox = new HBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Organizer Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        TextField codeField = new TextField();
        codeField.setPromptText("Code"); // new field for code

        organizerBox.getChildren().addAll(nameField, phoneField, codeField);
        organizersContainer.getChildren().add(organizerBox);
    }

    @FXML
    private void handleSubmit() {
        String eventName = eventNameField.getText();
        LocalDate eventDate = eventDatePicker.getValue();
        String mainName = mainOrganizerNameField.getText();
        String mainPhone = mainOrganizerNumberField.getText();

        if(eventName.isEmpty() || eventDate == null || mainName.isEmpty() || mainPhone.isEmpty()) {
            successLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            successLabel.setText("Please fill all required fields!");
            return;
        }

        List<Organizer> organizers = new ArrayList<>();
        for (Node node : organizersContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox box = (HBox) node;
                TextField nameField = (TextField) box.getChildren().get(0);
                TextField phoneField = (TextField) box.getChildren().get(1);
                TextField codeField = (TextField) box.getChildren().get(2); // new field

                if (!nameField.getText().isEmpty() && !phoneField.getText().isEmpty()) {
                    organizers.add(new Organizer(nameField.getText(), phoneField.getText(), codeField.getText()));
                }
            }
        }

        Event event = new Event(eventName, eventDate, mainName, mainPhone, organizers);
        EventStore.addEvent(event);

        eventNameField.clear();
        eventDatePicker.setValue(null);
        mainOrganizerNameField.clear();
        mainOrganizerNumberField.clear();
        organizersContainer.getChildren().clear();

        successLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        successLabel.setText("Event Created!");
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> successLabel.setText(""));
        pause.play();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
