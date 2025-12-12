import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class DashboardController extends Controller {

    @FXML
    private VBox sidebarPane;
    @FXML
    private VBox mainContent;
    @FXML
    private VBox rightPane;
    @FXML
    private VBox navMenu;

    @FXML
    private Label nameLabel, emailLabel, deptLabel, sessionLabel, idLabel;
    @FXML
    private StackPane profilePicStack;

    @FXML
    private FlowPane eventsListBox;
    @FXML
    private ScrollPane eventsScroll;

    @FXML
    private Button dashboardBtn, editProfileBtn, eventPortalBtn, changePasswordBtn, signOutBtn;

    private String loggedInUsername;
    private boolean editingProfile = false;

    private TextField nameField, emailField, deptField, sessionField, idField;
    private Button saveBtn;
    private ImageView profileImageView;

    private ProgressIndicator loadingIndicator;

    // ==========================================================
    // INITIALIZE
    // ==========================================================
    @FXML
    public void initialize() {

        if (mainContent != null) {
            mainContent.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }

        if (nameLabel != null) {
            nameLabel.setText("Loading...");
        }
        if (emailLabel != null) {
            emailLabel.setText("Loading.. .");
        }
        if (deptLabel != null) {
            deptLabel.setText("Loading...");
        }
        if (sessionLabel != null) {
            sessionLabel.setText("Loading.. .");
        }
        if (idLabel != null) {
            idLabel.setText("Loading...");
        }

        if (eventsListBox != null) {
            Label loadingEvents = new Label("Loading events...");
            loadingEvents.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
            eventsListBox.getChildren().setAll(loadingEvents);
        }

        // Set dashboard button as active by default
        setActiveNavButton(dashboardBtn);

        Platform.runLater(() -> {
            setupProfilePic();

            if (profilePicStack != null) {
                profilePicStack.setOnMouseClicked(this::onProfilePicClicked);
            }

            if (loggedInUsername != null) {
                loadUserProfileAsync();
                buildEventListAsync();
            }
        });
    }

    private void setupProfilePic() {
        if (profilePicStack == null)
            return;

        // Create default profile image with gradient background
        StackPane defaultPic = new StackPane();
        defaultPic.setPrefSize(45, 45);
        defaultPic.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #8B5CF6, #EC4899); -fx-background-radius: 50;");

        Label initialLabel = new Label("U");
        initialLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        defaultPic.getChildren().add(initialLabel);

        Circle clip = new Circle(22.5, 22.5, 22.5);
        defaultPic.setClip(clip);

        profilePicStack.getChildren().setAll(defaultPic);
    }

    // ==========================================================
    // LOADING INDICATOR
    // ==========================================================
    private void showLoadingIndicator(Pane container, String message) {
        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(20));

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(40, 40);
        progress.setStyle("-fx-progress-color: #8B5CF6;");

        Label loadingLabel = new Label(message);
        loadingLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");

        loadingBox.getChildren().addAll(progress, loadingLabel);
        container.getChildren().setAll(loadingBox);
    }

    // ==========================================================
    // SET LOGGED-IN USER
    // ==========================================================
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;

        Platform.runLater(() -> {
            loadUserProfileAsync();
            buildEventListAsync();
        });
    }

    // ==========================================================
    // NAVIGATION HANDLERS
    // ==========================================================
    @FXML
    private void onDashboardClick() {
        setActiveNavButton(dashboardBtn);
        buildEventListAsync();
    }

    @FXML
    private void onEditProfileClick() {
        setActiveNavButton(editProfileBtn);
        if (!editingProfile)
            enableProfileEditing();
    }

    @FXML
    private void onEventPortalClick() {
        setActiveNavButton(eventPortalBtn);
        Stage stage = getStage();
        try {
            new after_login().openEventPortal(stage, (stage != null ? stage.getScene() : null));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Event Portal not available: " + e.getMessage());
        }
    }

    @FXML
    private void onChangePasswordClick() {
        setActiveNavButton(changePasswordBtn);
        Stage stage = getStage();
        if (stage != null)
            showPasswordChangeDialog(stage);
        else
            showAlert("Cannot change password: no window found");
    }

    @FXML
    private void onSignOutClick() {
        DashboardController.loadingLoginPage(getStage());
    }

    private void setActiveNavButton(Button activeBtn) {
        // Remove active class from all nav buttons
        if (dashboardBtn != null)
            dashboardBtn.getStyleClass().remove("nav-button-active");
        if (editProfileBtn != null)
            editProfileBtn.getStyleClass().remove("nav-button-active");
        if (eventPortalBtn != null)
            eventPortalBtn.getStyleClass().remove("nav-button-active");
        if (changePasswordBtn != null)
            changePasswordBtn.getStyleClass().remove("nav-button-active");

        // Add active class to selected button
        if (activeBtn != null && !activeBtn.getStyleClass().contains("nav-button-active")) {
            activeBtn.getStyleClass().add("nav-button-active");
        }
    }

    private Stage getStage() {
        if (mainContent != null && mainContent.getScene() != null) {
            return (Stage) mainContent.getScene().getWindow();
        }
        return null;
    }

    private void logout(Stage stage) {
        Platform.exit();
        System.exit(0);
    }

    // ==========================================================
    // PROFILE LOADING
    // ==========================================================
    private void loadUserProfileAsync() {
        if (loggedInUsername == null)
            return;

        Task<String[]> task = new Task<String[]>() {
            @Override
            protected String[] call() throws Exception {
                // This runs on background thread
                return DatabaseUtility.getUserDetails(loggedInUsername);
            }
        };

        task.setOnSucceeded(e -> {
            // This runs on JavaFX Application Thread
            String[] details = task.getValue();
            if (details != null && details.length >= 4) {
                setProfile(details[0], details[1], details[2], details[3]);

                // Fade in profile info
                fadeInNode(rightPane);
            }
        });

        task.setOnFailed(e -> {
            System.out.println("Could not load user profile: " + task.getException().getMessage());
            Platform.runLater(() -> {
                if (nameLabel != null)
                    nameLabel.setText("Error loading profile");
            });
        });

        // Start the background thread
        new Thread(task).start();
    }

    public void setProfile(String name, String email, String dept, String session) {
        if (nameLabel != null)
            nameLabel.setText(Session.getUserName());
        if (emailLabel != null)
            emailLabel.setText(email);
        if (deptLabel != null)
            deptLabel.setText(dept);
        if (sessionLabel != null)
            sessionLabel.setText(session);
        if (idLabel != null)
            idLabel.setText(String.valueOf(Session.getUserId()));

        // Update profile initial
        if (profilePicStack != null && name != null && !name.isEmpty()) {
            StackPane defaultPic = new StackPane();
            defaultPic.setPrefSize(45, 45);
            defaultPic.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #8B5CF6, #EC4899); -fx-background-radius: 50;");

            Label initialLabel = new Label(name.substring(0, 1).toUpperCase());
            initialLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            defaultPic.getChildren().add(initialLabel);

            Circle clip = new Circle(22.5, 22.5, 22.5);
            defaultPic.setClip(clip);

            profilePicStack.getChildren().setAll(defaultPic);
        }
    }

    // ==========================================================
    // PROFILE EDIT
    // ==========================================================
    private void enableProfileEditing() {
        editingProfile = true;

        nameField = new TextField(nameLabel != null ? nameLabel.getText() : "");
        emailField = new TextField(emailLabel != null ? emailLabel.getText() : "");
        deptField = new TextField(deptLabel != null ? deptLabel.getText() : "");
        sessionField = new TextField(sessionLabel != null ? sessionLabel.getText() : "");

        String fieldStyle = "-fx-text-fill: white; -fx-background-color: #2D2D3A; -fx-border-color: #3D3D4A; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5;";
        nameField.setStyle(fieldStyle);
        emailField.setStyle(fieldStyle);
        deptField.setStyle(fieldStyle);
        sessionField.setStyle(fieldStyle);

        replace(nameLabel, nameField);
        replace(emailLabel, emailField);
        replace(deptLabel, deptField);
        replace(sessionLabel, sessionField);

        saveBtn = new Button("💾 Save");
        saveBtn.setStyle(
                "-fx-background-color: #8B5CF6; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand; -fx-background-radius: 5;");
        saveBtn.setOnAction(e -> saveProfileAsync());
        if (rightPane != null)
            rightPane.getChildren().add(1, saveBtn);
    }

    private void saveProfileAsync() {
        // Disable save button to prevent double-clicks
        if (saveBtn != null)
            saveBtn.setDisable(true);

        final String name = nameField.getText();
        final String email = emailField.getText();
        final String dept = deptField.getText();
        final String session = sessionField.getText();

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return DatabaseUtility.updateUserProfile(
                        loggedInUsername, name, email, dept, session);
            }
        };

        task.setOnSucceeded(e -> {
            boolean success = task.getValue();

            if (success) {
                setProfile(name, email, dept, session);
                showAlert("Profile updated!");
            } else {
                showAlert("Update failed!");
            }

            restore(nameLabel, nameField);
            restore(emailLabel, emailField);
            restore(deptLabel, deptField);
            restore(sessionLabel, sessionField);

            if (rightPane != null)
                rightPane.getChildren().remove(saveBtn);
            editingProfile = false;
            setActiveNavButton(dashboardBtn);
        });

        task.setOnFailed(e -> {
            System.out.println("Profile update failed: " + task.getException().getMessage());
            showAlert("Update failed!");
            if (saveBtn != null)
                saveBtn.setDisable(false);
        });

        new Thread(task).start();
    }

    private void replace(Label lbl, TextField tf) {
        if (lbl == null || tf == null)
            return;
        Pane parent = (Pane) lbl.getParent();
        if (parent == null)
            return;
        int index = parent.getChildren().indexOf(lbl);
        if (index >= 0)
            parent.getChildren().set(index, tf);
    }

    private void restore(Label lbl, TextField tf) {
        if (lbl == null || tf == null)
            return;
        Pane parent = (Pane) tf.getParent();
        if (parent == null)
            return;
        int index = parent.getChildren().indexOf(tf);
        if (index >= 0)
            parent.getChildren().set(index, lbl);
    }

    // ==========================================================
    // PASSWORD CHANGE
    // ==========================================================
    private void showPasswordChangeDialog(Stage stage) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("Change Password");

        DialogPane dialogPane = dlg.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1A1A2E;");

        GridPane gp = new GridPane();
        gp.setHgap(12);
        gp.setVgap(12);
        gp.setStyle("-fx-padding: 20;");

        PasswordField oldPass = new PasswordField();
        PasswordField newPass = new PasswordField();

        String fieldStyle = "-fx-background-color: #2D2D3A; -fx-text-fill: white; -fx-border-color: #3D3D4A; -fx-border-radius: 5; -fx-background-radius: 5;";
        oldPass.setStyle(fieldStyle);
        newPass.setStyle(fieldStyle);

        Label oldLabel = new Label("Old Password:");
        Label newLabel = new Label("New Password:");
        oldLabel.setStyle("-fx-text-fill: white;");
        newLabel.setStyle("-fx-text-fill: white;");

        gp.addRow(0, oldLabel, oldPass);
        gp.addRow(1, newLabel, newPass);

        dialogPane.setContent(gp);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dlg.showAndWait();
        result.ifPresent(res -> {
            if (res == ButtonType.OK) {
                changePasswordAsync(oldPass.getText(), newPass.getText());
            }
        });
    }

    private void changePasswordAsync(String oldPassword, String newPassword) {
        Task<Boolean> verifyTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return DatabaseUtility.checkUserExists(loggedInUsername, oldPassword);
            }
        };

        verifyTask.setOnSucceeded(e -> {
            if (verifyTask.getValue()) {
                Task<Boolean> updateTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return DatabaseUtility.updatePassword(loggedInUsername, newPassword);
                    }
                };

                updateTask.setOnSucceeded(event -> {
                    if (updateTask.getValue()) {
                        showAlert("Password updated!");
                    } else {
                        showAlert("Update failed!");
                    }
                });

                updateTask.setOnFailed(event -> {
                    showAlert("Update failed!");
                });

                new Thread(updateTask).start();
            } else {
                showAlert("Incorrect old password!");
            }
        });

        verifyTask.setOnFailed(e -> {
            showAlert("Verification failed!");
        });

        new Thread(verifyTask).start();
    }

    // ==========================================================
    // PROFILE PIC
    // ==========================================================
    private void onProfilePicClicked(MouseEvent e) {
        if (mainContent == null)
            return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Profile Picture");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File f = fc.showOpenDialog(mainContent.getScene().getWindow());

        if (f == null)
            return;

        Image img = new Image(f.toURI().toString(), 45, 45, true, true);
        ImageView iv = new ImageView(img);

        iv.setFitWidth(45);
        iv.setFitHeight(45);

        Circle clip = new Circle(22.5, 22.5, 22.5);
        iv.setClip(clip);

        if (profilePicStack != null)
            profilePicStack.getChildren().setAll(iv);
    }

    // ==========================================================
    // EVENTS
    // ==========================================================
    private void buildEventListAsync() {
        if (eventsListBox == null)
            return;

        // Show loading indicator
        showLoadingIndicator(eventsListBox, "Loading events...");

        Task<List<?>> task = new Task<List<?>>() {
            @Override
            protected List<?> call() throws Exception {
                // This runs on background thread
                return EventController.loadUserEvents(loggedInUsername);
            }
        };

        task.setOnSucceeded(e -> {
            // This runs on JavaFX Application Thread
            List<?> events = task.getValue();
            eventsListBox.getChildren().clear();

            if (events == null || events.isEmpty()) {
                Label empty = new Label("No events found");
                empty.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
                eventsListBox.getChildren().add(empty);
            } else {
                for (Object evObj : events) {
                    VBox card = createEventCard(evObj);
                    if (card != null) {
                        // Add with fade-in animation
                        card.setOpacity(0);
                        eventsListBox.getChildren().add(card);
                        fadeInNode(card);
                    }
                }
            }
        });

        task.setOnFailed(e -> {
            System.out.println("Could not load events: " + task.getException().getMessage());
            Platform.runLater(() -> {
                eventsListBox.getChildren().clear();
                Label error = new Label("Error loading events");
                error.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
                eventsListBox.getChildren().add(error);
            });
        });

        // Start the background thread
        new Thread(task).start();
    }

    private void fadeInNode(javafx.scene.Node node) {
        if (node == null)
            return;
        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    // ==========================================================
    // EVENT CARD - MODERN DESIGN
    // ==========================================================
    private VBox createEventCard(Object evObj) {
        if (evObj == null)
            return null;

        String evName = extractString(evObj, new String[] { "name", "title", "eventName" });
        String evCategory = extractString(evObj, new String[] { "category", "type", "eventType" });
        String evDate = extractString(evObj, new String[] { "date", "eventDate", "startDate" });
        Object evId = extractObject(evObj, new String[] { "id", "eventId", "eid" });

        VBox card = new VBox();
        card.setPrefSize(280, 140);
        card.setMinSize(280, 140);
        card.setMaxSize(280, 140);
        card.getStyleClass().add("event-card");
        card.setSpacing(8);
        card.setPadding(new Insets(20));

        // Event Title
        Label title = new Label(evName != null ? evName : "Untitled");
        title.getStyleClass().add("event-card-title");
        title.setWrapText(true);
        title.setMaxWidth(240);

        // Category Badge
        HBox categoryBox = new HBox();
        categoryBox.setAlignment(Pos.CENTER_LEFT);

        Label categoryLabel = new Label(evCategory != null ? evCategory : "General");
        categoryLabel.getStyleClass().add("category-badge");
        categoryBox.getChildren().add(categoryLabel);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom row with date and arrow
        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setSpacing(10);

        Label dateLabel = new Label(evDate != null ? evDate : "No date");
        dateLabel.getStyleClass().add("event-card-date");

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        Button arrowBtn = new Button("→");
        arrowBtn.getStyleClass().add("arrow-button");
        arrowBtn.setOnAction(e -> {
            // Handle event click - open event details
            openOrganizerViewBasedOnRole(evObj);
        });

        bottomRow.getChildren().addAll(dateLabel, bottomSpacer, arrowBtn);

        card.getChildren().addAll(title, categoryBox, spacer, bottomRow);

        // Check if user is creator for delete option
        boolean isCreator = false;
        try {
            String creator = extractString(evObj,
                    new String[] { "creator", "organizer", "owner", "createdBy", "author" });
            if (creator != null && loggedInUsername != null
                    && creator.trim().equalsIgnoreCase(loggedInUsername.trim())) {
                isCreator = true;
            }
        } catch (Exception ex) {
            System.out.println("Could not check event creator: " + ex.getMessage());
        }

        if (isCreator) {
            // Add context menu for delete
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Delete Event");
            deleteItem.setOnAction(e -> deleteEventAsync(evId, evObj, card));
            contextMenu.getItems().add(deleteItem);

            card.setOnContextMenuRequested(e -> contextMenu.show(card, e.getScreenX(), e.getScreenY()));
        }

        return card;
    }

    private void deleteEventAsync(Object evId, Object evObj, VBox card) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Delete Event?");
        String name = extractString(evObj, new String[] { "name", "title", "eventName" });
        a.setContentText("Are you sure you want to delete: " + (name != null ? name : "this event") + "?");

        DialogPane dialogPane = a.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1A1A2E;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");

        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.OK)
            return;

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (evId != null) {
                    return EventController.deleteEvent((Integer) evId);
                } else {
                    return EventController.deleteEvent((Integer) evObj);
                }
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                if (eventsListBox != null)
                    eventsListBox.getChildren().remove(card);
                showAlert("Event deleted!");
            } else {
                showAlert("Failed to delete event.");
            }
        });

        task.setOnFailed(e -> {
            System.out.println("Delete failed: " + task.getException().getMessage());
            showAlert("Failed to delete event.");
        });

        new Thread(task).start();
    }

    // ==========================================================
    // REFLECTION HELPERS
    // ==========================================================
    private String extractString(Object obj, String[] candidates) {
        Object val = extractObject(obj, candidates);
        return val != null ? val.toString() : null;
    }

    private Object extractObject(Object obj, String[] candidates) {
        if (obj == null)
            return null;
        Class<?> cls = obj.getClass();

        for (String c : candidates) {
            try {
                java.lang.reflect.Field f = cls.getField(c);
                Object v = f.get(obj);
                if (v != null)
                    return v;
            } catch (NoSuchFieldException ignored) {
            } catch (Throwable t) {
            }
        }

        for (String c : candidates) {
            try {
                java.lang.reflect.Field f = cls.getDeclaredField(c);
                f.setAccessible(true);
                Object v = f.get(obj);
                if (v != null)
                    return v;
            } catch (NoSuchFieldException ignored) {
            } catch (Throwable t) {
            }
        }

        for (String c : candidates) {
            String capital = c.substring(0, 1).toUpperCase() + (c.length() > 1 ? c.substring(1) : "");
            String[] methods = new String[] { "get" + capital, "is" + capital, c };
            for (String mName : methods) {
                try {
                    Method m = cls.getMethod(mName);
                    Object v = m.invoke(obj);
                    if (v != null)
                        return v;
                } catch (NoSuchMethodException ignored) {
                } catch (Throwable t) {
                }
            }
        }

        return null;
    }

    // ==========================================================
    // UTIL
    // ==========================================================
    private void showAlert(String msg) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
                alert.setHeaderText(null);

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #1A1A2E;");

                alert.show();
            } catch (Throwable t) {
                System.out.println("Alert: " + msg);
            }
        });
    }

    private void openOrganizerViewBasedOnRole(Object evObj) {
        Stage stage = getStage();
        if (stage == null) {
            showAlert("Cannot open organizer view: no window found");
            return;
        }

        // Get current scene to pass as eventsScene parameter
        javafx.scene.Scene currentScene = stage.getScene();

        String organizerName = Session.getUserName();
        int organizerId = Session.getUserId();

        // Extract event ID from the event object
        Object evIdObj = extractObject(evObj, new String[] { "id", "eventId", "eid" });
        int eventId = -1;
        if (evIdObj instanceof Integer) {
            eventId = (Integer) evIdObj;
        }

        System.out.println("Opening organizer view for event ID: " + eventId + ", User: " + organizerName + ", ID: "
                + organizerId);

        // Check if user is the main organizer of THIS event (creator)
        try {
            OrganizerViewFactory.createOrganizerView(
                    stage,
                    currentScene,
                    organizerName,
                    organizerId,
                    eventId,
                    new DashboardContext());

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Failed to open organizer view: " + ex.getMessage());
        }
    }

}