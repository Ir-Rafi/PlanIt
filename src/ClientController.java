import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;

    private Client client;
    private String displayName = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // auto-scroll
        vbox_messages.heightProperty().addListener((obs, oldVal, newVal) -> sp_main.setVvalue(1.0));

        button_send.setOnAction(this::handleSend);
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public void connectToServer(String host, int port) {

        if (displayName == null || displayName.isEmpty()) {
            System.out.println("Client username not set!");
            return;
        }

        new Thread(() -> {
            try {
                client = new Client(host, port, displayName);

                client.receiveMessages(vbox_messages);

                // Show login message on UI thread
                Platform.runLater(() ->
                        addLabel("You logged in as: " + displayName, vbox_messages)
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleSend(ActionEvent event) {
        String msg = tf_message.getText();
        if (msg.isEmpty() || client == null) return;

        String messageToSend = displayName + ": " + msg;

        // own bubble (right side)
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(5, 5, 5, 18));

        Text text = new Text(messageToSend);
        TextFlow tf = new TextFlow(text);
        tf.setStyle("-fx-background-color: rgb(15,125,242); -fx-background-radius: 20px;");
        tf.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.934, 0.945, 0.996));

        hbox.getChildren().add(tf);
        vbox_messages.getChildren().add(hbox);

        client.sendMessageToServer(messageToSend);
        tf_message.clear();
    }

    public static void addLabel(String messageFromServer, VBox vbox) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(253, 255, 235); -fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        hbox.getChildren().add(textFlow);

        Platform.runLater(() -> vbox.getChildren().add(hbox));
    }
}
