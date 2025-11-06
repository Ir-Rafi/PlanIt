import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewerView {

    public ViewerView(Stage stage, Scene eventsScene) {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));

        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Label title = new Label("Viewer Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #fff; -fx-font-weight: bold;");

        Label info = new Label("Event Details:\n📅 Date: 15th Nov 2025\n📍 Venue: City Convention Center");
        info.setStyle("-fx-font-size: 16px; -fx-text-fill: #EAEAEA; -fx-alignment: center;");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #2E86DE; -fx-text-fill: white; -fx-background-radius: 10;");
        backBtn.setOnAction(e -> stage.setScene(eventsScene));

        VBox card = new VBox(20, info);
        card.setAlignment(Pos.CENTER);
        card.prefWidthProperty().bind(layout.widthProperty().multiply(0.4));
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #2C3E50; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 4);");

        layout.getChildren().addAll(title, card, backBtn);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("cssfororganizer.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
