import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DiscussionForumController {
    private User currentUser;

    @FXML
    private VBox discussionThreadsContainer;

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 45456;

    public void injectThreadsContainer(VBox container) {
        this.discussionThreadsContainer = container;
    }

    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("User set: " + user.username);
        loadDiscussions();
    }

    public void refreshDiscussions() {
        loadDiscussions();
    }

    @FXML
    public void handleCreateThread(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/CreateThread.fxml"));
            Parent root = loader.load();

            CreateThreadController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setForumController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 400, 350));
            stage.setTitle("Create Thread");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        System.out.println("Back button clicked");
        // TODO: Navigate back to dashboard
    }

    private void loadDiscussions() {
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                System.out.println("Connection to server...");
                out.writeObject("LOAD_DISCUSSIONS");
                out.flush();

                @SuppressWarnings("unchecked")
                List<DiscussionThread> threads = (List<DiscussionThread>) in.readObject();
                System.out.println("Received " + threads.size() + " threads");

                Platform.runLater(() -> displayThreads(threads));

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading discussions: " + e.getMessage());
                e.printStackTrace();

                Platform.runLater(() -> {
                    if (discussionThreadsContainer != null) {
                        discussionThreadsContainer.getChildren().clear();
                        Label errorLabel = new Label("❌ Could not connect to server\n" + e.getMessage());
                        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14px;");
                        discussionThreadsContainer.getChildren().add(errorLabel);
                    }
                });
            }
        }).start();

    }

    // DiscussionForumController.java - Updated displayThreads method
private void displayThreads(List<DiscussionThread> threads) {

    if (discussionThreadsContainer == null) {
        System.err.println("❌ discussionThreadsContainer is null!");
        return;
    }

    discussionThreadsContainer.getChildren().clear();

    if (threads.isEmpty()) {
        Label noThreads = new Label("No discussions yet. Be the first to post!");
        noThreads.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        discussionThreadsContainer.getChildren().add(noThreads);
        return;
    }

    for (DiscussionThread thread : threads) {
        VBox threadBox = new VBox(8);
        threadBox.setStyle(
                "-fx-background-color: #334155; " +
                        "-fx-padding: 15; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-border-color: transparent; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-width: 2;");

        // Title
        Label titleLabel = new Label(thread.title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Author info
        Label authorLabel = new Label("by " + thread.authorUsername + " (" + thread.authorRole + ")");
        authorLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        // Content preview (collapsed by default)
        Label contentPreview = new Label(
            thread.content.length() > 100 
                ? thread.content.substring(0, 100) + "..." 
                : thread.content
        );
        contentPreview.setStyle(
            "-fx-text-fill: #cbd5e1; " +
            "-fx-font-size: 13px; " +
            "-fx-padding: 10 0 0 0;");
        contentPreview.setWrapText(true);
        contentPreview.setVisible(false);  // Hidden by default
        contentPreview.setManaged(false);  // Doesn't take space when hidden

        // Click to expand indicator
        Label clickHint = new Label("▶ Click to view details");
        clickHint.setStyle("-fx-text-fill: #8b5cf6; -fx-font-size: 11px; -fx-font-style: italic;");

        threadBox.getChildren().addAll(titleLabel, authorLabel, clickHint, contentPreview);

        // Hover effect
        threadBox.setOnMouseEntered(e -> {
            threadBox.setStyle(
                    "-fx-background-color: #3f4a5c; " +
                            "-fx-padding: 15; " +
                            "-fx-background-radius: 10; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: #8b5cf6; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-width: 2;");
        });

        threadBox.setOnMouseExited(e -> {
            threadBox.setStyle(
                    "-fx-background-color: #334155; " +
                            "-fx-padding: 15; " +
                            "-fx-background-radius: 10; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: transparent; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-width: 2;");
        });

        // Click to expand/collapse thread details
        threadBox.setOnMouseClicked(e -> {
            boolean isExpanded = contentPreview.isVisible();
            
            if (isExpanded) {
                // Collapse
                contentPreview.setVisible(false);
                contentPreview.setManaged(false);
                clickHint.setText("▶ Click to view details");
            } else {
                // Expand - show full content
                contentPreview.setVisible(true);
                contentPreview.setManaged(true);
                contentPreview.setText(thread.content);  // Show full content
                clickHint.setText("▼ Click to collapse");
            }
            
            System.out.println("Thread clicked: " + thread.title);
        });

        discussionThreadsContainer.getChildren().add(threadBox);
    }
}

}
