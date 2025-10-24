import javax.swing.*;
import java.awt.*;

public class BlankPage {
    public static void showBlankPage() {
        JFrame frame = new JFrame("Next Page");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Optional: label to show this is a blank page
        JLabel label = new JLabel("This is the next page (blank for now)");
        label.setFont(new Font("Georgia", Font.ITALIC, 28));
        label.setForeground(Color.BLACK);
        label.setBounds(200, 300, 600, 50);
        frame.add(label);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

