import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;








public class AssignRolesWindow {
    
    private final int mainOrganizerId;
    private final String mainOrganizerName;
    private final List<EventTask> tasks = new ArrayList<>();
    private VBox subOrganizerBox;

    public AssignRolesWindow(String mainOrganizerName, int mainOrganizerId) {
        this.mainOrganizerId = mainOrganizerId;
        this.mainOrganizerName = mainOrganizerName;
        loadTasks();
    }

    public void show() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Assign Tasks to Sub-Organizers");

        VBox layout = new VBox(18);
        layout.setPadding(new Insets(24));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #111216, #1B1D26);");

        Label title = new Label("Sub-Organizers & Assigned Tasks");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(#FFD966, #FFD700);");

        subOrganizerBox = new VBox(18);
        subOrganizerBox.setAlignment(Pos.TOP_CENTER);

        refreshSubOrganizerCards();

        ScrollPane scrollPane = new ScrollPane(subOrganizerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("""
            -fx-background-color: #2F3440;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-padding: 8 20 8 20;
        """);
        closeBtn.setOnAction(e -> window.close());

        layout.getChildren().addAll(title, scrollPane, closeBtn);

        Scene scene = new Scene(layout, 920, 720);
        window.setScene(scene);
        window.show();
    }

    /* ---------------------------- UI Refresh ---------------------------- */

    private void refreshSubOrganizerCards() {

        subOrganizerBox.getChildren().clear();
        Label loading = new Label("Loading sub-organizers...");
        loading.setStyle("-fx-text-fill: #BFC6D0; -fx-font-size: 16px;");
        subOrganizerBox.getChildren().add(loading);

        new Thread(() -> {
            List<EventData> events = getSubOrganizerEvents();
            List<VBox> eventCards = new ArrayList<>();

            for (EventData event : events) {
                VBox eventContainer = new VBox(12);
                eventContainer.setPadding(new Insets(12));
                eventContainer.setStyle("-fx-background-color: #232633; -fx-background-radius: 10;");
                eventContainer.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.6)));

                HBox eventHeader = new HBox(10);
                eventHeader.setAlignment(Pos.CENTER_LEFT);

                Label eventLabel = new Label(event.eventName + "  ");
                eventLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD966;");

                // Add Sub-Organizer button (SVG icon)
                Button addSubOrgBtn = createIconButton(svgPlus(), "#FFD700", "Add sub-organizer");
                addSubOrgBtn.setOnAction(e -> openAddSubOrganizerDialog(event));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                eventHeader.getChildren().addAll(eventLabel, spacer, addSubOrgBtn);
                eventContainer.getChildren().add(eventHeader);

                List<SubOrganizer> subOrgs = getSubOrganizers(event.eventId);
                if (subOrgs.isEmpty()) {
                    Label noSub = new Label("No sub-organizers for this event.");
                    noSub.setStyle("-fx-text-fill: #9EA7B2;");
                    eventContainer.getChildren().add(noSub);
                } else {
                    VBox subList = new VBox(10);
                    for (SubOrganizer sub : subOrgs) {
                        HBox card = new HBox(12);
                        card.setPadding(new Insets(12));
                        card.setStyle("-fx-background-color: #2D3142; -fx-background-radius: 8;");
                        card.setAlignment(Pos.CENTER_LEFT);
                        card.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.5)));

                        // Hover effect
                        card.addEventHandler(MouseEvent.MOUSE_ENTERED, ev ->
                                card.setStyle("-fx-background-color: #363A4E; -fx-background-radius: 8;"));
                        card.addEventHandler(MouseEvent.MOUSE_EXITED, ev ->
                                card.setStyle("-fx-background-color: #2D3142; -fx-background-radius: 8;"));

                        VBox left = new VBox(6);
                        Label nameLabel = new Label(sub.name + "  (ID: " + sub.id + ")");
                        nameLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px; -fx-text-fill: white;");

                        HBox iconRow = new HBox(8);
                        iconRow.setAlignment(Pos.CENTER_LEFT);

                        Button addTaskBtn = createIconButton(svgPlus(), "#FFD700", "Assign task");
                        addTaskBtn.setOnAction(e -> openAddTaskDialog(event.eventId, sub));

                        Button deleteSubBtn = createIconButton(svgTrash(), "#FF6B6B", "Delete sub-organizer");
                        deleteSubBtn.setOnAction(e -> deleteSubOrganizer(event.eventId, sub));

                        iconRow.getChildren().addAll(addTaskBtn, deleteSubBtn);
                        left.getChildren().addAll(nameLabel, iconRow);

                        VBox taskList = new VBox(8);

                        for (EventTask task : tasks) {
                            if (task.eventId == event.eventId && task.subOrganizerName.equals(sub.name)) {

                                HBox taskRow = new HBox(10);
                                taskRow.setAlignment(Pos.CENTER_LEFT);
                                taskRow.setPadding(new Insets(6, 6, 6, 6));
                                taskRow.setStyle("-fx-background-color: #222531; -fx-background-radius: 6;");

                                Label taskLabel = new Label(task.taskDescription);
                                taskLabel.setWrapText(true);
                                taskLabel.setMaxWidth(420);
                                taskLabel.setStyle(task.completed
                                        ? "-fx-text-fill: #9EE6A8; -fx-font-size: 13px;"
                                        : "-fx-text-fill: #FFDDAA; -fx-font-size: 13px;");

                                Region tSpacer = new Region();
                                HBox.setHgrow(tSpacer, Priority.ALWAYS);

                                Button completeBtn = createCircleButton(svgCheck(), "#4CAF50", "Mark complete");
                                completeBtn.setDisable(task.completed);
                                completeBtn.setOnAction(ev -> {
                                    task.completed = true;
                                    saveTasks();
                                    refreshSubOrganizerCards();
                                });

                                Button deleteTaskBtn = createCircleButton(svgClose(), "#E04848", "Delete task");
                                deleteTaskBtn.setOnAction(ev -> {
                                    // confirm deletion
                                    Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                                    a.setTitle("Delete Task");
                                    a.setHeaderText(null);
                                    a.setContentText("Delete this task?");
                                    Optional<ButtonType> res = a.showAndWait();
                                    if (res.isPresent() && res.get() == ButtonType.OK) {
                                        tasks.remove(task);
                                        saveTasks();
                                        refreshSubOrganizerCards();
                                    }
                                });

                                taskRow.getChildren().addAll(taskLabel, tSpacer, completeBtn, deleteTaskBtn);
                                taskList.getChildren().add(taskRow);
                            }
                        }

                        VBox rightContainer = new VBox(6);
                        rightContainer.getChildren().addAll(taskList);

                        HBox.setHgrow(rightContainer, Priority.ALWAYS);

                        card.getChildren().addAll(left, rightContainer);
                        subList.getChildren().add(card);
                    }
                    eventContainer.getChildren().add(subList);
                }

                eventCards.add(eventContainer);
            }

            Platform.runLater(() -> {
                subOrganizerBox.getChildren().clear();
                if (eventCards.isEmpty()) {
                    Label noEvent = new Label("No sub-organizers found for your events.");
                    noEvent.setStyle("-fx-font-size: 16px; -fx-text-fill: #9EA7B2;");
                    subOrganizerBox.getChildren().add(noEvent);
                } else {
                    subOrganizerBox.getChildren().addAll(eventCards);
                }
            });

        }).start();
    }

    /* ---------------------------- Dialogs ---------------------------- */

    private void openAddTaskDialog(int eventId, SubOrganizer sub) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Assign Task to " + sub.name);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(18));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle("-fx-background-color: #1F2229; -fx-background-radius: 8;");

        Label dialogTitle = new Label("Assign Task to: " + sub.name);
        dialogTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #FFD966;");

        TextArea taskArea = new TextArea();
        taskArea.setPromptText("Task description...");
        taskArea.setPrefRowCount(5);
        taskArea.setWrapText(true);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button assignBtn = new Button("Assign Task");
        assignBtn.setStyle("""
            -fx-background-color: #FFD700;
            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-padding: 8 14 8 14;
        """);
        assignBtn.setOnAction(e -> {
            String taskDesc = taskArea.getText().trim();
            if (!taskDesc.isEmpty()) {
                EventTask newTask = new EventTask(eventId, sub.name, taskDesc, false);
                tasks.add(newTask);
                saveTasks();
                dialog.close();
                refreshSubOrganizerCards();
            } else {
                new Alert(Alert.AlertType.WARNING, "Please enter a task description!").showAndWait();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #C6C9D1;");
        cancel.setOnAction(e -> dialog.close());

        btnRow.getChildren().addAll(cancel, assignBtn);
        layout.getChildren().addAll(dialogTitle, taskArea, btnRow);

        Scene scene = new Scene(layout, 520, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

private void openAddSubOrganizerDialog(EventData event) {  // <-- Use AssignRolesWindow.EventData
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setTitle("Add Sub-Organizer to " + event.eventName);

    VBox layout = new VBox(12);
    layout.setPadding(new Insets(16));
    layout.setAlignment(Pos.CENTER_LEFT);
    layout.setStyle("-fx-background-color: #1F2229; -fx-background-radius: 8;");

    Label lblName = new Label("Sub-organizer name:");
    lblName.setStyle("-fx-text-fill: #D9DCE1;");
    TextField nameField = new TextField();
    nameField.setPromptText("Enter name...");

    Label lblId = new Label("Sub-organizer ID:");
    lblId.setStyle("-fx-text-fill: #D9DCE1;");
    TextField idField = new TextField();
    idField.setPromptText("Enter ID...");

    HBox btnRow = new HBox(10);
    btnRow.setAlignment(Pos.CENTER_RIGHT);

    Button addBtn = new Button("Add");
    addBtn.setStyle("""
        -fx-background-color: #4A90E2;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-padding: 6 12 6 12;
    """);

    addBtn.setOnAction(e -> {
        String name = nameField.getText().trim();
        String codeText = idField.getText().trim();

        if (name.isEmpty() || codeText.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Name and ID cannot be empty!").showAndWait();
            return;
        }

        int code;
        try {
            code = Integer.parseInt(codeText);
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Organizer ID must be a number!").showAndWait();
            return;
        }

        // Check if already exists in DB
        if (isSubOrganizerExists(event.eventId, name)) {
            new Alert(Alert.AlertType.WARNING, "This sub-organizer already exists!").showAndWait();
            return;
        }

        // Add sub-organizer in DB
        addSubOrganizerToDB(event.eventId, name, code);

        dialog.close();
        refreshSubOrganizerCards();
    });

    Button cancel = new Button("Cancel");
    cancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #C6C9D1;");
    cancel.setOnAction(ev -> dialog.close());

    btnRow.getChildren().addAll(cancel, addBtn);
    layout.getChildren().addAll(lblName, nameField, lblId, idField, btnRow);

    Scene scene = new Scene(layout, 420, 200);
    dialog.setScene(scene);
    dialog.showAndWait();
}



    /* ---------------------------- DB / Helpers ---------------------------- */

    private void deleteSubOrganizer(int eventId, SubOrganizer sub) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete sub-organizer '" + sub.name + "'?\nThis will also delete all tasks assigned to them.",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) return;

        new Thread(() -> {
            try (Connection conn = getConnection()) {
                String sql = "DELETE FROM organizers WHERE organizer_id = ? AND event_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, sub.id);
                ps.setInt(2, eventId);
                ps.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to delete sub-organizer!").show());
            }

            // Also delete tasks from tasks.dat
            tasks.removeIf(t -> t.eventId == eventId && t.subOrganizerName.equals(sub.name));
            saveTasks();

            Platform.runLater(this::refreshSubOrganizerCards);
        }).start();
    }

    private void addSubOrganizerToDB(int eventId, String name,int code) {
        new Thread(() -> {
            String sql = "INSERT INTO organizers (organizer_name, event_id,organizer_id) VALUES (?, ?,?)";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setInt(2, eventId);
                ps.setInt(3, code);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to add sub-organizer!").show());
            }
        }).start();
    }

    private boolean isSubOrganizerExists(int eventId, String name) {
        String sql = "SELECT 1 FROM organizers WHERE event_id = ? AND organizer_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveTasks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            out.writeObject(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File file = new File("tasks.dat");
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                tasks.addAll((List<EventTask>) in.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<EventData> getSubOrganizerEvents() {
        List<EventData> eventList = new ArrayList<>();
        String sql = """
            SELECT DISTINCT e.event_id, e.event_name 
            FROM events e 
            JOIN organizers o ON e.event_id = o.event_id
            WHERE e.organizer_id = ? AND o.organizer_id != ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mainOrganizerId);
            ps.setInt(2, mainOrganizerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                eventList.add(new EventData(rs.getInt("event_id"), rs.getString("event_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventList;
    }

    private List<SubOrganizer> getSubOrganizers(int eventId) {
        List<SubOrganizer> list = new ArrayList<>();
        String sql = "SELECT organizer_id, organizer_name FROM organizers WHERE event_id = ? AND organizer_id != ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, mainOrganizerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SubOrganizer(rs.getInt("organizer_id"), rs.getString("organizer_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc";
        String user = "ununqd8usvy0wouy";
        String password = "GmDEehgTBjzyuPRuA8i8";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    /* ---------------------------- UI helpers (SVG icons + button factories) ---------------------------- */

    private Button createIconButton(SVGPath svg, String color, String tooltipText) {
        svg.setFill(Color.web(color));
        svg.setScaleX(0.85);
        svg.setScaleY(0.85);
        Button btn = new Button();
        btn.setGraphic(svg);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btn.setPadding(new Insets(6));
        if (tooltipText != null) btn.setTooltip(new Tooltip(tooltipText));
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> svg.setOpacity(0.85));
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> svg.setOpacity(1.0));
        return btn;
    }

    private Button createCircleButton(SVGPath svg, String bgColor, String tooltipText) {
        svg.setFill(Color.WHITE);
        svg.setScaleX(0.7);
        svg.setScaleY(0.7);
        StackPane sp = new StackPane(svg);
        sp.setPrefSize(34, 34);
        sp.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 18;");
        Button btn = new Button();
        btn.setGraphic(sp);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        if (tooltipText != null) btn.setTooltip(new Tooltip(tooltipText));
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> sp.setScaleX(1.05));
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> sp.setScaleX(1.0));
        return btn;
    }

    /* SVG paths for icons (compact, embeddable) */
    private SVGPath svgPlus() {
        SVGPath s = new SVGPath();
        s.setContent("M10 4h4v6h6v4h-6v6h-4v-6H4V10h6z"); // plus (simple)
        return s;
    }

    private SVGPath svgTrash() {
        SVGPath s = new SVGPath();
        s.setContent("M3 6h18v2H3V6zm3 3h12l-1.2 11H7.2L6 9zM9 4h6l1 2H8l1-2z"); // trash-like
        return s;
    }

    private SVGPath svgCheck() {
        SVGPath s = new SVGPath();
        s.setContent("M1.5 9.5L6 14l12-12-2-2L6 10l-2.5-2.5z"); // check-ish
        return s;
    }
private SVGPath svgClose() {
    SVGPath s = new SVGPath();
    s.setContent("M6 6 L18 18 M18 6 L6 18");
    s.setStroke(Color.WHITE);
    s.setStrokeWidth(2.5);
    s.setFill(Color.TRANSPARENT);
    return s;
}




    /* ---------------------------- Helper classes ---------------------------- */

    static class EventData {
        int eventId;
        String eventName;
        EventData(int id, String name) { this.eventId = id; this.eventName = name; }
    }

    static class SubOrganizer {
        int id;
        String name;
        SubOrganizer(int id, String name) { this.id = id; this.name = name; }
    }

    public static class EventTask implements Serializable {
        public int eventId;
        public String subOrganizerName;
        public String taskDescription;
        public boolean completed;
        public EventTask(int eventId, String subOrganizerName, String taskDescription, boolean completed) {
            this.eventId = eventId;
            this.subOrganizerName = subOrganizerName;
            this.taskDescription = taskDescription;
            this.completed = completed;
        }
    }
}



