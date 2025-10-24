import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LoginPage {

    public static void showLoginPage(ArrayList<String> registeredNumbers) {
        JFrame frame = new JFrame("Login");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Components
        JLabel label = new JLabel("Enter your Registration Number:");
        JTextField regField = new JTextField(20);
        JButton loginButton = new JButton("Check Access");
        JButton backButton = new JButton("Back");

        // Fonts and colors
        label.setFont(new Font("Georgia", Font.ITALIC, 28));
        label.setForeground(Color.BLACK);

        regField.setFont(new Font("Tahoma", Font.PLAIN, 22));
        regField.setForeground(Color.BLACK);
        regField.setBackground(Color.WHITE);

        loginButton.setFont(new Font("Verdana", Font.BOLD, 18));
        loginButton.setForeground(Color.BLACK);
        MainMenu.makeTransparent(loginButton);

        backButton.setFont(new Font("Verdana", Font.BOLD, 22));
        backButton.setForeground(Color.BLACK);
        MainMenu.makeTransparent(backButton);

        // Position components
        label.setBounds(280, 250, 500, 40);
        regField.setBounds(300, 300, 400, 40);
        loginButton.setBounds(300, 360, 180, 50);
        backButton.setBounds(520, 360, 180, 50);

        // Button actions
        loginButton.addActionListener(e -> {
            String reg = regField.getText().trim();
            if (registeredNumbers.contains(reg)) {
                // Show welcome message as a pop-up dialog
                JOptionPane.showMessageDialog(frame,
                        "🎉 Welcome to the Event Page as an Organizer, " + reg + "!",
                        "Welcome",
                        JOptionPane.INFORMATION_MESSAGE);

                // Open the next page after user closes the dialog
                frame.dispose();
                BlankPage.showBlankPage();

            } else {
                JOptionPane.showMessageDialog(frame,
                        "❌ Access Denied! You are not registered.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
            }
            regField.setText("");
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            MainMenu.showMainMenu();
        });

        // Add components
        frame.add(label);
        frame.add(regField);
        frame.add(loginButton);
        frame.add(backButton);

        // Background
        JLabel background = BackgroundHelper.getBackground("background.jpg", 1000, 700);
        frame.add(background);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}



