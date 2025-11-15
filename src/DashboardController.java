import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.*;
import java.util.Optional;

public class DashboardController extends Controller {

    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox leftPane;
    @FXML
    private Label nameLabel, emailLabel, deptLabel, sessionLabel;
    @FXML
    private StackPane profilePicStack;
    @FXML
    private Pane slideMenuPane;
    @FXML
    private VBox eventsListBox;

    private final int MENU_WIDTH = 260;
    private boolean menuVisible = false;
    private Label[] eventLabels = new Label[5];
    private int nextEventIndex = 0;

    private boolean editingProfile = false;

    // Editable fields
    private TextField nameField, emailField, deptField, sessionField;
    private Button saveBtn;
    private Label profileInitialLabel;
    private String loggedInUsername;

    // ---------------- INITIALIZATION ----------------
    public void initialize() {
        buildSlideMenu();
        buildEventList();
        profilePicStack.setOnMouseClicked(this::onProfilePicClicked);

        // Wait for JavaFX to finish building the UI, then check username
        Platform.runLater(() -> {
            if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
                System.out.println("🔄 Username already available during init: " + loggedInUsername);
                loadUserProfile();
            } else {
                System.out.println("⏳ Username not yet set — will load once provided.");
            }
        });

        System.out.println("Dashboard initialized, instance hash = " + this.hashCode());
    }

    private void buildEventList() {
        String[] colors = {
                "rgba(200,180,255,0.6)",
                "rgba(180,255,200,0.6)",
                "rgba(255,200,180,0.6)",
                "rgba(200,255,255,0.6)",
                "rgba(255,180,255,0.6)"
        };

        for (int i = 0; i < 5; i++) {
            Label ev = new Label("Event " + (i + 1));
            ev.getStyleClass().add("event-item");
            ev.setMaxWidth(Double.MAX_VALUE);
            ev.setStyle("-fx-background-color: " + colors[i % colors.length] + "; " +
                    "-fx-padding: 12; -fx-border-color: rgba(255,230,180,0.8); " +
                    "-fx-border-width: 0 0 2 0; -fx-background-radius: 2;");
            eventsListBox.getChildren().add(ev);
            eventLabels[i] = ev;
        }
    }

    // ---------------- PROFILE DATA SETTER ----------------
    public void setProfile(String name, String email, String dept, String session) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        deptLabel.setText("🏫 " + dept);
        sessionLabel.setText("📚 Session: " + session);

        // Update profile initial
        if (profileInitialLabel == null) {
            profileInitialLabel = new Label();
            profileInitialLabel.getStyleClass().add("profile-initial");
            profilePicStack.getChildren().clear();
            profilePicStack.getChildren().add(profileInitialLabel);
        }

        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(name.substring(0, 1).toUpperCase());
        }
    }

    // ---------------- SIDE MENU ----------------
    private void buildSlideMenu() {
        slideMenuPane.setPrefWidth(MENU_WIDTH);
        slideMenuPane.setTranslateX(-MENU_WIDTH - 5);
        slideMenuPane.setManaged(false);
        slideMenuPane.setVisible(false);
        slideMenuPane.setStyle("-fx-background-color: transparent;");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setFillWidth(true);
        box.setPrefWidth(MENU_WIDTH);
        box.setStyle("-fx-background-color: #222;");

        String[] options = {
                "Dashboard",
                "Edit Profile",
                "Event Portal",
                "Change Password",
                "Sign Out",
                "Help & Support"
        };

        for (String opt : options) {
            Button btn = new Button(opt);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setMinWidth(MENU_WIDTH - 40);
            btn.setWrapText(false);
            btn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 14px; "
                    + "-fx-padding: 10 20 10 20; -fx-alignment: CENTER_LEFT;");
            btn.setOnAction(e -> {
                handleMenuAction(opt);
                toggleMenu();
            });
            box.getChildren().add(btn);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);

        Button closeBtn = new Button("❌ Close Menu");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 14px;");
        closeBtn.setOnAction(e -> toggleMenu());
        box.getChildren().add(closeBtn);

        slideMenuPane.getChildren().clear();
        slideMenuPane.getChildren().add(box);
    }

    private void handleMenuAction(String opt) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        switch (opt) {
            case "Dashboard":
                new Alert(Alert.AlertType.INFORMATION, "Dashboard refreshed", ButtonType.OK).showAndWait();
                break;

            case "Edit Profile":
                if (!editingProfile) {
                    enableProfileEditing();
                }
                break;

            case "Event Portal":
                try {
                    Stage currentStage = (Stage) stage.getScene().getWindow();
                    after_login event_portal = new after_login();
                    event_portal.openEventPortal(currentStage, stage.getScene());
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Change Password":
                showPasswordChangeDialog(stage);
                break;

            case "Sign Out":
                new Alert(Alert.AlertType.INFORMATION, "Signed Out", ButtonType.OK).showAndWait();
                DashboardController.loadLoginPage(stage);
                break;

            default:
                new Alert(Alert.AlertType.INFORMATION, opt + " clicked", ButtonType.OK).showAndWait();
        }
    }

    private void enableProfileEditing() {
        nameField = new TextField(nameLabel.getText());
        emailField = new TextField(emailLabel.getText());
        deptField = new TextField(deptLabel.getText());
        sessionField = new TextField(sessionLabel.getText());

        replaceLabelWithField(nameLabel, nameField);
        replaceLabelWithField(emailLabel, emailField);
        replaceLabelWithField(deptLabel, deptField);
        replaceLabelWithField(sessionLabel, sessionField);

        saveBtn = new Button("💾 Save");
        saveBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
        saveBtn.setOnAction(ev -> {
            if (!nameField.getText().isEmpty()) {
                profileInitialLabel.setText(nameField.getText().substring(0, 1).toUpperCase());
            }

            saveInlineField(nameLabel, nameField);
            saveInlineField(emailLabel, emailField);
            saveInlineField(deptLabel, deptField);
            saveInlineField(sessionLabel, sessionField);

            ((Pane) saveBtn.getParent()).getChildren().remove(saveBtn);
            editingProfile = false;
        });

        leftPane.getChildren().add(saveBtn);
        editingProfile = true;
    }

    private void showPasswordChangeDialog(Stage stage) {
        Dialog<ButtonType> pwdDialog = new Dialog<>();
        pwdDialog.initOwner(stage);
        pwdDialog.setTitle("Change Password");
        GridPane pg = new GridPane();
        pg.setHgap(8);
        pg.setVgap(8);
        PasswordField oldP = new PasswordField();
        PasswordField newP = new PasswordField();
        pg.addRow(0, new Label("Old Password:"), oldP);
        pg.addRow(1, new Label("New Password:"), newP);
        pwdDialog.getDialogPane().setContent(pg);
        pwdDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        pwdDialog.showAndWait();
    }

    @FXML
    private void toggleMenu() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), slideMenuPane);

        if (!menuVisible) {
            slideMenuPane.setVisible(true);
            tt.setToX(0);
            menuVisible = true;
        } else {
            tt.setToX(-MENU_WIDTH - 5);
            tt.setOnFinished(e -> slideMenuPane.setVisible(false));
            menuVisible = false;
        }

        tt.play();
    }

    private void onProfilePicClicked(MouseEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose profile picture");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fc.showOpenDialog(rootPane.getScene().getWindow());
        if (file != null) {
            javafx.scene.image.Image img = new javafx.scene.image.Image(file.toURI().toString(), 120, 120, true, true);
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(img);
            iv.setFitWidth(120);
            iv.setFitHeight(120);
            profilePicStack.getChildren().clear();
            profilePicStack.getChildren().add(iv);
        }
    }

    public void addEvent(String name) {
        if (nextEventIndex < eventLabels.length) {
            eventLabels[nextEventIndex].setText(name);
            nextEventIndex++;
        } else {
            new Alert(Alert.AlertType.WARNING, "All event slots are full!", ButtonType.OK).showAndWait();
        }
    }

    // ---------------- Inline Editing Helpers ----------------
    private void replaceLabelWithField(Label lbl, TextField tf) {
        Pane parent = (Pane) lbl.getParent();
        int idx = parent.getChildren().indexOf(lbl);
        parent.getChildren().remove(lbl);
        parent.getChildren().add(idx, tf);
    }

    private void saveInlineField(Label lbl, TextField tf) {
        lbl.setText(tf.getText());
        Pane parent = (Pane) tf.getParent();
        int idx = parent.getChildren().indexOf(tf);
        parent.getChildren().remove(tf);
        parent.getChildren().add(idx, lbl);
    }

    // ---------------- LOAD USER PROFILE ----------------
    public void setLoggedInUsername(String username) {
        System.out.println("Dashboard received username: " + username);
        this.loggedInUsername = username;

        // If UI already initialized, load the profile right away
        if (nameLabel != null) {
            Platform.runLater(this::loadUserProfile);
        }
    }

    private void loadUserProfile() {
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            System.out.println("⚠️ Cannot load profile: username is null or empty.");
            return;
        }

        System.out.println("Fetching profile for: " + loggedInUsername);
        String[] details = DatabaseUtility.getUserDetails(loggedInUsername);

        if (details != null) {
            String uname = details[0];
            String email = details[1];
            System.out.println("✅ Loaded from DB → Username: " + uname + ", Email: " + email);
            setProfile(uname, email, "CSE", "2021-22");
        } else {
            System.out.println("⚠️ No user found in database for: " + loggedInUsername);
        }
    }
}
