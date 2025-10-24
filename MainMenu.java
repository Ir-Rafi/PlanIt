import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class MainMenu {

    static ArrayList<String> registeredNumbers = new ArrayList<>();
    static String FILE_NAME = "registered.txt";

    public static void main(String[] args) {
        loadRegisteredNumbers(); // load saved numbers
        showMainMenu();
    }

    public static void loadRegisteredNumbers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                registeredNumbers.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("No previous data found. Starting fresh...");
        }
    }

    public static void showMainMenu() {
        JFrame frame = new JFrame("Student Access System");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Buttons
        JButton registerButton = new JButton("Register Students");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        registerButton.setForeground(Color.BLACK);
        loginButton.setForeground(Color.BLACK);
        exitButton.setForeground(Color.BLACK);

        registerButton.setFont(new Font("Arial", Font.BOLD, 20));
        loginButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));

        registerButton.setBounds(340, 200, 300, 50);
        loginButton.setBounds(340, 300, 300, 50);
        exitButton.setBounds(340, 400, 300, 50);

        // Make buttons transparent
        makeTransparent(registerButton);
        makeTransparent(loginButton);
        makeTransparent(exitButton);

        // Button actions
        registerButton.addActionListener(e -> {
            frame.dispose();
            RegisterPage.showRegisterPage(registeredNumbers, FILE_NAME);
        });

        loginButton.addActionListener(e -> {
            frame.dispose();
            LoginPage.showLoginPage(registeredNumbers);
        });

        exitButton.addActionListener(e -> System.exit(0));

        frame.add(registerButton);
        frame.add(loginButton);
        frame.add(exitButton);

        // Add background last
        JLabel background = BackgroundHelper.getBackground("background.jpg", 1000, 700);
        frame.add(background);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void makeTransparent(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);

    }
}

//javac *.java
//java MainMenu
