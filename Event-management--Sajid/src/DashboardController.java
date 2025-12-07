import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class DashboardController {

    @FXML private BorderPane rootPane;

    @FXML private StackPane slideMenuPane;
    @FXML private Region menuOverlay;
    @FXML private VBox slideMenuBox;

    @FXML private Label nameLabel, emailLabel, deptLabel, sessionLabel;
    @FXML private StackPane profilePicStack;

    @FXML private TilePane eventsListBox;
    @FXML private VBox rightPane;

    @FXML private ScrollPane eventsScroll;

    private boolean menuVisible = false;
    private final int MENU_WIDTH = 280;

    private String loggedInUsername;
    private boolean editingProfile = false;

    private TextField nameField, emailField, deptField, sessionField;
    private Button saveBtn;
    private Label profileInitialLabel;

    // ==========================================================
    // INITIALIZE
    // ==========================================================
    public void initialize() {

        rootPane.setStyle("-fx-background-color: linear-gradient(to bottom, #3a1d54 0%, #2b1a45 40%, #1b162c 100%);");

        nameLabel.setText("Loading...");
        nameLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 18px; -fx-font-weight: bold;");
        emailLabel.setText("Loading...");
        emailLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12px;");
        deptLabel.setText("Loading...");
        deptLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 14px;");
        sessionLabel.setText("Loading...");
        sessionLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 14px;");

        Label loadingEvents = new Label("Loading events...");
        loadingEvents.setStyle("-fx-text-fill: #ccc; -fx-font-size: 14px;");
        eventsListBox.getChildren().setAll(loadingEvents);

        eventsScroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        eventsScroll.setOpaqueInsets(new Insets(0));
        eventsListBox.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        Platform.runLater(() -> {
            slideMenuPane.prefWidthProperty().bind(rootPane.widthProperty());
            slideMenuPane.prefHeightProperty().bind(rootPane.heightProperty());
            menuOverlay.prefWidthProperty().bind(slideMenuPane.widthProperty());
            menuOverlay.prefHeightProperty().bind(slideMenuPane.heightProperty());

            StackPane.setAlignment(slideMenuBox, Pos.CENTER_LEFT);
            slideMenuBox.setPrefWidth(MENU_WIDTH);
            slideMenuBox.setTranslateX(-MENU_WIDTH);

            slideMenuPane.setVisible(false);
            slideMenuPane.setMouseTransparent(true);
            menuOverlay.setVisible(false);

            if (profilePicStack != null)
                profilePicStack.setOnMouseClicked(this::onProfilePicClicked);

            buildMenuButtons();

            if (loggedInUsername != null) {
                loadUserProfile();
                buildEventList();
            }

            rootPane.applyCss();
            rootPane.layout();
        });

        rootPane.getScene().windowProperty().addListener((obs, oldWindow, newWindow) -> {
            if (newWindow != null) {
                newWindow.focusedProperty().addListener((o, oldVal, newVal) -> {
                    if (newVal) buildEventList();
                });
            }
        });
    }

    // ==========================================================
    // SET LOGGED-IN USER
    // ==========================================================
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;

        Platform.runLater(() -> {
            loadUserProfile();
            buildEventList();
            rootPane.applyCss();
            rootPane.layout();
        });
    }

    // ==========================================================
    // MENU BUTTONS
    // ==========================================================
    private void buildMenuButtons() {
        slideMenuBox.getChildren().removeIf(n -> n instanceof Button);

        String[] options = {
                "Help & Support",
                "Sign Out",
                "Change Password",
                "Event Portal",
                "Edit Profile",
                "Dashboard"
        };

        int insertIndex = slideMenuBox.getChildren().size();

        for (String opt : options) {
            Button btn = new Button(opt);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");

            btn.setOnAction(e -> {
                handleMenuAction(opt);
                toggleMenu();
            });

            slideMenuBox.getChildren().add(insertIndex, btn);
            insertIndex++;
        }
    }

    private void handleMenuAction(String opt) {
        Stage stage = (Stage) rootPane.getScene().getWindow();

        switch (opt) {
            case "Dashboard" -> buildEventList();
            case "Edit Profile" -> { if (!editingProfile) enableProfileEditing(); }
            case "Event Portal" -> {
                try { new after_login().openEventPortal(stage, stage.getScene()); }
                catch (Exception e) { e.printStackTrace(); }
            }
            case "Change Password" -> showPasswordChangeDialog(stage);
            case "Sign Out" -> logout(stage);
            default -> showAlert(opt + " clicked");
        }
    }

    private void logout(Stage stage) {
        Platform.exit();
        System.exit(0);
    }

    // ==========================================================
    // MENU TOGGLE
    // ==========================================================
    @FXML private void toggleMenu() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(240), slideMenuBox);

        if (!menuVisible) {
            slideMenuPane.setVisible(true);
            slideMenuPane.setMouseTransparent(false);
            menuOverlay.setVisible(true);

            tt.setToX(0);
            menuVisible = true;

        } else {
            tt.setToX(-MENU_WIDTH);
            tt.setOnFinished(e -> {
                slideMenuPane.setVisible(false);
                slideMenuPane.setMouseTransparent(true);
                menuOverlay.setVisible(false);
            });

            menuVisible = false;
        }

        tt.play();
    }

    // ==========================================================
    // PROFILE LOADING
    // ==========================================================
    private void loadUserProfile() {
        if (loggedInUsername == null) return;

        String[] d = DatabaseUtility.getUserDetails(loggedInUsername);
        if (d != null) setProfile(d[0], d[1], d[2], d[3]);
    }

    public void setProfile(String name, String email, String dept, String session) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        deptLabel.setText("🏫 " + dept);
        sessionLabel.setText("📚 " + session);

        if (profileInitialLabel == null) {
            profileInitialLabel = new Label();
            profileInitialLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");
            profilePicStack.getChildren().setAll(profileInitialLabel);
        }

        profileInitialLabel.setText(name.substring(0, 1).toUpperCase());
    }

    // ==========================================================
    // PROFILE EDIT
    // ==========================================================
    private void enableProfileEditing() {
        editingProfile = true;

        nameField = new TextField(nameLabel.getText());
        emailField = new TextField(emailLabel.getText());
        deptField = new TextField(deptLabel.getText().substring(2));
        sessionField = new TextField(sessionLabel.getText().substring(2));

        replace(nameLabel, nameField);
        replace(emailLabel, emailField);
        replace(deptLabel, deptField);
        replace(sessionLabel, sessionField);

        saveBtn = new Button("💾 Save");
        saveBtn.setOnAction(e -> saveProfile());
        rightPane.getChildren().add(saveBtn);
    }

    private void saveProfile() {
        boolean ok = DatabaseUtility.updateUserProfile(
                loggedInUsername,
                nameField.getText(),
                emailField.getText(),
                deptField.getText(),
                sessionField.getText()
        );

        if (ok) {
            setProfile(nameField.getText(), emailField.getText(), deptField.getText(), sessionField.getText());
            showAlert("Profile updated!");
        } else showAlert("Update failed!");

        restore(nameLabel, nameField);
        restore(emailLabel, emailField);
        restore(deptLabel, deptField);
        restore(sessionLabel, sessionField);

        rightPane.getChildren().remove(saveBtn);
        editingProfile = false;
    }

    private void replace(Label lbl, TextField tf) {
        Pane parent = (Pane) lbl.getParent();
        int index = parent.getChildren().indexOf(lbl);
        parent.getChildren().set(index, tf);
    }

    private void restore(Label lbl, TextField tf) {
        Pane parent = (Pane) tf.getParent();
        int index = parent.getChildren().indexOf(tf);
        parent.getChildren().set(index, lbl);
        lbl.setText(tf.getText());
    }

    // ==========================================================
    // PASSWORD CHANGE
    // ==========================================================
    private void showPasswordChangeDialog(Stage stage) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("Change Password");

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(8);

        PasswordField oldPass = new PasswordField();
        PasswordField newPass = new PasswordField();

        gp.addRow(0, new Label("Old Password:"), oldPass);
        gp.addRow(1, new Label("New Password:"), newPass);

        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                if (DatabaseUtility.checkUserExists(loggedInUsername, oldPass.getText())) {
                    if (DatabaseUtility.updatePassword(loggedInUsername, newPass.getText()))
                        showAlert("Password updated!");
                    else showAlert("Update failed!");
                } else showAlert("Incorrect old password!");
            }
        });
    }

    // ==========================================================
    // PROFILE PIC
    // ==========================================================
    private void onProfilePicClicked(MouseEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Profile Picture");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        File f = fc.showOpenDialog(rootPane.getScene().getWindow());

        if (f == null) return;

        Image img = new Image(f.toURI().toString(), 120, 120, true, true);
        ImageView iv = new ImageView(img);

        iv.setFitWidth(120);
        iv.setFitHeight(120);

        Rectangle clip = new Rectangle(120, 120);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        iv.setClip(clip);

        profilePicStack.getChildren().setAll(iv);
    }

    // ==========================================================
    // EVENTS
    // ==========================================================
    private void buildEventList() {
        eventsListBox.getChildren().clear();

        List<EventController.EventData> list = EventController.loadUserEvents(loggedInUsername);

        if (list == null || list.isEmpty()) {
            Label empty = new Label("No events found");
            empty.setStyle("-fx-text-fill: #ccc; -fx-font-size: 14px;");
            eventsListBox.getChildren().add(empty);
            return;
        }

        for (EventController.EventData ev : list)
            eventsListBox.getChildren().add(createEventCard(ev));
    }

    // ==========================================================
    // UPDATED EVENT CARD WITH BOTTOM-RIGHT DELETE BUTTON
    // ==========================================================
    private StackPane createEventCard(EventController.EventData ev) {

        StackPane card = new StackPane();
        card.setPrefSize(200, 150);
        card.setStyle(
                "-fx-background-color: rgba(58,29,84,0.8);" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 6,0,0,3);"
        );

        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(10));

        Label title = new Label(ev.name);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        content.getChildren().add(title);
        card.getChildren().add(content);

        // ---- DELETE BUTTON AT BOTTOM-RIGHT FOR CREATOR ----
        if (ev.mainOrganizer != null &&
                ev.mainOrganizer.trim().equalsIgnoreCase(loggedInUsername.trim())) {

            Button del = new Button("Delete");
            del.setStyle(
                    "-fx-background-color: #c9302c;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 10px;" +
                    "-fx-padding: 3 6;" +
                    "-fx-background-radius: 5;"
            );

            del.setOnAction(e -> deleteEvent(ev, card));

            StackPane.setAlignment(del, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(del, new Insets(0, 7, 7, 0));

            card.getChildren().add(del);
        }

        return card;
    }

    private void deleteEvent(EventController.EventData ev, StackPane card) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Delete Event?");
        a.setContentText("Are you sure you want to delete: " + ev.name + "?");

        if (a.showAndWait().get() != ButtonType.OK)
            return;

        boolean done = EventController.deleteEvent(ev.id);

        if (done) {
            eventsListBox.getChildren().remove(card);
            showAlert("Event deleted!");
        } else showAlert("Failed to delete event.");
    }

    // ==========================================================
    // UTIL
    // ==========================================================
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}
