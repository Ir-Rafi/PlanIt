import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SubOrganizerView extends BaseOrganizerView implements OrganizerPanel {
    protected Stage stage;
    protected Scene eventsScene;
    private final String subOrganizerName;
    private final OrganizerViewContext context;
    private final int subOrganizerId;
    private final int eventid;
    private List<AssignRolesWindow.EventTask> tasks = new ArrayList<>();
    private VBox tasksBox;

    public SubOrganizerView(Stage stage, Scene eventsScene, String subOrganizerName, int subOrganizerId) {
        this(stage, eventsScene, subOrganizerName, subOrganizerId, new subOrganizerPortalContext(), -1);
    }

    // OVERLOADED CONSTRUCTOR - With context but no eventId
    public SubOrganizerView(Stage stage, Scene eventsScene, String subOrganizerName, int subOrganizerId,
            OrganizerViewContext context) {
        this(stage, eventsScene, subOrganizerName, subOrganizerId, context, -1);
    }

    public SubOrganizerView(Stage stage, Scene eventsScene, String subOrganizerName, int subOrganizerId,
            OrganizerViewContext context, int eventid) {
        this.stage = stage;
        this.eventsScene = eventsScene;
        this.subOrganizerName = subOrganizerName;
        this.subOrganizerId = subOrganizerId;
        this.context = context;
        this.eventid = eventid;
        loadTasks();

        System.out.println("🎯 SubOrganizerView created with context: " + context.getContextName() + 
                          ", eventId: " + (eventid == -1 ? "NONE (Global)" : eventid));


        VBox layout = new VBox(30);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1E1E2F, #2C2C3E);");

        Label title = new Label("Sub-Organizer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        tasksBox = new VBox(20);
        tasksBox.setAlignment(Pos.TOP_CENTER);
        refreshTasks();

        ScrollPane scrollPane = new ScrollPane(tasksBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        // Chat button with conditional display
        Button chatBtn = new Button("💬 Open Discussion Forum");
        styleButton(chatBtn, "#7E57C2", "#5E35B1");
        chatBtn.setOnAction(e -> {
            try {
                User currentUser = new User(subOrganizerName, getOrganizerType());
                startDiscussionServerIfNeeded();
                Thread.sleep(500);

                // Use event-specific chat only if we have a valid eventId
                if (eventid != -1) {
                    openDiscussionForum(currentUser, eventid);  // Event-specific
                } else {
                    openDiscussionForum(currentUser);  // Global
                }

            } catch (Exception ex) {
                System.err.println("Error opening Discussion Forum: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Back button
        Button backBtn = new Button("← Back");
        backBtn.setStyle("""
                    -fx-background-color: #444C5C;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-padding: 8 20 8 20;
                """);
        backBtn.setOnAction(e -> {
            stage.setScene(eventsScene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
        });

        // Add components to layout conditionally
        layout.getChildren().addAll(title, scrollPane);

        if (context.shouldShowButton("chat")) {
            layout.getChildren().add(chatBtn);
        }

        layout.getChildren().add(backBtn);

        Scene scene = new Scene(layout, 1920, 1080);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }

    /**
     * Style button with hover effects
     */
    private void styleButton(Button button, String baseColor, String hoverColor) {
        button.setMaxWidth(400);
        button.setPrefHeight(50);
        button.setStyle(
                "-fx-background-color: " + baseColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10 20;");

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + hoverColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10 20;"));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + baseColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10 20;"));
    }

    private void refreshTasks() {
        tasksBox.getChildren().clear();

        List<AssignRolesWindow.EventTask> subTasks = new ArrayList<>();
        for (AssignRolesWindow.EventTask task : tasks) {
            if (task.subOrganizerName.equalsIgnoreCase(subOrganizerName)) {
                subTasks.add(task);
            }
        }

        if (subTasks.isEmpty()) {
            Label emptyLabel = new Label("No tasks assigned yet.");
emptyLabel.setMaxWidth(Double.MAX_VALUE);
emptyLabel.setAlignment(Pos.CENTER);
emptyLabel.setPadding(new Insets(15));
emptyLabel.setStyle("""
    -fx-font-size: 18px;
    -fx-background-color: black;
    -fx-text-fill: #FFD700;
""");
            tasksBox.getChildren().add(emptyLabel);
            return;
        }

        for (AssignRolesWindow.EventTask task : subTasks) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(15));
            card.setStyle("""
                        -fx-background-color: #2F2F3F;
                        -fx-background-radius: 10;
                    """);
            card.setEffect(new DropShadow(6, Color.BLACK));

            Label eventLabel = new Label("Event ID: " + task.eventId);
            eventLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 14px;");

            Label taskLabel = new Label(task.taskDescription);
            taskLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 13px;");

            Label statusLabel = new Label(task.completed ? "✅ Completed" : "⏳ Pending");
            statusLabel.setStyle(
                    task.completed ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #FFA000; -fx-font-weight: bold;");

            Button completeBtn = new Button("Mark Complete");
            completeBtn.setDisable(task.completed);
            completeBtn.setStyle("""
                        -fx-background-color: #4CAF50;
                        -fx-text-fill: white;
                        -fx-font-weight: bold;
                        -fx-background-radius: 8;
                        -fx-padding: 5 15 5 15;
                    """);
            completeBtn.setOnAction(e -> {
                task.completed = true;
                saveTasks();
                refreshTasks();
            });

            HBox statusRow = new HBox(10);
            statusRow.setAlignment(Pos.CENTER_LEFT);
            statusRow.getChildren().addAll(statusLabel, completeBtn);

            card.getChildren().addAll(eventLabel, taskLabel, statusRow);
            tasksBox.getChildren().add(card);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File file = new File("tasks.dat");
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (List<AssignRolesWindow.EventTask>) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            out.writeObject(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        // Initialization logic if needed
    }

    @Override
    public String getOrganizerType() {
        return "Sub Organizer";
    }

    @Override
    public int getOrganizerId() {
        return subOrganizerId;
    }

    @Override
    public String getOrganizerName() {
        return subOrganizerName;
    }
}