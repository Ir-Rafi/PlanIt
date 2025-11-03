
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import org.mindrot.jbcrypt.BCrypt;

public class DatabaseUtility {
    private static final String URL = "jdbc:mysql://localhost:3306/db";
    private static final String USER = "root";  // Replace with your MySQL username
    

    

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to check if a user exists (used in login)
    public static boolean checkUserExists(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                // Compare plain password with the stored hashed password
                return BCrypt.checkpw(password, storedHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to register a new user (hashes password before saving)
    public static boolean registerUser(String username, String email, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);

            int result = stmt.executeUpdate();
            return result > 0;  // Returns true if a new user is inserted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if username exists
public static boolean userExists(String username) {
    String query = "SELECT 1 FROM users WHERE username = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return false;
}

private static final String PASSWORD  = "iambasic";  // Replace with your MySQL password

// Update password for a user
public static boolean updatePassword(String username, String newPassword) {
    String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    String query = "UPDATE users SET password = ? WHERE username = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, hashedPassword);
        stmt.setString(2, username);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

}
