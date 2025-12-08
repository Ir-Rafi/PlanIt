
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import org.mindrot.jbcrypt.BCrypt; // This was missing

public class DatabaseUtility {
    private static final String URL = "jdbc:mysql://ununqd8usvy0wouy:GmDEehgTBjzyuPRuA8i8@b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc";
    private static final String USER = "ununqd8usvy0wouy";  // Replace with your MySQL username




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

    // Check if a user exists with both username and email
public static boolean userExists(String username, String email) {
    String query = "SELECT 1 FROM users WHERE username = ? AND email = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        stmt.setString(2, email);
        ResultSet rs = stmt.executeQuery();
        return rs.next();  // Returns true if such a user exists
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}


    // Check if an email already exists in the DB
    public static boolean emailExists(String email) {
        String query = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final String PASSWORD  = "GmDEehgTBjzyuPRuA8i8";  // Replace with your MySQL password

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

    public static int getUserId(String username) {
    String query = "SELECT id FROM users WHERE username = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1;
}

    public static String[] getServerAndClientNames(String username) {
        // Just return the username as both server and client name
        return new String[]{username, username};
    }


    public static String[] getUserDetails(String username) {
    String query = "SELECT username, email, department, session FROM users WHERE username = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String uname = rs.getString("username");
            String email = rs.getString("email");
            String dept = rs.getString("department");
            String session = rs.getString("session");

            return new String[]{ uname, email, dept, session };
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}


public static boolean updateUserProfile(String oldUsername, String newUsername,
                                        String newEmail, String department, String session) {

    String updateUser = "UPDATE users SET username = ?, email = ?, department = ?, session = ? WHERE username = ?";
    String updateOrganizer = "UPDATE organizers SET organizer_name = ? WHERE organizer_name = ?";

    try (Connection conn = getConnection()) {

        conn.setAutoCommit(false); // Start transaction

        try (PreparedStatement stmt1 = conn.prepareStatement(updateUser);
             PreparedStatement stmt2 = conn.prepareStatement(updateOrganizer)) {

            // Update users table
            stmt1.setString(1, newUsername);
            stmt1.setString(2, newEmail);
            stmt1.setString(3, department);
            stmt1.setString(4, session);
            stmt1.setString(5, oldUsername);
            stmt1.executeUpdate();

            // Update organizers table
            stmt2.setString(1, newUsername);
            stmt2.setString(2, oldUsername);
            stmt2.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}




}


   
