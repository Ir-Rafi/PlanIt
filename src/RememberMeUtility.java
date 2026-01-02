import java.io.*;
import java.util.Properties;

public class RememberMeUtility {
    private static final String FILE_PATH = "remember_me.properties";

    public static void saveUser(String username) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            Properties props = new Properties();
            props.setProperty("username", username);
            props.setProperty("remember", "true");
            props.store(fos, "Remember Me Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getRememberedUser() {
        try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
            Properties props = new Properties();
            props.load(fis);
            if ("true".equals(props.getProperty("remember"))) {
                return props.getProperty("username");
            }
        } catch (IOException e) {
            // File might not exist on first run, ignore
        }
        return null;
    }

    public static void clearRememberedUser() {
        File file = new File(FILE_PATH);
        if (file.exists()) file.delete();
    }
}
