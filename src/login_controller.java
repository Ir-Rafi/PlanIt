import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class login_controller {

    @FXML private AnchorPane Container;

    // Login Pane
    @FXML private Pane leftpane;
    @FXML private Pane rightpane;

    // Register Pane
    @FXML private Pane leftpane1;
    @FXML private Pane rightpane1;

    // Login controls
    @FXML private TextField user;
    @FXML private PasswordField pass;
    @FXML private Button loginBtn;
    @FXML private Button gotoRegBtn;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Button forgotPass;
    @FXML private Label userError;
    @FXML private Label passError;

    // Register controls
    @FXML private TextField userReg;
    @FXML private TextField userMail;
    @FXML private PasswordField passReg;
    @FXML private Button gotoLoginBtn;
    @FXML private Button regBtn;
    @FXML private CheckBox termsCheck;
    @FXML private Label userRegError;
    @FXML private Label emailRegError;
    @FXML private Label passRegError;
    @FXML private Label termsError;

    @FXML
    public void initialize() {
        // Show login at start
        leftpane.setVisible(true);
        rightpane.setVisible(true);
        leftpane1.setVisible(false);
        rightpane1.setVisible(false);

        // Button actions
        gotoRegBtn.setOnAction(e -> slideToRegister());
        gotoLoginBtn.setOnAction(e -> slideToLogin());

        loginBtn.setOnAction(e -> handleLogin());
        regBtn.setOnAction(e -> handleRegister());
    }

    private void slideToRegister() {
        leftpane.setVisible(false); rightpane.setVisible(false);
        leftpane.setManaged(false); rightpane.setManaged(false);

        leftpane1.setVisible(true); rightpane1.setVisible(true);
        leftpane1.setManaged(true); rightpane1.setManaged(true);

        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(0.5), leftpane1);
        slideLeft.setToX(-1366);
        TranslateTransition slideRight = new TranslateTransition(Duration.seconds(0.5), rightpane1);
        slideRight.setToX(1366);

        slideLeft.play();
        slideRight.play();
    }

    public void slideToLogin() {
        leftpane1.setVisible(false); rightpane1.setVisible(false);
        leftpane1.setManaged(false); rightpane1.setManaged(false);

        leftpane.setVisible(true); rightpane.setVisible(true);
        leftpane.setManaged(true); rightpane.setManaged(true);

        leftpane1.setTranslateX(0); rightpane1.setTranslateX(0);

        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(0.5), leftpane);
        slideLeft.setFromX(683); slideLeft.setToX(0);
        TranslateTransition slideRight = new TranslateTransition(Duration.seconds(0.5), rightpane);
        slideRight.setFromX(-683); slideRight.setToX(0);

        slideLeft.play();
        slideRight.play();
    }

    private void handleLogin() {
        String username = user.getText();
        String password = pass.getText();

        // Reset errors
        userError.setText(""); passError.setText("");

        boolean hasError = false;
        if (username.isEmpty()) { userError.setText("Please enter username"); hasError = true; }
        if (password.isEmpty()) { passError.setText("Please enter password"); hasError = true; }

        if (!hasError) {
            // Assume login is successful, proceed to the next screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Hello5.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) loginBtn.getScene().getWindow();
                Scene scene = new Scene(root, 1280, 720);
                scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Event Management App");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRegister() {
        String username = userReg.getText();
        String email = userMail.getText();
        String password = passReg.getText();
        boolean agreed = termsCheck.isSelected();

        // Reset error labels
        userRegError.setText("");
        emailRegError.setText("");
        passRegError.setText("");
        termsError.setText("");

        boolean hasError = false;

        if (username.isEmpty()) { userRegError.setText("Please enter username"); hasError = true; }
        if (email.isEmpty()) { emailRegError.setText("Please enter email"); hasError = true; }
        if (password.isEmpty()) { passRegError.setText("Please enter password"); hasError = true; }
        if (!agreed) { termsError.setText("You must agree to the terms"); hasError = true; }

        if (!hasError) {
            // Registration success, transition to login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Form.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) regBtn.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("form.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Registration Form");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Slide back to the login page after registration
            slideToLogin();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}
