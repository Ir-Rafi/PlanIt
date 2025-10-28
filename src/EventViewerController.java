import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class EventViewerController {

    @FXML private TextField codeField;
    @FXML private Button submitButton;
    @FXML private VBox eventsContainer;
    @FXML private Button closeButton;

    @FXML
    private void handleSubmit() {
        String enteredCode = codeField.getText().trim();
        List<Event> allEvents = EventStore.getSubmittedEvents();

        List<Event> filteredEvents;

        if (!enteredCode.isEmpty()) {
            filteredEvents = allEvents.stream()
                    .filter(event -> event.getOrganizers() != null &&
                            event.getOrganizers().stream()
                                    .anyMatch(org -> enteredCode.equals(org.getCode())))
                    .collect(Collectors.toList());

            if (filteredEvents.isEmpty()) {
                filteredEvents = allEvents;
            }
        } else {
            filteredEvents = allEvents;
        }

        displayEvents(filteredEvents, enteredCode);
    }

    private void displayEvents(List<Event> events, String enteredCode) {
        eventsContainer.getChildren().clear();

        if (events.isEmpty()) {
            eventsContainer.getChildren().add(new Label("No events to display!"));
            return;
        }

        boolean isEditable = events.stream()
                .anyMatch(event -> event.getOrganizers() != null &&
                        event.getOrganizers().stream()
                                .anyMatch(org -> enteredCode.equals(org.getCode())));

        for (Event e : events) {
            StringBuilder sb = new StringBuilder();
            sb.append("📌 Event Name: ").append(e.getName()).append("\n")
              .append("📅 Date: ").append(e.getDate()).append("\n")
              .append("👤 Main Organizer: ").append(e.getMainOrganizerName())
              .append(" (").append(e.getMainOrganizerPhone()).append(")\n");

            if (e.getOrganizers() != null && !e.getOrganizers().isEmpty()) {
                sb.append("👥 Additional Organizers: ")
                  .append(e.getOrganizers().stream()
                          .map(org -> org.getName() + " (" + org.getCode() + ")")
                          .collect(Collectors.joining(", ")))
                  .append("\n");
            }

            TextArea eventArea = new TextArea(sb.toString());
            eventArea.setEditable(isEditable);
            eventArea.setWrapText(true);
            eventArea.setPrefHeight(120);

            eventsContainer.getChildren().add(eventArea);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
