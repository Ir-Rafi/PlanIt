package com.example.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Try to load from classpath first
        URL fxmlUrl = Main.class.getResource("/com/example/dashboard/dashboard.fxml");

        // Fallback: load directly from project resources folder (useful when running without resources on classpath)
        if (fxmlUrl == null) {
            File f = new File("src/main/resources/com/example/dashboard/dashboard.fxml");
            if (f.exists()) {
                fxmlUrl = f.toURI().toURL();
            } else {
                throw new RuntimeException("Cannot find dashboard.fxml on classpath or at: " + f.getAbsolutePath());
            }
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 600);

        // CSS: try classpath first, fallback to filesystem
        URL cssUrl = Main.class.getResource("/com/example/dashboard/style.css");
        if (cssUrl == null) {
            File cssFile = new File("src/main/resources/com/example/dashboard/style.css");
            if (cssFile.exists()) {
                scene.getStylesheets().add(cssFile.toURI().toString());
            } else {
                System.err.println("Warning: style.css not found on classpath or in src/main/resources/com/example/dashboard/");
            }
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("User Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

