import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class chatWindows {

    // Server-side chat (Main Organizer)
    public static void openServerChat(String displayName) {
        try {
            FXMLLoader loader = new FXMLLoader(chatWindows.class.getResource("Chat.fxml"));
            Parent root = loader.load();

            ChatController controller = loader.getController();
            controller.setLoggedInUsername(displayName);
            controller.startServer();

            Stage stage = new Stage();
            stage.setTitle("Organizer Chat (Server) - " + displayName);
            stage.setScene(new Scene(root, 480, 600));
            stage.show();
            // ChatController.initialize() will create the Server and start listening on port 1234
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Client-side chat (Sub Organizer / Viewer)
    public static void openClientChat(String displayName) {
        try {
            // 1️⃣ Make sure the FXML is set directly in the constructor
            FXMLLoader loader = new FXMLLoader(
                    chatWindows.class.getResource("ChatClient.fxml")
            );

            // (Optional debug: uncomment if needed)
            // System.out.println("FXML URL = " + ChatWindows.class.getResource("ChatClient.fxml"));

            Parent root = loader.load();

            // 2️⃣ Get controller and set name + connect
            ClientController controller = loader.getController();
            controller.setDisplayName(displayName);
            controller.connectToServer("10.33.17.195", 1234); // or your server IP

            // 3️⃣ Open window
            Stage stage = new Stage();
            stage.setTitle("Chat - " + displayName);
            stage.setScene(new Scene(root, 480, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
