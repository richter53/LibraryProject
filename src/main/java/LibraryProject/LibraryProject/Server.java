package LibraryProject.LibraryProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

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
                        System.out.println("Wrong password!");
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
    
    public static boolean verifyEmailUse(Connection connection, String email) {
        Scanner scanner = new Scanner(System.in);
        
    	String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                	System.out.print("E-mail already in use!\nTry a different email: ");
                	return true;
                } else {
                    return false;
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
    
    public static void insertUser(Connection connection, String email, String name, String surname, String password) {

        String query = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        	preparedStatement.setInt(1, 0);
        	preparedStatement.setString(2, email);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, surname);
            preparedStatement.setString(5, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            preparedStatement.setInt(6, 0);

            int affectedRows = preparedStatement.executeUpdate();
            System.out.println("\nAccount created successfully!");
        }
        catch (SQLException e) {
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }
    } 
    
    public static boolean checkAdmin(Connection connection, int user_id) {
        String query = "SELECT * FROM users WHERE id_user = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, user_id);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                	if(resultSet.getBoolean("spravca")) {
                    	return true;
                    }else return false;
                } else {
                    return false;
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