import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class CreateThreadController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    private User currentUser;
    private DiscussionForumController forumController;

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 45456;

    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("✅ User set in CreateThreadController: " + user.username + " (" + user.role + ")");
    }

    public void setForumController(DiscussionForumController controller) {
        this.forumController = controller;
        System.out.println("✅ Forum controller set in CreateThreadController");
    }

    @FXML
    public void initialize() {
        System.out.println("CreateThreadController initialized");
    }

    @FXML
    public void handlePost(ActionEvent event) {
        System.out.println("📝 Post button clicked");

        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        System.out.println("Title: " + title);
        System.out.println("Content: " + content);

        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Please fill in both title and content");
            return;
        }

        if (currentUser == null) {
            showAlert("User not set. Please try again.");
            System.err.println("❌ Current user is null!");
            return;
        }

        try {
            System.out.println("Attempting to create thread on server...");
            createThreadOnServer(title, content);
            showSuccessAlert("Thread created successfully!");
            
            if (forumController != null) {
                System.out.println("Refreshing forum discussions...");
                forumController.refreshDiscussions();
            }

            closeWindow();
        } catch (ConnectException e) {
            showAlert("Cannot connect to server!\n\nPlease make sure the Discussion Server is running.\n\nError: " + e.getMessage());
            System.err.println("❌ Server not running or connection refused");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert("Failed to create thread: " + e.getMessage());
            System.err.println("❌ IO Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        System.out.println("Cancel button clicked");
        closeWindow();
    }

    private void createThreadOnServer(String title, String content) throws IOException {
        System.out.println("🔌 Connecting to server at " + SERVER_HOST + ":" + SERVER_PORT);
        
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("✅ Connected to server");
            
            System.out.println("Sending CREATE_THREAD command...");
            out.writeObject("CREATE_THREAD");
            
            System.out.println("Sending username: " + currentUser.username);
            out.writeObject(currentUser.username);
            
            System.out.println("Sending role: " + currentUser.role);
            out.writeObject(currentUser.role);
            
            System.out.println("Sending title: " + title);
            out.writeObject(title);
            
            System.out.println("Sending content: " + content);
            out.writeObject(content);
            
            out.flush();
            System.out.println("✅ Data sent, waiting for response...");

            String response = (String) in.readObject();
            System.out.println("✅ Server response: " + response);

            if ("SUCCESS".equals(response)) {
                System.out.println("🎉 Thread created successfully!");
            } else {
                System.err.println("⚠️ Unexpected response: " + response);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Class not found: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error reading server response", e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        if (titleField != null && titleField.getScene() != null) {
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();
            System.out.println("Window closed");
        }
    }
}