import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DiscussionServer {
    private static final int PORT = 45456;
    private static final String THREADS_FILE = "discussion_threads.dat";
    private static Map<String, DiscussionThread> threads = new ConcurrentHashMap<>();
    private static int currentEventId = -1;

    public static void main(String[] args) {
        System.out.println("Discussion Server starting on port " + PORT);
        
        // Load existing threads from file
        loadThreadsFromFile();
        
        // Add shutdown hook to save threads when server stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server shutting down, saving threads...");
            saveThreadsToFile();
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server ready! Loaded " + threads.size() + " threads from file.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connected");
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Save threads on exit
            saveThreadsToFile();
        }
    }

    private static void handleClient(Socket socket) {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            String command = (String) in.readObject();
            System.out.println("Received command: " + command);

            if (command.equals("SET_EVENT_CONTEXT")) {
                int eventId = (int) in.readObject();
                setEventContext(eventId);
                out.writeObject("CONTEXT_SET");
                System.out.println("Event context set to: " + eventId);
            }
            
            else if (command.equals("LOAD_DISCUSSIONS")) {
                List<DiscussionThread> threadList = new ArrayList<>(threads.values());
                out.writeObject(threadList);
                System.out.println("Sent " + threadList.size() + " threads");
            }

            else if (command.equals("CREATE_THREAD")) {
                String username = (String) in.readObject();
                String role = (String) in.readObject();
                String title = (String) in.readObject();
                String content = (String) in.readObject();

                String threadId = UUID.randomUUID().toString();
                DiscussionThread thread = new DiscussionThread(threadId, title, content, username, role);
                threads.put(threadId, thread);

                // Save to file immediately after creating thread
                saveThreadsToFile();

                out.writeObject("SUCCESS");
                System.out.println("Created thread: " + title + " by " + username);
                System.out.println("Total threads: " + threads.size());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void setEventContext(int eventId) {
        if (currentEventId != eventId) {
            saveThreadsToFile(); // Save current context
            threads.clear();     // Clear memory
            currentEventId = eventId; // Set new context
            loadThreadsFromFile(); // Load new context
        }
    }

    /**
     * Save all threads to file (Global context)
     * POLYMORPHISM: Method Overloading - No parameters
     */
    private static synchronized void saveThreadsToFile() {
        if (currentEventId == -1) {
            // Global context - save to default file
            saveToFile(THREADS_FILE);
        } else {
            // Event context - call overloaded method
            saveThreadsToFile(currentEventId);
        }
    }

    /**
     * Save all threads to file (Event-specific context)
     * POLYMORPHISM: Method Overloading - With eventId parameter
     */
    private static synchronized void saveThreadsToFile(int eventId) {
        String filename = "event_" + eventId + "_" + THREADS_FILE;
        saveToFile(filename);
    }

    private static void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new HashMap<>(threads));
            System.out.println("✅ Saved " + threads.size() + " threads to file: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Error saving threads to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load threads from file (Global context)
     * POLYMORPHISM: Method Overloading - No parameters
     */
    @SuppressWarnings("unchecked")
    private static void loadThreadsFromFile() {
        if (currentEventId == -1) {
            // Global context - load from default file
            loadFromFile(THREADS_FILE);
        } else {
            // Event context - call overloaded method
            loadThreadsFromFile(currentEventId);
        }
    }

    /**
     * Load threads from file (Event-specific context)
     * POLYMORPHISM: Method Overloading - With eventId parameter
     */
    @SuppressWarnings("unchecked")
    private static void loadThreadsFromFile(int eventId) {
        String filename = "event_" + eventId + "_" + THREADS_FILE;
        loadFromFile(filename);
    }

    @SuppressWarnings("unchecked")
    private static void loadFromFile(String filename) {
        File file = new File(filename);
        
        if (!file.exists()) {
            System.out.println("No existing threads file found: " + filename + ". Starting with empty thread list.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Map<String, DiscussionThread> loadedThreads = (Map<String, DiscussionThread>) ois.readObject();
            threads.putAll(loadedThreads);
            System.out.println("✅ Loaded " + threads.size() + " threads from file: " + filename);
            
            // Print thread titles for verification
            for (DiscussionThread thread : threads.values()) {
                System.out.println("  - " + thread.title + " by " + thread.authorUsername);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading threads from file: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Starting with empty thread list.");
        }
    }

    /**
     * Get thread count (for testing)
     */
    public static int getThreadCount() {
        return threads.size();
    }

    /**
     * Clear all threads (for testing)
     */
    public static void clearThreads() {
        threads.clear();
        saveThreadsToFile();
        System.out.println("All threads cleared.");
    }
}