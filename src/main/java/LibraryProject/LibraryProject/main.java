package LibraryProject.LibraryProject;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int menuChoice;

        Connection connection = establishConnection("jdbc:mysql://localhost:3306/mydb", "root", "");
        
        menuChoice = menu(connection);
        

        
        
        
        //String query = "SELECT * FROM ";
        /*
        try (Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();	
                ResultSet resultSet = statement.executeQuery(query)) {

               // Print column headers
               for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                   System.out.print(resultSet.getMetaData().getColumnName(i) + "\t");
               }
               System.out.println();

               // Print data
               while (resultSet.next()) {
                   for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                       System.out.print(resultSet.getString(i) + "\t");
                   }
                   System.out.println();
               }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }*/
    }
    
    

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
    
    // *************** MENU - LOG IN / SIGN IN ***************
    public static int menu(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to the User Authentication System!");
            System.out.println("1. Log In");
            System.out.println("2. Create Account");
            System.out.println("3. Exit");

            System.out.print("Choose an option (1/2/3): ");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    boolean logged = logIn(connection);
                    if(logged) {
                    	return 1;
                    }
                    break;
                case "2":
                    createAccount(connection);
                    return 2;
                case "3":
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }
    private static boolean logIn(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Log In");
        System.out.print("Enter your email: ");
        String userEmail  = scanner.next();
        System.out.print("Enter your password: ");
        String passwordCheck = scanner.next();

        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userEmail);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // User found, compare passwords
                    String storedPassword = resultSet.getString("password");
                    
                    if (BCrypt.checkpw(passwordCheck, storedPassword)) {
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
    
    private static void createAccount(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Create Account");
        System.out.print("Enter your name: ");
        String name = scanner.next();
        System.out.print("Enter your surname: ");
        String surname = scanner.next();
        System.out.print("Enter your email: ");
        String email = scanner.next();
        while(!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            System.out.print("Wrong format try again: ");
        	email = scanner.next();
    	}
        System.out.print("Enter your password: ");
        String password = "a", confirmPassword = "b";
        while(!password.equals(confirmPassword)) {
	        password = scanner.next();
	        while(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
	            System.out.print("Password isn't secure enough! Try again: ");
	            password = scanner.next();
	    	}
	        System.out.print("Confirm your password: ");
	        confirmPassword = scanner.next();
	        
	        if(!password.equals(confirmPassword)) {
        		System.out.print("Passwords don't match!\nEnter your password: ");
        	}
	    }
	        

        String query = "INSERT INTO users VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        	preparedStatement.setInt(1, 0);
        	preparedStatement.setString(2, email);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, surname);
            preparedStatement.setString(5, BCrypt.hashpw(password, BCrypt.gensalt(12)));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User inserted successfully!");
            } else {
                System.out.println("Failed to insert user.");
            }
        }
        catch (SQLException e) {
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }
        
        
        
        // Perform account creation logic here
        if (password.equals(confirmPassword)) {
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Passwords do not match. Account creation failed.");
        }
    }


}





