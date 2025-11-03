

import javafx.animation.Interpolator;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Controller {

    @FXML private AnchorPane Container;
    @FXML private Pane forgotPane;

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
    @FXML private Button forgotPassBtn;
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

    //forgotPass
    @FXML private Button changePass;
    @FXML private PasswordField forgotPass;
    @FXML private TextField forgotUser;

    @FXML
    public void initialize() {
        // Show login at start
        leftpane.setVisible(true);
        rightpane.setVisible(true);
        leftpane1.setVisible(false);
        rightpane1.setVisible(false);

            // Hide forgotPane initially (hidden below screen)
    forgotPane.setVisible(true);          // make sure it can animate
    forgotPane.setTranslateY(960);        // start hidden


        //rememebered user check
            String rememberedUser = RememberMeUtility.getRememberedUser();
            if (rememberedUser != null) {
                user.setText(rememberedUser);
                rememberMeCheck.setSelected(true);
            }

        // Button actions
        gotoRegBtn.setOnAction(e -> slideToRegister());
        gotoLoginBtn.setOnAction(e -> slideToLogin());

        loginBtn.setOnAction(e -> handleLogin());
        regBtn.setOnAction(e -> handleRegister());
        


        forgotPassBtn.setOnAction(e -> showForgotPane());
        changePass.setOnAction(e -> handleChangePassword());


        
    }

    private void slideToRegister() {
        leftpane.setVisible(false); rightpane.setVisible(false);
        leftpane.setManaged(false); rightpane.setManaged(false);

        leftpane1.setVisible(true); rightpane1.setVisible(true);
        leftpane1.setManaged(true); rightpane1.setManaged(true);

        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(0.5), leftpane1);
        slideLeft.setToX(-1530);
        TranslateTransition slideRight = new TranslateTransition(Duration.seconds(0.5), rightpane1);
        slideRight.setToX(1530);

        slideLeft.play();
        slideRight.play();
    }

    private void slideToLogin() {
        leftpane1.setVisible(false); rightpane1.setVisible(false);
        leftpane1.setManaged(false); rightpane1.setManaged(false);

        leftpane.setVisible(true); rightpane.setVisible(true);
        leftpane.setManaged(true); rightpane.setManaged(true);

        leftpane1.setTranslateX(0); rightpane1.setTranslateX(0);

        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(0.5), leftpane);
        slideLeft.setFromX(765); slideLeft.setToX(0);
        TranslateTransition slideRight = new TranslateTransition(Duration.seconds(0.5), rightpane);
        slideRight.setFromX(-765); slideRight.setToX(0);

        slideLeft.play();
        slideRight.play();
    }

    private void handleLogin() {
        String username = user.getText();
        String password = pass.getText();
        boolean rememberMe = rememberMeCheck.isSelected();

        // Reset errors
        userError.setText(""); passError.setText("");

        boolean hasError = false;
        if (username.isEmpty()) { userError.setText("Please enter username"); hasError = true; }
        if (password.isEmpty()) { passError.setText("Please enter password"); hasError = true; }

        //*new
        if (!hasError) {
        // Check credentials in the database
                if (DatabaseUtility.checkUserExists(username, password)) {
                
                    // ✅ Save remember-me status
                if (rememberMe)
                    RememberMeUtility.saveUser(username);
                else
                    RememberMeUtility.clearRememberedUser();
             showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back, " + username + "!");
                    // Here, you can also implement 'Remember Me' logic
                    // E.g., store username/password/token in a file, or session cookie
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                }
        //*/
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
        else if (!isValidEmail(email)) { 
            emailRegError.setText("Invalid email format"); 
            hasError = true; 
        }
        if (password.isEmpty()) { passRegError.setText("Please enter password"); hasError = true; }
        if (!agreed) { termsError.setText("You must agree to the terms"); hasError = true; }

        if (!hasError) {
        // Register user in the database
        if (DatabaseUtility.registerUser(username, email, password)) {
             showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Welcome, " + username + "!");
            slideToLogin();  // After successful registration, switch to the login screen
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username or email may already be taken.");
        }
    }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(emailRegex);
    }

private void showForgotPane() {
    TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), forgotPane);
    slideUp.setToY(0);                        // slide to visible position
    slideUp.setInterpolator(Interpolator.EASE_BOTH);
    slideUp.play();
}

private void hideForgotPane() {
    TranslateTransition slideDown = new TranslateTransition(Duration.millis(400), forgotPane);
    slideDown.setToY(960);                     // slide back down
    slideDown.setInterpolator(Interpolator.EASE_BOTH);
    slideDown.play();
}

private void handleChangePassword() {
    String username = forgotUser.getText();
    String newPassword = forgotPass.getText();

    

    if (username.isEmpty() || newPassword.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
        return;
    }

    if (!DatabaseUtility.userExists(username)) {
        showAlert(Alert.AlertType.ERROR, "Error", "Username not found.");
        return;
    }

    if (DatabaseUtility.updatePassword(username, newPassword)) {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully!");
        hideForgotPane();     // slide it back down
        forgotUser.clear();
        forgotPass.clear();
    } else {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password.");
    }
}


}