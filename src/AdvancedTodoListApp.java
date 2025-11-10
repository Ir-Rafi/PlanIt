import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class AdvancedTodoListApp extends Application {

    private ObservableList<Task> allTasks = FXCollections.observableArrayList();
    private ObservableList<String> categories = FXCollections.observableArrayList("Work", "Personal", "Shopping");
    private ListView<Task> taskListView;
    private ComboBox<String> filterCombo, priorityCombo, categoryCombo;
    private TextField searchField;
    private Label totalLabel, completedLabel, pendingLabel;

    private Scene organizerScene; // store main organizer scene

    public AdvancedTodoListApp() {}

    public AdvancedTodoListApp(Scene organizerScene) {
        this.organizerScene = organizerScene;
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        VBox header = createHeader(stage);
        root.setTop(header);

        VBox mainContent = createMainContent();
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("todoListStyle.css").toExternalForm());

        stage.setTitle("Advanced To-Do List");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private VBox createHeader(Stage stage) {
        VBox header = new VBox(15);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(20));

        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBar, Priority.ALWAYS);

        Label title = new Label("Advanced To-Do List");
        title.getStyleClass().add("app-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 🧭 BACK BUTTON
        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("btn-primary");
        backBtn.setOnAction(e -> {
            if (organizerScene != null) {
                stage.setScene(organizerScene);
            } else {
                // fallback: show an alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Back Navigation");
                alert.setHeaderText("Organizer scene not available!");
                alert.showAndWait();
            }
        });

        titleBar.getChildren().addAll(title, spacer, backBtn);

        HBox statsBar = new HBox(30);
        statsBar.setAlignment(Pos.CENTER);
        statsBar.getStyleClass().add("stats-bar");
        statsBar.setPadding(new Insets(15));

        VBox totalBox = createStatBox("0", "Total");
        VBox completedBox = createStatBox("0", "Completed");
        VBox pendingBox = createStatBox("0", "Pending");

        totalLabel = (Label) totalBox.getChildren().get(0);
        completedLabel = (Label) completedBox.getChildren().get(0);
        pendingLabel = (Label) pendingBox.getChildren().get(0);

        statsBar.getChildren().addAll(totalBox, completedBox, pendingBox);
        header.getChildren().addAll(titleBar, statsBar);
        return header;
    }

    private VBox createStatBox(String count, String label) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        Label countLabel = new Label(count);
        countLabel.getStyleClass().add("stat-count");

        Label textLabel = new Label(label);
        textLabel.getStyleClass().add("stat-label");

        box.getChildren().addAll(countLabel, textLabel);
        return box;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("main-content");

        HBox inputBox = new HBox(10);
        TextField taskInput = new TextField();
        taskInput.setPromptText("Add a new task...");
        taskInput.getStyleClass().add("task-input");
        HBox.setHgrow(taskInput, Priority.ALWAYS);

        Button addBtn = new Button("➕ Add");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> addTask(taskInput.getText(), taskInput));
        taskInput.setOnAction(e -> addTask(taskInput.getText(), taskInput));

        inputBox.getChildren().addAll(taskInput, addBtn);

        HBox filtersBox = new HBox(10);
        filtersBox.setAlignment(Pos.CENTER_LEFT);

        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All Tasks", "Completed", "Pending");
        filterCombo.setValue("All Tasks");
        filterCombo.getStyleClass().add("filter-combo");
        filterCombo.setOnAction(e -> filterTasks());

        priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("All Priorities", "High", "Medium", "Low");
        priorityCombo.setValue("All Priorities");
        priorityCombo.getStyleClass().add("filter-combo");
        priorityCombo.setOnAction(e -> filterTasks());

        categoryCombo = new ComboBox<>();
        updateCategoryCombo();
        categoryCombo.getStyleClass().add("filter-combo");
        categoryCombo.setOnAction(e -> filterTasks());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search tasks...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((obs, old, newVal) -> filterTasks());

        filtersBox.getChildren().addAll(filterCombo, priorityCombo, categoryCombo, spacer, searchField);

        taskListView = new ListView<>(allTasks);
        taskListView.getStyleClass().add("task-list");
        taskListView.setCellFactory(lv -> new TaskCell());
        VBox.setVgrow(taskListView, Priority.ALWAYS);

        content.getChildren().addAll(inputBox, filtersBox, taskListView);
        updateStats();
        return content;
    }

    private void addTask(String text, TextField input) {
        if (text == null || text.trim().isEmpty()) return;
        Task task = new Task(text.trim());
        allTasks.add(0, task);
        input.clear();
        updateStats();
    }

    private void deleteTask(Task task) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Task");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to delete this task?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            allTasks.remove(task);
            updateStats();
        }
    }

    private void editTask(Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Task Details");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField textField = new TextField(task.getText());
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("High", "Medium", "Low");
        priorityBox.setValue(task.getPriority());

        DatePicker datePicker = new DatePicker();
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            datePicker.setValue(LocalDate.parse(task.getDueDate()));
        }

        TextField categoryField = new TextField();
        categoryField.setPromptText("Add category...");

        ListView<String> categoryList = new ListView<>(FXCollections.observableArrayList(task.getCategories()));
        categoryList.setPrefHeight(100);

        Button addCatBtn = new Button("Add");
        addCatBtn.setOnAction(e -> {
            String cat = categoryField.getText().trim();
            if (!cat.isEmpty() && !categoryList.getItems().contains(cat)) {
                categoryList.getItems().add(cat);
                if (!categories.contains(cat)) {
                    categories.add(cat);
                }
                categoryField.clear();
            }
        });

        Button removeCatBtn = new Button("Remove");
        removeCatBtn.setOnAction(e -> {
            String selected = categoryList.getSelectionModel().getSelectedItem();
            if (selected != null) categoryList.getItems().remove(selected);
        });

        HBox catButtons = new HBox(5, addCatBtn, removeCatBtn);

        grid.add(new Label("Task:"), 0, 0);
        grid.add(textField, 1, 0);
        grid.add(new Label("Priority:"), 0, 1);
        grid.add(priorityBox, 1, 1);
        grid.add(new Label("Due Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Categories:"), 0, 3);
        grid.add(categoryField, 1, 3);
        grid.add(catButtons, 1, 4);
        grid.add(categoryList, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                task.setText(textField.getText());
                task.setPriority(priorityBox.getValue());
                task.setDueDate(datePicker.getValue() != null ? datePicker.getValue().toString() : "");
                task.setCategories(new ArrayList<>(categoryList.getItems()));
                return task;
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        if (result.isPresent()) {
            taskListView.refresh();
            updateCategoryCombo();
            updateStats();
        }
    }

    private void filterTasks() {
        String filter = filterCombo.getValue();
        String priority = priorityCombo.getValue();
        String category = categoryCombo.getValue();
        String search = searchField.getText().toLowerCase();

        ObservableList<Task> filtered = FXCollections.observableArrayList();

        for (Task task : allTasks) {
            boolean matchFilter = filter.equals("All Tasks") ||
                    (filter.equals("Completed") && task.isCompleted()) ||
                    (filter.equals("Pending") && !task.isCompleted());

            boolean matchPriority = priority.equals("All Priorities") ||
                    task.getPriority().equals(priority);

            boolean matchCategory = category.equals("All Categories") ||
                    task.getCategories().contains(category);

            boolean matchSearch = search.isEmpty() ||
                    task.getText().toLowerCase().contains(search);

            if (matchFilter && matchPriority && matchCategory && matchSearch) {
                filtered.add(task);
            }
        }

        taskListView.setItems(filtered);
    }

    private void updateStats() {
        totalLabel.setText(String.valueOf(allTasks.size()));
        long completed = allTasks.stream().filter(Task::isCompleted).count();
        completedLabel.setText(String.valueOf(completed));
        pendingLabel.setText(String.valueOf(allTasks.size() - completed));
    }

    private void updateCategoryCombo() {
        String current = categoryCombo != null ? categoryCombo.getValue() : null;
        categoryCombo.getItems().clear();
        categoryCombo.getItems().add("All Categories");
        categoryCombo.getItems().addAll(categories);
        categoryCombo.setValue(current != null ? current : "All Categories");
    }

    private String formatDate(String date) {
        try {
            LocalDate ld = LocalDate.parse(date);
            return ld.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            return date;
        }
    }

    class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setGraphic(null);
                return;
            }

            HBox cell = new HBox(10);
            cell.setAlignment(Pos.CENTER_LEFT);
            cell.setPadding(new Insets(10));
            cell.getStyleClass().add("task-cell");

            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(task.isCompleted());
            checkBox.setOnAction(e -> {
                task.setCompleted(checkBox.isSelected());
                updateStats();
                updateItem(task, false);
            });

            Label textLabel = new Label(task.getText());
            textLabel.getStyleClass().add("task-text");
            if (task.isCompleted()) {
                textLabel.getStyleClass().add("completed");
            }
            HBox.setHgrow(textLabel, Priority.ALWAYS);

            Label priorityLabel = new Label(task.getPriority().toUpperCase());
            priorityLabel.getStyleClass().addAll("task-badge", "priority-" + task.getPriority().toLowerCase());

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox buttons = new HBox(5);
            Button editBtn = new Button("✏️");
            editBtn.getStyleClass().add("btn-icon");
            editBtn.setOnAction(e -> editTask(task));

            Button deleteBtn = new Button("🗑️");
            deleteBtn.getStyleClass().add("btn-icon");
            deleteBtn.setOnAction(e -> deleteTask(task));

            buttons.getChildren().addAll(editBtn, deleteBtn);

            cell.getChildren().addAll(checkBox, textLabel, priorityLabel, spacer, buttons);

            if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
                Label dateLabel = new Label("📅 " + formatDate(task.getDueDate()));
                dateLabel.getStyleClass().add("task-date");
                cell.getChildren().add(cell.getChildren().size() - 1, dateLabel);
            }

            if (!task.getCategories().isEmpty()) {
                Label catLabel = new Label(task.getCategories().get(0));
                catLabel.getStyleClass().add("task-category");
                cell.getChildren().add(cell.getChildren().size() - 1, catLabel);
            }

            setGraphic(cell);
        }
    }
}

class Task {
    private String text;
    private boolean completed;
    private String priority;
    private String dueDate;
    private ArrayList<String> categories;

    public Task(String text) {
        this.text = text;
        this.completed = false;
        this.priority = "Medium";
        this.dueDate = "";
        this.categories = new ArrayList<>();
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public ArrayList<String> getCategories() { return categories; }
    public void setCategories(ArrayList<String> categories) { this.categories = categories; }
}