import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;

public class TaskStorage {
    private static final String FILE_PATH = "tasks.dat";

    public static void saveTasks(ObservableList<Task> tasks) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(tasks)); // convert ObservableList to ArrayList
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Task> loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return FXCollections.observableArrayList();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            ArrayList<Task> list = (ArrayList<Task>) ois.readObject();
            return FXCollections.observableArrayList(list);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}


