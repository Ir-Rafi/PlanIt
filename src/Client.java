import javafx.scene.layout.VBox;
import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String host, int port, String clientUsername) {
        try {
            this.socket = new Socket(host, port);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferedWriter.write(clientUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Receive the server's logged-in message
            String serverMessage = bufferedReader.readLine();
            while ((serverMessage = bufferedReader.readLine()) != null) {
                System.out.println(serverMessage);
                // The first message is the welcome message, rest are previous chat messages
                // You can differentiate if needed
                break; // Only receive welcome message here, rest in receiveMessages
            }

        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }
    }

    public void sendMessageToServer(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }
    }

    public void receiveMessages(VBox vbox) {
        Thread receiverThread = new Thread(() -> {
            while (socket != null && socket.isConnected()) {
                try {
                    String msg = bufferedReader.readLine();
                    if (msg == null) break;
                    ClientController.addLabel(msg, vbox);
                } catch (IOException e) {
                    e.printStackTrace();
                    closeEverything();
                    break;
                }
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
