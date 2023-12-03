package LibraryProject.LibraryProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class Server {

    public static Connection establishConnection(String url, String username, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            System.err.println("Error establishing the database connection: " + e.getMessage());
            e.printStackTrace();
        }

        return connection;
    }
    
    public static boolean verifyLogIn(Connection connection, String email, String password) {

        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // User found, compare passwords
                    String storedPassword = resultSet.getString("password");
                    
                    if (BCrypt.checkpw(password, storedPassword)) {
                        System.out.println("Authentication successful!");
                        return true;
                    } else {
                        System.out.println("Authentication failed. Passwords do not match.");
                        return false;
                    }
                } else {
                    System.out.println("User not found.");
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
