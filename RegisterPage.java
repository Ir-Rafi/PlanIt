import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class RegisterPage {

    public static void showRegisterPage(ArrayList<String> registeredNumbers, String fileName) {
        JFrame frame = new JFrame("Register Students");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Components
        JLabel label = new JLabel("Enter Registration Number:");
        JTextField regField = new JTextField(20);
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JTextArea listArea = new JTextArea();
        listArea.setEditable(false);
        listArea.setForeground(Color.BLACK);
        listArea.setFont(new Font("Courier New", Font.PLAIN, 18));
        JButton nextButton = new JButton("Next");
        nextButton.setForeground(Color.BLACK);
        nextButton.setFont(new Font("Verdana", Font.BOLD, 22));
        MainMenu.makeTransparent(nextButton);
        nextButton.setBounds(800, 600, 150, 50); // position at bottom


        // Wrap in a scroll pane
        JScrollPane scrollPane = new JScrollPane(listArea);
        scrollPane.setBounds(350, 340, 300, 200); // position and size of the visible area
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        // Fonts and colors
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Georgia", Font.ITALIC, 24));

        regField.setForeground(Color.BLACK);
        regField.setFont(new Font("Tahoma", Font.PLAIN, 20));
        regField.setBackground(Color.WHITE);

        addButton.setForeground(Color.BLACK);
        addButton.setFont(new Font("Verdana", Font.BOLD, 22));
        MainMenu.makeTransparent(addButton);

        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFont(new Font("Verdana", Font.BOLD, 22));
        MainMenu.makeTransparent(deleteButton);

        listArea.setForeground(Color.BLACK);
        listArea.setFont(new Font("Courier New", Font.PLAIN, 18));

        // Position components
        label.setBounds(340, 200, 360, 30);
        regField.setBounds(350, 240, 300, 30);
        addButton.setBounds(350, 280, 140, 40);
        deleteButton.setBounds(510, 280, 140, 40); // occupies Back button position
        listArea.setBounds(350, 340, 300, 150);

        // Show existing numbers in list
        for (int i = 0; i < registeredNumbers.size(); i++) {
            listArea.append((i + 1) + ". " + registeredNumbers.get(i) + "\n");
        }

        // Counter for new numbers
        final int[] counter = {registeredNumbers.size()};

        // Add button action
        addButton.addActionListener(e -> {
    String reg = regField.getText().trim();
    if (reg.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Please enter a registration number.");
    } else if (registeredNumbers.contains(reg)) {
        JOptionPane.showMessageDialog(frame, "This registration number is already added!");
        regField.setText("");
    } else {
        registeredNumbers.add(reg);
        counter[0]++;
        listArea.append(counter[0] + ". " + reg + "\n");
        regField.setText("");
        saveToFile(fileName, reg);
    }
    });

   nextButton.addActionListener(e -> {
    if (registeredNumbers.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "No registration numbers entered yet!");
    } else {
        frame.dispose();
        BlankPage.showBlankPage(); // open blank page for now
    }
});




        // Delete button action
        deleteButton.addActionListener(e -> {
            String reg = regField.getText().trim();
            if (reg.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a registration number to delete.");
            } else if (!registeredNumbers.contains(reg)) {
                JOptionPane.showMessageDialog(frame, "Registration number not found!");
            } else {
                registeredNumbers.remove(reg);
                saveAllToFile(fileName, registeredNumbers);

                // Refresh listArea
                listArea.setText("");
                for (int i = 0; i < registeredNumbers.size(); i++) {
                    listArea.append((i + 1) + ". " + registeredNumbers.get(i) + "\n");
                }

                JOptionPane.showMessageDialog(frame, "Registration number deleted.");
                regField.setText("");
                counter[0] = registeredNumbers.size(); // update counter
            }
        });

        // Add components to frame
        frame.add(label);
        frame.add(regField);
        frame.add(addButton);
        frame.add(deleteButton);
        frame.add(scrollPane);
        frame.add(nextButton);


        // Background
        JLabel background = BackgroundHelper.getBackground("background.jpg", 1000, 700);
        frame.add(background);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Save new registration to file
    private static void saveToFile(String fileName, String reg) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(reg + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving to file!");
        }
    }

    // Save all registrations to file (used for deletion)
    private static void saveAllToFile(String fileName, ArrayList<String> numbers) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String reg : numbers) {
                writer.write(reg + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving to file!");
        }
    }
}

