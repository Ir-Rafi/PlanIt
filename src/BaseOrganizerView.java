import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class BaseOrganizerView extends after_login {
    protected static Thread serverThread = null;
    protected static boolean serverStarted = false;

    /**
     * Start Discussion Server if not already running
     */
    protected void startDiscussionServerIfNeeded() {
        if (serverStarted) {
            System.out.println("✅ Discussion Server already running");
            return;
        }

        serverThread = new Thread(() -> {
            try {
                System.out.println("🚀 Starting Discussion Server...");
                DiscussionServer.main(new String[] {});
            } catch (Exception ex) {
                System.err.println("Discussion server error: " + ex.getMessage());
                if (ex.getMessage() != null && ex.getMessage().contains("Address already in use")) {
                    System.out.println("✅ Discussion Server already running on port");
                    serverStarted = true;
                }
            }
        });

        serverThread.setDaemon(true);
        serverThread.setName("DiscussionServerThread");
        serverThread.start();
        serverStarted = true;

        System.out.println("✅ Discussion Server thread started");
    }

    /**
     * Open Discussion Forum window
     */
    protected void openDiscussionForum(User user) {
        try {
            Stage forumStage = new Stage();
            forumStage.setTitle("Discussion Forum - " + user.username);

            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b);");

            Label titleLabel = new Label("💬 Discussion Forum");
            titleLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

            Label subtitleLabel = new Label("Connect and collaborate with your team");
            subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8;");

            VBox discussionThreadsContainer = new VBox(10);
discussionThreadsContainer.setStyle(
        "-fx-background-color: #1e293b; " +
                "-fx-padding: 20; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: #334155; " +
                "-fx-border-radius: 15; " +
                "-fx-border-width: 1;");

// Wrap in ScrollPane for scrolling
javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(discussionThreadsContainer);
scrollPane.setFitToWidth(true);
scrollPane.setMinHeight(300);
scrollPane.setMaxHeight(400);  // Limit height to enable scrolling
scrollPane.setStyle(
        "-fx-background: transparent; " +
        "-fx-background-color: transparent; " +
        "-fx-border-color: transparent;");
scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);

            DiscussionForumController controller = new DiscussionForumController();
            controller.injectThreadsContainer(discussionThreadsContainer);
            controller.setUser(user);

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);

            Button createThreadBtn = new Button("➕ Create Thread");
            createStyledButton(createThreadBtn, "#22c55e", "#4ade80");
            createThreadBtn.setOnAction(e -> {
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("fxml/CreateThread.fxml"));
                    javafx.scene.Parent root2 = loader.load();

                    CreateThreadController threadController = loader.getController();
                    threadController.setUser(user);
                    threadController.setForumController(controller);

                    Stage threadStage = new Stage();
                    threadStage.setScene(new Scene(root2, 450, 400));
                    threadStage.setTitle("Create Thread");
                    threadStage.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Error opening create thread window: " + ex.getMessage());
                }
            });

            Button refreshBtn = new Button("🔄 Refresh");
            createStyledButton(refreshBtn, "#3b82f6", "#60a5fa");
            refreshBtn.setOnAction(e -> controller.refreshDiscussions());

            Button backBtn = new Button("← Back");
            createStyledButton(backBtn, "#6b7280", "#9ca3af");
            backBtn.setOnAction(e -> forumStage.close());

            buttonBox.getChildren().addAll(createThreadBtn, refreshBtn, backBtn);

            root.getChildren().addAll(titleLabel, subtitleLabel, scrollPane, buttonBox);

            Scene scene = new Scene(root, 700, 500);
            forumStage.setScene(scene);
            forumStage.show();

            System.out.println("✅ Discussion Forum opened for: " + user.username);
        } catch (Exception ex) {
            System.err.println("❌ Error opening Discussion Forum: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    protected void openDiscussionForum(User user, int eventId) {
        try {
            // Set event context on server
            setServerEventContext(eventId);

            // Small delay to ensure context is set
            Thread.sleep(200);

            // Open forum
            openDiscussionForum(user);

        } catch (Exception ex) {
            System.err.println("❌ Error setting event context: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void setServerEventContext(int eventId) {
        try (java.net.Socket socket = new java.net.Socket("127.0.0.1", 45456);
                java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
                java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream())) {

            System.out.println("🎯 Setting event context to: " + eventId);
            out.writeObject("SET_EVENT_CONTEXT");
            out.writeObject(eventId);
            out.flush();

            String response = (String) in.readObject();
            System.out.println("✅ Server response: " + response);

        } catch (Exception e) {
            System.err.println("❌ Error setting event context: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Style discussion button with hover effects
     */
    protected void createStyledButton(Button button, String baseColor, String hoverColor) {
        button.setStyle(
                "-fx-background-color: " + baseColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10 20;");
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + hoverColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10 20;"));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + baseColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10 20;"));
    }
}