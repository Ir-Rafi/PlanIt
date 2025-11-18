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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        new Thread(() -> {
            try{
                server = new Server(new ServerSocket(1234));
                server.receiveMessageFromClient(vbox_messages);
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("Error creating server");
            }
        }, "Char-server-thread").start();

        // Automatically scroll to the bottom when new messages arrive
        vbox_messages.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            sp_main.setVvalue(1.0);
        });


        button_send.setOnAction((ActionEvent event) -> {
            String messageToSend = tf_message.getText();
            if(messageToSend.isEmpty() || server == null){
                return;
            }

            if (!messageToSend.isEmpty()) {

                // Message container (right-aligned)
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_RIGHT);
                hbox.setPadding(new Insets(5, 5, 5, 18));

                // Message text
                Text text = new Text(messageToSend);
                TextFlow textFlow = new TextFlow(text);

                // ✅ FIXED: Added missing semicolon between properties
                textFlow.setStyle(
                        "-fx-background-color: rgb(15,125,242); " +
                                "-fx-background-radius: 20px;"
                );

                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.934, 0.945, 0.996));

                hbox.getChildren().add(textFlow);
                vbox_messages.getChildren().add(hbox);

                // Send to client
                server.sendMessageToClient(messageToSend);

                // Clear input
                tf_message.clear();
            }
        });
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
