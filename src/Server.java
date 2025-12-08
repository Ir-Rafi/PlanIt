import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private FileWriter fileWriter;

    private String serverName;
    private String clientName;
    private String ChatLogFile;

    public Server(ServerSocket serverSocket) {
        try {
            this.serverSocket = serverSocket;

            System.out.println("Server waiting for client...");
            this.socket = serverSocket.accept();
            System.out.println("Client connected!");

            // Initialize IO
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Read username sent from client
            String clientUsername = bufferedReader.readLine();
            System.out.println("Client username: " + clientUsername);

            // Fetch serverName & clientName based on username
            String[] names = DatabaseUtility.getServerAndClientNames(clientUsername);
            this.serverName = names[0];
            this.clientName = names[1];

            System.out.println("Fetched from DB -> ServerName: " + serverName + " | ClientName: " + clientName);

            String chatDir = "Chat";
            File dir = new File(chatDir);
            if (!dir.exists()) {
                dir.mkdirs();  // Creates directory if it doesn't exist
            }

            // Create chat log file
            this.ChatLogFile = "Chat/" + serverName + "_" + clientName + "_chat_log.dat";
            this.fileWriter = new FileWriter(ChatLogFile, true);

            loadPreviousMessages();

            // Send welcome message to client
            bufferedWriter.write("Server logged in as: " + serverName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            System.out.println("Error creating server");
            e.printStackTrace();
        }
    }

    private void loadPreviousMessages(){
        try{
            File chatFile = new File(ChatLogFile);
            if(chatFile.exists()){
                System.out.println("Chat log file found. Loading previous messages...");

                bufferedWriter.newLine();
                bufferedWriter.flush();

                BufferedReader filereader = new BufferedReader(new FileReader(chatFile));
                String line;
                int messagecount = 0;
                while((line=filereader.readLine())!=null){
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    messagecount++;
                }
                filereader.close();

                System.out.println("Loaded "+messagecount+" previous messagges");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } else{
                System.out.println("No previous chat log found. Starting fresh.");
            }
        } catch(IOException e){
            System.out.println("Error loading previous messages");
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(String messageToClient){
        try{
            bufferedWriter.write(messageToClient);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            fileWriter.write("Server: "+messageToClient+ "\n");
            fileWriter.flush();

        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Error sending message to client");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMessageFromClient(VBox vbox){
        Thread receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()) {
                    try {
                        String messageFromClient = bufferedReader.readLine();
                        if(messageFromClient == null) break;
                        ChatController.addLabel(messageFromClient, vbox);

                        fileWriter.write("Client: "+messageFromClient+"\n");
                        fileWriter.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error receiving message from the client");
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
            if(fileWriter != null){
                fileWriter.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}

