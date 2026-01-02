import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OrganizerViewFactory {

    /**
     * Async version - Creates organizer view with background thread for database
     * operations
     */
    public static void createOrganizerView(
            Stage stage,
            Scene eventsScene,
            String organizerName,
            int organizerId,
            int eventId,
            DashboardContext context) {

        // Show loading dialog
        Stage loadingStage = showLoadingDialog(stage);

        // Create background task for database operations
        Task<OrganizerPanel> task = new Task<OrganizerPanel>() {
            @Override
            protected OrganizerPanel call() throws Exception {
                System.out.println("🔍 Checking role for user " + organizerId + " in event " + eventId);

                // These database calls now run on background thread
                if (isMainOrganizerOfEvent(eventId, organizerId)) {
                    System.out.println("✅ User is MAIN organizer of event " + eventId);
                    // Create view on JavaFX thread
                    final MainOrganizerView[] view = new MainOrganizerView[1];
                    Platform.runLater(() -> {
                        view[0] = new MainOrganizerView(stage, eventsScene, organizerName, organizerId, context, eventId);
                    });
                    return view[0];

                }

                if (isSubOrganizerOfEvent(eventId, organizerId)) {
                    System.out.println("✅ User is SUB organizer of event " + eventId);
                    final SubOrganizerView[] view = new SubOrganizerView[1];
                    Platform.runLater(() -> {
                        view[0] = new SubOrganizerView(stage, eventsScene, organizerName, organizerId, context, eventId);
                    });
                    return view[0];
                }

                System.out.println("❌ User has no organizer role for event " + eventId);
                return null;
            }
        };

        // Handle success
        task.setOnSucceeded(e -> {
            loadingStage.close();
        });

        // Handle failure
        task.setOnFailed(e -> {
            loadingStage.close();
            Throwable exception = task.getException();
            exception.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load organizer view");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        });

        // Start background thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private static Stage showLoadingDialog(Stage owner) {
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initOwner(owner);
        loadingStage.setTitle("Loading...");

        VBox loadingBox = new VBox(15);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setStyle("-fx-background-color: #1A1A2E; -fx-padding: 30;");

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(60, 60);
        progress.setStyle("-fx-progress-color: #8B5CF6;");

        javafx.scene.control.Label loadingLabel = new javafx.scene.control.Label("Loading organizer view...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        loadingBox.getChildren().addAll(progress, loadingLabel);

        Scene scene = new Scene(loadingBox, 300, 150);
        loadingStage.setScene(scene);
        loadingStage.show();

        return loadingStage;
    }

    // Database helper methods - these can safely run on background thread
    private static boolean isMainOrganizerOfEvent(int eventId, int userId) {
        try (java.sql.Connection conn = DatabaseUtility.getConnection()) {
            String query = "SELECT organizer_id FROM events WHERE event_id = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, eventId);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int mainOrganizerId = rs.getInt("organizer_id");
                System.out.println("📋 Event " + eventId + " main organizer ID: " + mainOrganizerId);
                return mainOrganizerId == userId;
            }
        } catch (Exception e) {
            System.err.println("❌ Error checking main organizer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isSubOrganizerOfEvent(int eventId, int userId) {
        try (java.sql.Connection conn = DatabaseUtility.getConnection()) {
            String query = "SELECT COUNT(*) as count FROM organizers WHERE event_id = ? AND organizer_id = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println(
                        "📋 Found " + count + " sub-organizer records for user " + userId + " in event " + eventId);
                return count > 0;
            }
        } catch (Exception e) {
            System.err.println("❌ Error checking sub organizer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}