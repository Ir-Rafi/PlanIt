import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Duration;

import java.io.IOException;

public class login_controller {

    @FXML
    private AnchorPane Container;
    @FXML
    private Pane forgotPane;
    @FXML
    private ProgressIndicator progressIndicator;

    // Login Pane
    @FXML
    private Pane leftpane;
    @FXML
    private Pane rightpane;

    // Register Pane
    @FXML
    private Pane leftpane1;
    @FXML
    private Pane rightpane1;

    // Login controls
    @FXML
    private TextField user;
    @FXML
    private PasswordField pass;
    @FXML
    private Button loginBtn;
    @FXML
    private Button gotoRegBtn;
    @FXML
    private CheckBox rememberMeCheck;
    @FXML
    private Button forgotPassBtn;
    @FXML
    private Label userError;
    @FXML
    private Label passError;

    // Register controls
    @FXML
    private TextField userReg;
    @FXML
    private TextField userMail;
    @FXML
    private PasswordField passReg;
    @FXML
    private Button gotoLoginBtn;
    @FXML
    private Button regBtn;
    @FXML
    private CheckBox termsCheck;
    @FXML
    private Label userRegError;
    @FXML
    private Label emailRegError;
    @FXML
    private Label passRegError;
    @FXML
    private Label termsError;

    @FXML
    private Button changePass;
    @FXML
    private PasswordField forgotPass;
    @FXML
    private TextField forgotUser;
    @FXML
    private Button Goback;               // the Back button in forgotPane
    @FXML
    private PasswordField ConfiirmPass;  // Confirm Password field
    @FXML
    private Label passMismatch;          // error label for mismatch

    @FXML private TextField firstNameReg;
    @FXML private TextField lastNameReg;
    @FXML private TextField departmentReg;
    @FXML private TextField sessionReg;

    @FXML private Label firstNameError;
    @FXML private Label lastNameError;
    @FXML private Label departmentError;
    @FXML private Label sessionError;

    @FXML
    public void initialize() {
        // Show login at start
        leftpane.setVisible(true);
        rightpane.setVisible(true);
        leftpane1.setVisible(false);
        rightpane1.setVisible(false);

        forgotPane.setVisible(true); // make sure it can animate
        forgotPane.setTranslateY(768); // start hidden

        // rememebered user check
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
        Goback.setOnAction(e -> hideForgotPane());
    }

    private void slideToRegister() {
        leftpane.setVisible(false);
        rightpane.setVisible(false);
        leftpane.setManaged(false);
        rightpane.setManaged(false);

        leftpane1.setVisible(true);
        rightpane1.setVisible(true);
        leftpane1.setManaged(true);
        rightpane1.setManaged(true);

        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(0.5), leftpane1);
        slideLeft.setToX(-1366);
        TranslateTransition slideRight = new TranslateTransition(Duration.seconds(0.5), rightpane1);
        slideRight.setToX(1366);

        slideLeft.play();
        slideRight.play();
    }

    public void slideToLogin() {
        leftpane1.setVisible(false);
        rightpane1.setVisible(false);
        leftpane1.setManaged(false);
        rightpane1.setManaged(false);

        leftpane.setVisible(true);
        rightpane.setVisible(true);
        leftpane.setManaged(true);
        rightpane.setManaged(true);

        leftpane1.setTranslateX(0);
        rightpane1.setTranslateX(0);

        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(0.5), leftpane);
        slideLeft.setFromX(683);
        slideLeft.setToX(0);
        TranslateTransition slideRight = new TranslateTransition(Duration.seconds(0.5), rightpane);
        slideRight.setFromX(-683);
        slideRight.setToX(0);

        slideLeft.play();
        slideRight.play();
    }

    private void handleLogin() {
        String username = user.getText();
        String password = pass.getText();
        boolean rememberMe = rememberMeCheck.isSelected();
        // Reset errors
        userError.setText("");
        passError.setText("");

        boolean hasError = false;
        if (username.isEmpty()) {
            userError.setText("Please enter username");
            hasError = true;
        }
        if (password.isEmpty()) {
            passError.setText("Please enter password");
            hasError = true;
        }

        if (!hasError) {
            // Assume login is successful, proceed to the next screen

            if(progressIndicator != null){
                progressIndicator.setVisible(true);
            }
            loginBtn.setDisable(true);

            final String[] dots = {"", ".", "..", "..."};
            final int[] dotIndex = {0};

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
                dotIndex[0] = (dotIndex[0]+1) % dots.length;
                loginBtn.setText("Logging in"+dots[dotIndex[0]]);
            }));

            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            Task<Boolean> loginTask = new Task<Boolean>(){
                @Override
                protected Boolean call() throws Exception{
                    return DatabaseUtility.checkUserExists(username, password);
                }
            };

            loginTask.setOnSucceeded(event -> {
                Boolean success = loginTask.getValue();

                timeline.stop();
                if(progressIndicator!=null){
                    progressIndicator.setVisible(false);
                }

                loginBtn.setDisable(false);
                loginBtn.setText("Login");

                if(success) {
                    int organizerID = DatabaseUtility.getUserId(username);
                    Session.setSession(organizerID, username);

                    if(rememberMe){
                        RememberMeUtility.saveUser(username);
                    }
                    else{
                        RememberMeUtility.clearRememberedUser();
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back, "+username+"!");

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                        Parent root = loader.load();

                        DashboardController controller = loader.getController();
                        controller.setLoggedInUsername(username);  // << send username

                        Stage stage = (Stage) loginBtn.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setMaximized(true);
                        stage.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Username or password");
                }
            });

            loginTask.setOnFailed(event -> {
                // Stop the animation and hide progress indicator
                timeline.stop();
                if (progressIndicator != null) {
                    progressIndicator.setVisible(false);
                }
                loginBtn.setDisable(false);
                loginBtn.setText("Login");

                showAlert(Alert.AlertType.ERROR, "Login Failed", "An error occurred. Please try again.");
                loginTask.getException().printStackTrace();
            });

            // Start the background thread
            Thread thread = new Thread(loginTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void handleRegister() {
        String username = userReg.getText();
        String email = userMail.getText();
        String password = passReg.getText();
        boolean agreed = termsCheck.isSelected();

        String firstName = firstNameReg.getText();
        String lastName = lastNameReg.getText();
        String department = departmentReg.getText();
        String session = sessionReg.getText();

        // Reset error labels
        userRegError.setText("");
        emailRegError.setText("");
        passRegError.setText("");
        termsError.setText("");

        firstNameError.setText("");
        lastNameError.setText("");
        departmentError.setText("");
        sessionError.setText("");

        boolean hasError = false;

        if (firstName.isEmpty()) {
            firstNameError.setText("Please Enter your first name");
            hasError = true;
        }
        if (lastName.isEmpty()) {
            lastNameError.setText("Please Enter your last name");
            hasError = true;
        }
        if (department.isEmpty()) {
            departmentError.setText("Enter your department");
            hasError = true;
        }
        if (session.isEmpty()) {
            sessionError.setText("Enter your session");
            hasError = true;
        }

        if (username.isEmpty()) {
            userRegError.setText("Please enter username");
            hasError = true;
        }
        if (email.isEmpty()) {
            emailRegError.setText("Please enter email");
            hasError = true;
        } else if (!isValidEmail(email)) {
            emailRegError.setText("Invalid email format");
            hasError = true;
        }
        if (password.isEmpty()) {
            passRegError.setText("Please enter password");
            hasError = true;
        }
        if (!agreed) {
            termsError.setText("You must agree to the terms");
            hasError = true;
        }

        if (!hasError) {
            // Show progress indicator and disable button
            if (progressIndicator != null) {
                progressIndicator.setVisible(true);
            }
            regBtn.setDisable(true);
            final String[] dots = {"", ".", "..", "..."};
            final int[] dotIndex = {0};

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
                dotIndex[0] = (dotIndex[0] + 1) % dots.length;
                regBtn.setText("Processing" + dots[dotIndex[0]]);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            // Run database operations on a background thread
            Task<Boolean> registrationTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    // Check if username exists
                    if (DatabaseUtility.userExists(username)) {
                        updateMessage("username_taken");
                        return false;
                    }

                    // Check if email exists
                    if (DatabaseUtility.emailExists(email)) {
                        updateMessage("email_taken");
                        return false;
                    }

                    // Register user
                    boolean success = DatabaseUtility.registerUser(username, email, password);
                    if (success) {
                        updateMessage("success");
                    }
                    return success;
                }
            };

            registrationTask.setOnSucceeded(event -> {
                Boolean success = registrationTask.getValue();
                String message = registrationTask.getMessage();

                // Hide progress indicator and re-enable button
                timeline.stop();
                if (progressIndicator != null) {
                    progressIndicator.setVisible(false);
                }
                regBtn.setDisable(false);
                regBtn.setText("Register");

                if ("username_taken".equals(message)) {
                    userRegError.setText("Username already taken");
                } else if ("email_taken".equals(message)) {
                    emailRegError.setText("Email already registered");
                } else if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Welcome, " + username + "!");

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
                    slideToLogin();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed", "An error occurred during registration.");
                }
            });

            registrationTask.setOnFailed(event -> {
                // Hide progress indicator and re-enable button
                timeline.stop();
                if (progressIndicator != null) {
                    progressIndicator.setVisible(false);
                }
                regBtn.setDisable(false);
                regBtn.setText("Register");

                showAlert(Alert.AlertType.ERROR, "Registration Failed", "An error occurred. Please try again.");
                registrationTask.getException().printStackTrace();
            });

            // Start the background thread
            Thread thread = new Thread(registrationTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(emailRegex);
    }

    private void showForgotPane() {
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), forgotPane);
        slideUp.setToY(0); // slide to visible position
        slideUp.setInterpolator(Interpolator.EASE_BOTH);
        slideUp.play();
    }

    private void hideForgotPane() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(400), forgotPane);
        slideDown.setToY(768); // slide back down
        slideDown.setInterpolator(Interpolator.EASE_BOTH);
        slideDown.play();
    }

    private void handleChangePassword() {
        String username = forgotUser.getText();
        String newPassword = forgotPass.getText();
        String confirmPassword = ConfiirmPass.getText();

        passMismatch.setText("");
        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        if (!DatabaseUtility.userExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username not found.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            passMismatch.setText("Passwords do not match!");
            return;
        }

        if (DatabaseUtility.updatePassword(username, newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully!");
            hideForgotPane(); // slide it back down
            forgotUser.clear();
            forgotPass.clear();
            ConfiirmPass.clear();
            passMismatch.setText("");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password.");
        }
    }
}

