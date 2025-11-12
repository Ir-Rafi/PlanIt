package com.example.dashboard;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Optional;

public class DashboardController {

    @FXML private BorderPane rootPane;
    @FXML private VBox leftPane;
    @FXML private Label nameLabel, emailLabel, deptLabel, sessionLabel;
    @FXML private StackPane profilePicStack;
    @FXML private Pane slideMenuPane;
    @FXML private VBox eventsListBox;

    private final int MENU_WIDTH = 260;
    private boolean menuVisible = false;
    private Label[] eventLabels = new Label[5];
    private int nextEventIndex = 0;

    private boolean editingProfile = false;

    // Keep references to the TextFields when editing
    private TextField nameField, emailField, deptField, sessionField;
    private Button saveBtn;
    private Label profileInitialLabel;


    public void initialize() {
        
        setProfile("Test User", "test@mail.com", "CSE", "2021-22");
        buildSlideMenu();
 // Colors for each event box
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

        profilePicStack.setOnMouseClicked(this::onProfilePicClicked);
    }

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
                "Create / Join Event",
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
                    // Turn all labels into editable TextFields
                    nameField = new TextField(nameLabel.getText());
                    emailField = new TextField(emailLabel.getText());
                    deptField = new TextField(deptLabel.getText());
                    sessionField = new TextField(sessionLabel.getText());

                    replaceLabelWithField(nameLabel, nameField);
                    replaceLabelWithField(emailLabel, emailField);
                    replaceLabelWithField(deptLabel, deptField);
                    replaceLabelWithField(sessionLabel, sessionField);

                    // Add a small Save button below profile info
                    saveBtn = new Button("💾 Save");
                    saveBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                    saveBtn.setOnAction(ev -> {
                        // Update profile initial after editing name
                        if (!nameField.getText().isEmpty()) {
                            profileInitialLabel.setText(nameField.getText().substring(0,1).toUpperCase());
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
                break;

            case "Create / Join Event":
                TextInputDialog tid = new TextInputDialog();
                tid.initOwner(stage);
                tid.setTitle("Create / Join Event");
                tid.setHeaderText(null);
                tid.setContentText("Enter event name:");
                Optional<String> evName = tid.showAndWait();
                evName.ifPresent(name -> addEvent(name.trim()));
                break;

            case "Change Password":
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
                break;

            case "Sign Out":
                stage.close();
                new Alert(Alert.AlertType.INFORMATION, "Signed Out", ButtonType.OK).showAndWait();
                break;

            default:
                new Alert(Alert.AlertType.INFORMATION, opt + " clicked", ButtonType.OK).showAndWait();
        }
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
}


//javac --module-path "C:\Users\HP\Downloads\openjfx-25.0.1_windows-x64_bin-sdk\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml -d out src/main/java/com/example/dashboard/*.java       
// java --module-path "C:\Users\HP\Downloads\openjfx-25.0.1_windows-x64_bin-sdk\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp out com.example.dashboard.Main