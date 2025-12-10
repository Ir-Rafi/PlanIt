import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;


public class ProgressReportWindow {

    private static final String REPORTS_DIR = "reports";

    public void show() {
        Stage window = new Stage();
        window.setTitle("Progress Report");

        Label title = new Label("Progress Report Panel");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");

        Button createBtn = new Button("Create a New Report");
        Button viewBtn = new Button("View All Reports");
        Button backBtn = new Button("← Back");

        createBtn.setOnAction(e -> openEditor(window));
        viewBtn.setOnAction(e -> viewAllReports(window));
        backBtn.setOnAction(e -> window.close());

        VBox layout = new VBox(25, title, createBtn, viewBtn, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("root");

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(getClass().getResource("cssforreport.css").toExternalForm());
        window.setScene(scene);
       // window.setMaximized(true);
       window.setFullScreen(true);
window.setFullScreenExitHint("");

        window.show();
    }

    private void openEditor(Stage parentStage) {
        Stage editorStage = new Stage();
        editorStage.setTitle("Write Your Report");

        Label nameLabel = new Label("Enter Report Name:");
        nameLabel.getStyleClass().add("label");

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Event Progress - March");

        TextArea reportArea = new TextArea();
        reportArea.setPromptText("Write your progress report here...");
        reportArea.setWrapText(true);

        Button saveBtn = new Button("Save Report");
        Button backBtn = new Button("← Back");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("Error", "Please enter a report name!");
            } else {
                saveReport(name, reportArea.getText());
                editorStage.close();
            }
        });

        backBtn.setOnAction(e -> editorStage.close());

        HBox bottomButtons = new HBox(20, saveBtn, backBtn);
        bottomButtons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, nameLabel, nameField, reportArea, bottomButtons);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("root");

        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("cssforreport.css").toExternalForm());
        editorStage.setScene(scene);
        //editorStage.setMaximized(true);
        editorStage.setFullScreen(true);
editorStage.setFullScreenExitHint("");

        editorStage.show();
    }

    private void saveReport(String name, String text) {
        try {
            File dir = new File(REPORTS_DIR);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, name + ".txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(text);
            }

            showAlert("Success", "Report saved as '" + name + ".txt'");
        } catch (IOException e) {
            showAlert("Error", "Failed to save report!");
        }
    }

    private void viewAllReports(Stage parentStage) {
        Stage viewStage = new Stage();
        viewStage.setTitle("All Reports");

        ListView<String> fileList = new ListView<>();
        File folder = new File(REPORTS_DIR);

        if (folder.exists()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                fileList.getItems().add(file.getName());
            }
        }

        TextArea contentArea = new TextArea();
        contentArea.setEditable(false);
        contentArea.setPromptText("Select a report to view...");

        fileList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                try {
                    File file = new File(REPORTS_DIR, selected);
                    String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                    contentArea.setText(content);
                } catch (IOException e) {
                    contentArea.setText("Error loading report!");
                }
            }
        });

        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> viewStage.close());

        SplitPane splitPane = new SplitPane(fileList, contentArea);
        splitPane.setDividerPositions(0.3);

        VBox layout = new VBox(10, splitPane, backBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("root");

        Scene scene = new Scene(layout, 900, 600);
        scene.getStylesheets().add(getClass().getResource("cssforreport.css").toExternalForm());
        viewStage.setScene(scene);
        viewStage.setMaximized(true);
        viewStage.show();
    }

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Progress Report");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}