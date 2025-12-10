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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
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

    
    private boolean dialogShown = false;
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

                termsCheck.setOnMouseClicked(this::showTermsDialog); // Show terms on hover
        
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

        try {
            // Validate FIRST (synchronous, on UI thread) - quick checks only
            if (username.isEmpty()) throw new InvalidInputException("Username cannot be empty.");
            if (password.isEmpty()) throw new InvalidInputException("Password cannot be empty");

            // NOW start animation (before any database calls)
            if(progressIndicator != null){
                progressIndicator.setVisible(true);
            }
            loginBtn.setDisable(true);

            loginBtn.setText("Logging in.");

            final String[] dots = {".", "..", "...", ""};
            final int[] dotIndex = {0};

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(500), e -> {
                        dotIndex[0] = (dotIndex[0] + 1) % dots.length;
                        loginBtn.setText("Logging in" + dots[dotIndex[0]]);
                    })
            );

            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();


            // Background task for all database operations
            Task<Void> loginTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Check credentials in background
                    if (!DatabaseUtility.checkUserExists(username, password)) {
                        throw new InvalidInputException("Invalid username or password");
                    }

                    int organizerId = DatabaseUtility.getUserId(username);
                    Session.setSession(organizerId, username);

                    if (rememberMe) RememberMeUtility.saveUser(username);
                    else RememberMeUtility.clearRememberedUser();

                    return null;
                }
            };

            loginTask.setOnSucceeded(event -> {
                timeline.stop();
                loginBtn.setText("Login");
                loginBtn.setDisable(false);
                if (progressIndicator != null) progressIndicator.setVisible(false);

                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back, " + username + "!");

                // Show loading window
                Stage loadingStage = new Stage();
                loadingStage.setTitle("Loading Dashboard");
                loadingStage.setWidth(500);
                loadingStage.setHeight(300);
                loadingStage.setResizable(false);
                loadingStage.centerOnScreen();

                // Create loading screen UI
                VBox loadingPane = new VBox(15);
                loadingPane.setAlignment(Pos.CENTER);
                loadingPane.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 30;");

                ProgressIndicator spinner = new ProgressIndicator();
                spinner.setPrefSize(80, 80);
                spinner.setStyle("-fx-progress-color: #2E86DE;");

                Label loadingLabel = new Label("Loading Dashboard...");
                loadingLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333; -fx-font-weight: bold;");

                Label statusLabel = new Label("Fetching your data...");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

                loadingPane.getChildren().addAll(spinner, loadingLabel, statusLabel);

                Scene loadingScene = new Scene(loadingPane);
                loadingStage.setScene(loadingScene);
                loadingStage.show();

                // Load dashboard in background thread
                Thread dashboardThread = new Thread(() -> {
                    try {
                        // Update status
                        Platform.runLater(() -> statusLabel.setText("Loading dashboard interface..."));

                        // Load FXML in background
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                        Parent root = loader.load();
                        DashboardController controller = loader.getController();

                        // Update status
                        Platform.runLater(() -> statusLabel.setText("Initializing your profile..."));

                        // Switch to UI thread for scene creation and display
                        Platform.runLater(() -> {
                            try {
                                // Set username on controller (triggers async data loading)
                                controller.setLoggedInUsername(username);

                                // Create scene
                                Scene scene = new Scene(root);

                                // Get current stage (login stage)
                                Stage stage = (Stage) loginBtn.getScene().getWindow();
                                stage.setScene(scene);
                                //stage.setMaximized(true);
                                stage.setFullScreen(true);
stage.setFullScreenExitHint("");

                                stage.show();

                                // Close loading window
                                loadingStage.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
                                loadingStage.close();
                            }
                        });

                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
                            loadingStage.close();
                        });
                        e.printStackTrace();
                    }
                });

                dashboardThread.setDaemon(true);
                dashboardThread.start();
            });

            loginTask.setOnFailed(event -> {
                timeline.stop();
                loginBtn.setDisable(false);
                loginBtn.setText("Login");
                if (progressIndicator != null) progressIndicator.setVisible(false);

                showAlert(Alert.AlertType.ERROR, "Login Error", loginTask.getException().getMessage());
                loginTask.getException().printStackTrace();
            });

            Thread thread = new Thread(loginTask);
            thread.setDaemon(true);
            thread.start();

        } catch (InvalidInputException e) {
            showAlert(Alert.AlertType.WARNING, "Login Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", "Something went wrong during login");
            e.printStackTrace();
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
        String usermail = forgotPass.getText();
        String newPassword = ConfiirmPass.getText();

        passMismatch.setText("");
        if (username.isEmpty() || newPassword.isEmpty() || usermail.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        if (!DatabaseUtility.userExists(username,usermail)) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user with this username and email.");
            return;
        }

        // if (!newPassword.equals(confirmPassword)) {
        //     passMismatch.setText("Passwords do not match!");
        //     return;
        // }

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

        private void showTermsDialog(MouseEvent event) {
        // Check if the checkbox is not already checked
        if (!dialogShown && !termsCheck.isSelected()) {
            // Create and display the Terms and Conditions dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Terms and Conditions");
            alert.setHeaderText("Please read the Terms and Conditions");
            alert.setContentText("Here are the terms and conditions...\n\n" +
                    "By using this application, you agree to these terms:\n" +
                    "- Term 1: You agree to provide accurate,complete info .\n" +
                    "- Term 2: You agree not to violate any local laws, regulations, or community standards.\n" +
                    "- Term 3: You agree not to use the app for any unlawful or unethical purposes");

            alert.show();
             // Set the flag to true after the dialog has been shown
            dialogShown = true;

            // Add an event handler to reset the flag when the dialog is closed
            alert.setOnHidden(e -> dialogShown = false);  // Reset the flag when the dialog is closed
        }
    }

  
}
