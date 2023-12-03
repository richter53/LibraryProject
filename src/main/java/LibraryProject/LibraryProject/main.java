package LibraryProject.LibraryProject;

import org.mindrot.jbcrypt.BCrypt;
import com.mysql.cj.protocol.Resultset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int id_user;

        Connection connection = Server.establishConnection("jdbc:mysql://localhost:3306/mydb", "root", "");
        
        id_user = menu1(connection);
        boolean is_admin = Server.checkAdmin(connection, id_user);
        System.out.println(is_admin);
        
        /*String query = "SELECT * FROM users WHERE id_user = ?";
    	resultset 
    	premenna = Resultset.getboolean("spravca");
    	if()*/

    }

    // *************** MENU - LOG IN / SIGN IN ***************
    public static int menu1(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n**********************************************\n| Welcome to the User Authentication System! |");
            System.out.println("|                                            |");
            System.out.println("|               1. Log In                    |");
            System.out.println("|           2. Create Account                |");
            System.out.println("|                3. Exit                     |");
            System.out.println("**********************************************");

            System.out.print("\nChoose an option (1/2/3): ");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    int user_id = logIn(connection);
                    if (user_id == -1) {
                    	break;
                    } else {
                    	return user_id;
                    }
                case "2":
                    createAccount(connection);
                    break;
                case "3":
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }
    private static int logIn(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Log In");
        System.out.print("Enter your email: ");
        String email = scanner.next();
        while(!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            System.out.print("Wrong format try again: ");
        	email = scanner.next();
    	}
        System.out.print("Enter your password: ");
        String password = scanner.next();
        boolean logged = Server.verifyLogIn(connection, email, password);
        if(logged) {
        	String query = "SELECT * FROM users WHERE email = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, email);

	            // Execute the query
	            try (ResultSet user = preparedStatement.executeQuery()) {
	                if (user.next()) {
	                	System.out.print("User returned.");
	                	return user.getInt("id_user");
	                } else {
	                    System.out.println("User doesn't exist.");
	                }
	            }
	        }
	        catch (SQLException e) {
	            System.err.println("Error executing the query: " + e.getMessage());
	            e.printStackTrace();
	        }
        }
		return -1;
        
    }
    
    private static void createAccount(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        boolean flag = true, fFlag = true;
        
        System.out.println("Create Account");
        System.out.print("Enter your name: ");
        String name = scanner.next();
        System.out.print("Enter your surname: ");
        String surname = scanner.next();
        System.out.print("Enter your email: ");
        String email = scanner.next();
        while(flag) {
        	if(fFlag) {
        		fFlag = false;
        	}else {
            	email = scanner.next();	
        	}
	        while(!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
	            System.out.print("Wrong format try again: ");
	        	email = scanner.next();
	    	}
	        flag = Server.verifyEmailUse(connection, email);
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
	        
        Server.insertUser(connection, email, name, surname, password);
    }


}





