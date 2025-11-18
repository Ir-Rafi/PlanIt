
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.temporal.ChronoUnit;
import java.sql.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EventController implements Initializable {

    @FXML private TextField eventNameField;
    @FXML private DatePicker eventDatePicker;
    @FXML private ComboBox<String> endsAfterCombo;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private ComboBox<String> locationCombo;
    @FXML private TextArea eventDescriptionArea;
    @FXML private VBox organizersContainer;
    @FXML private HBox colorContainer;
    @FXML private Button closeButton;
    @FXML private Button attachFileButton;
    @FXML private Button addImageButton;
    @FXML private Button addOrganizerButton;
    @FXML private Button cancelButton;
    @FXML private Button createButton;
    @FXML private ToggleGroup showMeGroup;
    @FXML private ToggleGroup visibilityGroup;

    private String selectedColor = "#6366F1";
    private List<OrganizerData> organizersList = new ArrayList<>();
    private File attachedFile = null;
    private File eventImage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeLocations();
        initializeTimes();
        initializeEndsAfter();
        initializeColorPicker();
    }

    private void initializeLocations() {
        locationCombo.getItems().addAll("TSC", "CURZON", "SENATE");
    }

    private void initializeTimes() {
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                startTimeCombo.getItems().add(time);
                endTimeCombo.getItems().add(time);
            }
        }
    }

    private void initializeEndsAfter() {
        endsAfterCombo.getItems().addAll(
            "Same Day", "1 Day", "2 Days", "3 Days", "1 Week", "2 Weeks", "Custom"
        );
        endsAfterCombo.setValue("Same Day");
    }

    private void initializeColorPicker() {
        String[] colors = {
            "#6366F1", "#06B6D4", "#10B981", "#F59E0B", 
            "#F97316", "#EF4444", "#EC4899", "#8B5CF6"
        };

        for (String color : colors) {
            Circle colorCircle = new Circle(15);
            colorCircle.setFill(Color.web(color));
            colorCircle.setStroke(Color.web("#E5E7EB"));
            colorCircle.setStrokeWidth(2);
            colorCircle.getStyleClass().add("color-circle");
            
            colorCircle.setOnMouseClicked(e -> {
                selectedColor = color;
                updateColorSelection(colorCircle);
            });
            
            colorContainer.getChildren().add(colorCircle);
            
            if (color.equals("#6366F1")) {
                colorCircle.setStrokeWidth(3);
                colorCircle.setStroke(Color.web("#4F46E5"));
            }
        }
    }

    private void updateColorSelection(Circle selectedCircle) {
        colorContainer.getChildren().forEach(node -> {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                circle.setStrokeWidth(2);
                circle.setStroke(Color.web("#E5E7EB"));
            }
        });
        
        selectedCircle.setStrokeWidth(3);
        selectedCircle.setStroke(Color.web("#4F46E5"));
    }

    @FXML
    private void handleAddOrganizer() {
        VBox organizerCard = new VBox(10);
        organizerCard.getStyleClass().add("organizer-card");
        organizerCard.setPadding(new Insets(15));
        organizerCard.setSpacing(10);
        
        // Organizer Name
        VBox nameBox = new VBox(5);
        Label nameLabel = new Label("Organizer Name");
        nameLabel.getStyleClass().add("organizer-field-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter organizer name");
        nameField.getStyleClass().add("organizer-field");
        nameBox.getChildren().addAll(nameLabel, nameField);
        
        // Registration Code
        VBox codeBox = new VBox(5);
        Label codeLabel = new Label("Registration Code");
        codeLabel.getStyleClass().add("organizer-field-label");
        TextField codeField = new TextField();
        codeField.setPromptText("Enter registration code");
        codeField.getStyleClass().add("organizer-field");
        codeBox.getChildren().addAll(codeLabel, codeField);
        
        // Remove Button
        Button removeBtn = new Button("✕ Remove");
        removeBtn.getStyleClass().add("remove-organizer-btn");
        removeBtn.setOnAction(e -> {
            organizersContainer.getChildren().remove(organizerCard);
            organizersList.removeIf(org -> org.nameField == nameField);
        });
        
        organizerCard.getChildren().addAll(nameBox, codeBox, removeBtn);
        organizersContainer.getChildren().add(organizerCard);
        
        organizersList.add(new OrganizerData(nameField, codeField));
    }

    @FXML
    private void handleAttachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx")
        );
        
        attachedFile = fileChooser.showOpenDialog(attachFileButton.getScene().getWindow());
        
        if (attachedFile != null) {
            showAlert("File Attached", "File: " + attachedFile.getName(), Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleAddImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Event Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        eventImage = fileChooser.showOpenDialog(addImageButton.getScene().getWindow());
        
        if (eventImage != null) {
            showAlert("Image Added", "Image: " + eventImage.getName(), Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleCreate() {
        // Validate basic fields
        if (eventNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter an event name", Alert.AlertType.ERROR);
            return;
        }
        
        if (eventDatePicker.getValue() == null) {
            showAlert("Validation Error", "Please select an event date", Alert.AlertType.ERROR);
            return;
        }
        LocalDate startDate = eventDatePicker.getValue();
String endsAfter = endsAfterCombo.getValue();

// Calculate end date and duration
LocalDate endDate = calculateEndDate(startDate, endsAfter);
long durationDays = calculateDurationDays(startDate, endDate);

        if (locationCombo.getValue() == null) {
            showAlert("Validation Error", "Please select a location", Alert.AlertType.ERROR);
            return;
        }

String currentUsername = Session.getUsername();
    int currentOrganizerId = Session.getOrganizerId();

    if (currentUsername == null || currentOrganizerId == 0) {
        showAlert("Authentication Error", "You must be logged in to create an event.", Alert.AlertType.ERROR);
        return;
    }


        
        // Collect and validate organizers
        List<Organizer> organizers = new ArrayList<>();
         organizers.add(new Organizer(currentUsername, currentOrganizerId));
        // ✅ Check booking ONCE for main organizer only
if (!isPlaceBookedByOrganizer(
        locationCombo.getValue(),
        startDate,
        endDate,
        startTimeCombo.getValue(),
        endTimeCombo.getValue(),
        currentOrganizerId)) {

    showAlert("Booking Error",
            "You cannot create this event. The place is not booked by you for the selected date/time range.",
            Alert.AlertType.ERROR);
    return;
}

// ✅ Then process other organizers (sub-organizers)
for (OrganizerData orgData : organizersList) {
    String name = orgData.nameField.getText().trim();
    String codeText = orgData.codeField.getText().trim();

    if (name.isEmpty() || codeText.isEmpty()) continue;

    int code;
    try {
        code = Integer.parseInt(codeText);
    } catch (NumberFormatException e) {
        showAlert("Validation Error", "Organizer code must be a number!", Alert.AlertType.ERROR);
        return;
    }

    if (!isValidOrganizer(name, code)) {
        showAlert("Validation Error", "Organizer " + name + " with ID " + code + " is invalid!", Alert.AlertType.ERROR);
        return;
    }

    boolean alreadyAdded = organizers.stream().anyMatch(o -> o.registrationCode == code);
    if (!alreadyAdded) {
        organizers.add(new Organizer(name, code));
    }
}



        // Create event
        EventData event = new EventData(
            eventNameField.getText().trim(),
            startDate,endDate,
            startTimeCombo.getValue(),
            endTimeCombo.getValue(),
            locationCombo.getValue(),
            eventDescriptionArea.getText().trim(),
            organizers,
            selectedColor,
            ((RadioButton) showMeGroup.getSelectedToggle()).getText(),
            ((RadioButton) visibilityGroup.getSelectedToggle()).getText(),
            attachedFile,
            eventImage,
            durationDays
        );
        
       
        
        String message = organizers.size() > 0 
            ? "Event created successfully with " + organizers.size() + " organizer(s)!"
            : "Event created successfully!";

        // Load existing events



// Check if user is logged in
if (currentUsername == null || currentOrganizerId == 0) {
    showAlert("Authentication Error", "You must be logged in to create an event.", Alert.AlertType.ERROR);
    return;
}






insertEventIntoDatabase(event);



            
        showAlert("Success", message, Alert.AlertType.INFORMATION);
        handleClose();
    }

    @FXML
    private void handleCancel() {
        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    public static void openEventForm(Stage parentStage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                EventController.class.getResource("CreateEventForm.fxml")
            );
            VBox root = loader.load();
            
            Stage formStage = new Stage();
            formStage.setTitle("Create New Event");
            formStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            formStage.initOwner(parentStage);
            formStage.setScene(new javafx.scene.Scene(root));
            formStage.setResizable(false);
            formStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showStaticAlert("Error", "Could not load event form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private static void showStaticAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Organizer field reference holder
    private static class OrganizerData {
        TextField nameField;
        TextField codeField;

        public OrganizerData(TextField nameField, TextField codeField) {
            this.nameField = nameField;
            this.codeField = codeField;
        }
    }

    // Organizer info holder
    static class Organizer implements Serializable {
        String name;
        int registrationCode;

        public Organizer(String name, int registrationCode) {
            this.name = name;
            this.registrationCode = registrationCode;
        }
    }

    // Event data holder
     static class EventData implements Serializable {
        String name, startTime, endTime, location, description, color, showMe, visibility;
        LocalDate date;
            LocalDate endDate;       // calculated end date
    long durationDays;       // calculated duration
        List<Organizer> organizers;
        File attachedFilePath, eventImagePath;
        public EventData(String name, LocalDate date, LocalDate enDate, String startTime, String endTime, 
                 String location, String description, List<Organizer> organizers,
                 String color, String showMe, String visibility, File attachedFile, File eventImage, long durationDays) {
    this.name = name;
    this.date = date;
    this.endDate = enDate;  // fixed
    this.durationDays = durationDays;
    this.startTime = startTime;
    this.endTime = endTime;
    this.location = location;
    this.description = description;
    this.organizers = new ArrayList<>(organizers);
    this.color = color;
    this.showMe = showMe;
    this.visibility = visibility;
    this.attachedFilePath = attachedFile;
    this.eventImagePath = eventImage;
}

    }

    //new
    // Path to store data


// Load all events from file
@SuppressWarnings("unchecked")


private Connection getConnection() throws Exception {
    String url = "jdbc:mysql://ununqd8usvy0wouy:GmDEehgTBjzyuPRuA8i8@b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc";
    String user = "ununqd8usvy0wouy" ;
    String password = "GmDEehgTBjzyuPRuA8i8";
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(url, user, password);
}

private boolean isValidOrganizer(String username, int registrationCode) {
    // Allow the logged-in organizer
    if (Session.getUsername() != null &&
        Session.getUsername().equalsIgnoreCase(username) &&
        Session.getOrganizerId() == registrationCode) {
        return true;
    }

    // Otherwise, check database
    String sql = "SELECT ID FROM users WHERE Username = ? AND ID = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.setInt(2, registrationCode);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

private LocalDate calculateEndDate(LocalDate startDate, String endsAfter) {
    if (startDate == null || endsAfter == null) return startDate;

    int days;
    switch (endsAfter) {
        case "Same Day" -> days = 1;
        case "1 Day" -> days = 2;
        case "2 Days" -> days = 3;
        case "3 Days" -> days = 4;
        case "1 Week" -> days = 8;
        case "2 Weeks" -> days = 15;
        default -> days = 1; // Custom or unrecognized
    }

    return startDate.plusDays(days - 1); // subtract 1 because start day counts
}

private long calculateDurationDays(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) return 1;
    return ChronoUnit.DAYS.between(startDate, endDate) + 1; // inclusive
}

private void insertEventIntoDatabase(EventData event) {
    String sql = """
        INSERT INTO events (
            event_name, place_name, start_date, end_date,
            start_time, end_time, Description, Color,
            ShowMe, Visibility, AttachedFilePath, EventImagePath,
            organizer_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    try (Connection conn = getConnection()) {
        // Start transaction
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, event.name);
            ps.setString(2, event.location);
            ps.setDate(3, java.sql.Date.valueOf(event.date));
            ps.setDate(4, java.sql.Date.valueOf(event.endDate));
            ps.setString(5, event.startTime);
            ps.setString(6, event.endTime);
            ps.setString(7, event.description);
            ps.setString(8, event.color);
            ps.setString(9, event.showMe);
            ps.setString(10, event.visibility);
            ps.setString(11, event.attachedFilePath != null ? event.attachedFilePath.getAbsolutePath() : null);
            ps.setString(12, event.eventImagePath != null ? event.eventImagePath.getAbsolutePath() : null);
            ps.setInt(13, event.organizers.get(0).registrationCode); // main organizer

            ps.executeUpdate();

            // Get generated event_id
            ResultSet keys = ps.getGeneratedKeys();
            int eventId = 0;
            if (keys.next()) {
                eventId = keys.getInt(1);
            }

            // Insert all organizers (main + sub-organizers)
            if (!event.organizers.isEmpty()) {
                insertOrganizers(conn, eventId, event.organizers);
            }

            conn.commit();
            System.out.println("✅ Event and organizers saved successfully to MySQL!");

        } catch (Exception e) {
            conn.rollback(); // rollback on failure
            e.printStackTrace();
            showAlert("Database Error", "Failed to save event: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            conn.setAutoCommit(true);
        }

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Database Error", "Failed to save event connection: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}


  @SuppressWarnings("unchecked")
public static List<EventData> loadEventsFromDB() {
    List<EventData> events = new ArrayList<>();

    String url = "jdbc:mysql://ununqd8usvy0wouy:GmDEehgTBjzyuPRuA8i8@b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc";
    String user = "ununqd8usvy0wouy";
    String password = "GmDEehgTBjzyuPRuA8i8";

    String sql = "SELECT event_id, event_name, place_name, start_date, end_date, start_time, end_time, Description, Color, ShowMe, Visibility, AttachedFilePath, EventImagePath FROM events";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int eventId = rs.getInt("event_id");
            String name = rs.getString("event_name");
            String location = rs.getString("place_name");
            LocalDate startDate = rs.getDate("start_date").toLocalDate();
            LocalDate endDate = rs.getDate("end_date").toLocalDate();
            String startTime = rs.getString("start_time");
            String endTime = rs.getString("end_time");
            String description = rs.getString("Description");
            String color = rs.getString("Color");
            String showMe = rs.getString("ShowMe");
            String visibility = rs.getString("Visibility");
            String attachedFilePath = rs.getString("AttachedFilePath");
            String eventImagePath = rs.getString("EventImagePath");

            long durationDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

            // Fetch organizers using event_id
            List<Organizer> organizers = new ArrayList<>();
            try (PreparedStatement orgPs = conn.prepareStatement(
                    "SELECT organizer_name, organizer_id FROM organizers WHERE event_id = ?")) {
                orgPs.setInt(1, eventId);
                ResultSet orgRs = orgPs.executeQuery();
                while (orgRs.next()) {
                    organizers.add(new Organizer(orgRs.getString("organizer_name"), orgRs.getInt("organizer_id")));
                }
            }

            events.add(new EventData(
                    name,
                    startDate,
                    endDate,
                    startTime,
                    endTime,
                    location,
                    description,
                    organizers,
                    color,
                    showMe,
                    visibility,
                    attachedFilePath != null ? new File(attachedFilePath) : null,
                    eventImagePath != null ? new File(eventImagePath) : null,
                    durationDays
            ));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return events;
}


private void insertOrganizers(Connection conn, int eventId, List<Organizer> organizers) throws SQLException {
    String sql = "INSERT INTO organizers (event_id, organizer_name, organizer_id) VALUES (?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        for (Organizer org : organizers) {
            ps.setInt(1, eventId);
            ps.setString(2, org.name);
            ps.setInt(3, org.registrationCode);
            ps.addBatch();
        }
        ps.executeBatch();
    }
}


private boolean isPlaceBookedByOrganizer(String placeName, LocalDate startDate, LocalDate endDate,
                                         String startTime, String endTime, int organizerId) {
    String sql = """
        SELECT COUNT(*) FROM bookings
        WHERE place_name = ?
          AND organizer_id = ?
          AND start_date <= ?
          AND end_date >= ?
          AND start_time <= ?
          AND end_time >= ?
    """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, placeName);
        ps.setInt(2, organizerId);
        ps.setDate(3, java.sql.Date.valueOf(endDate));
        ps.setDate(4, java.sql.Date.valueOf(startDate));
        ps.setString(5, endTime);   // end_time as string
        ps.setString(6, startTime); // start_time as string

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

public static List<EventData> loadUserEvents(String username) {
    int organizerId = Session.getOrganizerId(); // get logged-in organizer ID
    return loadUserEvents(username, organizerId);
}

public static List<EventData> loadUserEvents(String username, int organizerId) {
    List<EventData> events = new ArrayList<>();

    String sql = """
        SELECT e.event_id, e.event_name, e.place_name, e.start_date, e.end_date,
               e.start_time, e.end_time, e.Description, e.Color, e.ShowMe,
               e.Visibility, e.AttachedFilePath, e.EventImagePath
        FROM events e
        JOIN organizers o ON e.event_id = o.event_id
        WHERE o.organizer_name = ? AND o.organizer_id = ?
        ORDER BY e.start_date ASC
    """;

    try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://ununqd8usvy0wouy:GmDEehgTBjzyuPRuA8i8@b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc",
            "ununqd8usvy0wouy", "GmDEehgTBjzyuPRuA8i8");
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setInt(2, organizerId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int eventId = rs.getInt("event_id");
            String name = rs.getString("event_name");
            String location = rs.getString("place_name");
            LocalDate startDate = rs.getDate("start_date").toLocalDate();
            LocalDate endDate = rs.getDate("end_date").toLocalDate();
            String startTime = rs.getString("start_time");
            String endTime = rs.getString("end_time");
            String description = rs.getString("Description");
            String color = rs.getString("Color");
            String showMe = rs.getString("ShowMe");
            String visibility = rs.getString("Visibility");
            String attachedFilePath = rs.getString("AttachedFilePath");
            String eventImagePath = rs.getString("EventImagePath");
            long durationDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

            // Load all organizers for this event
            List<Organizer> organizers = new ArrayList<>();
            try (PreparedStatement orgPs = conn.prepareStatement(
                    "SELECT organizer_name, organizer_id FROM organizers WHERE event_id = ?")) {
                orgPs.setInt(1, eventId);
                ResultSet orgRs = orgPs.executeQuery();
                while (orgRs.next()) {
                    organizers.add(new Organizer(orgRs.getString("organizer_name"),
                                                 orgRs.getInt("organizer_id")));
                }
            }

            events.add(new EventData(
                    name, startDate, endDate, startTime, endTime,
                    location, description, organizers,
                    color, showMe, visibility,
                    attachedFilePath != null ? new File(attachedFilePath) : null,
                    eventImagePath != null ? new File(eventImagePath) : null,
                    durationDays
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return events;
}







}

