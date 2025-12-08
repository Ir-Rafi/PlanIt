import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;

    private Server server;
    private String serverName;
    private String loggedInUserName;

    public void setLoggedInUsername(String username) {
        this.loggedInUserName = username;  // Set the logged-in username
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vbox_messages.heightProperty().addListener((obs, oldVal, newVal) -> sp_main.setVvalue(1.0));
        button_send.setOnAction(this::sendMessage);
    }

    public void startServer() {
        new Thread(() -> {
            try {
               server = new Server(new ServerSocket(
        1234,
        50,
        java.net.InetAddress.getByName("0.0.0.0")
));
                server.receiveMessageFromClient(vbox_messages);
                Platform.runLater(() ->
                        addLabel("Server logged in as: " + loggedInUserName, vbox_messages)
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage(ActionEvent event) {
        String msg = tf_message.getText();
        if (msg.isEmpty() || server == null) return;

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(5, 5, 5, 18));

        Text text = new Text("" + msg);
        TextFlow tf = new TextFlow(text);
        tf.setStyle("-fx-background-color: rgb(15,125,242); -fx-background-radius: 20px;");
        tf.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.WHITE);

        hbox.getChildren().add(tf);
        vbox_messages.getChildren().add(hbox);

        server.sendMessageToClient("" + msg);
        tf_message.clear();
    }

    // Message from client (left-aligned)
    public static void addLabel(String messageFromClient, VBox vbox) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);

        // ✅ FIXED: Added missing semicolon
        textFlow.setStyle(
                "-fx-background-color: rgb(253,255,235); " +
                        "-fx-background-radius: 20px;"
        );

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hbox.getChildren().add(textFlow);

        Platform.runLater(() -> vbox.getChildren().add(hbox));
    }
}
