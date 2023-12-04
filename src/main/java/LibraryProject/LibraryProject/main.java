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

        if (is_admin) {
            adminMenu(connection, id_user);
        } else {
            System.out.println("Regular user. Exiting the program. Goodbye!");
            System.exit(0);
        }

    }

    // *************** MENU - LOG IN / SIGN IN ***************
    public static int menu1(Connection connection) throws SQLException {
    	Scanner scanner = new Scanner(System.in);
        int user_id = -1;

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
                    user_id = logIn(connection);
                    if (user_id != -1) {
                        return user_id;  // Return user_id when successfully logged in
                    }
                    break;
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
    
    // *************** MENU - MAIN MENU *************************
    public static void mainMenu(Connection connection, int user_id) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n**********************************************\n| Welcome to the Book Management System!     |");
            System.out.println("|                                            |");
            System.out.println("|               1. Add a Book                |");
            System.out.println("|               2. View Friends' Books       |");
            System.out.println("|               3. Logout                    |");
            System.out.println("|               4. Exit                      |");
            System.out.println("**********************************************");

            System.out.print("\nChoose an option (1/2/3/4): ");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    addBook(connection, user_id);
                    break;
                case "2":
                    viewFriendsBooks(connection, user_id);
                    break;
                case "3":
                    // Logging out, break the loop to return to the login menu
                    loggedIn = false;
                    System.out.println("Logging out. Returning to the login menu.");
                    break;
                case "4":
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
        menu1(connection);

    }
    
    private static void adminMenu(Connection connection, int user_id) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean adminLoggedIn = true;

        while (adminLoggedIn) {
            System.out.println("\n**********************************************\n|         Welcome to the Admin Menu!         |");
            System.out.println("|                                            |");
            System.out.println("|               1. Add a Book                |");
            System.out.println("|               2. View Library              |");
            System.out.println("|               3. Add User                  |");
            System.out.println("|               4. Remove User               |");
            System.out.println("|               5. Logout                    |");
            System.out.println("|               6. Exit                      |");
            System.out.println("**********************************************");

            System.out.print("\nChoose an option (1/2/3/4/5/6): ");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    addBook(connection, user_id);
                    break;
                case "2":
                    viewFriendsBooks(connection, user_id);  // Change to viewLibrary instead of viewFriendsBooks
                    break;
                case "3":
                    AdminCreatesAccount(connection);
                    break;
                case "4":
                	 System.out.print("Enter the email to remove: ");
                	    String userEmailToRemove = scanner.next();
                	    Server.deleteUser(connection, userEmailToRemove);
                	    break;
                   
                case "5":
                    // Logging out, break the loop to return to the login menu
                    adminLoggedIn = false;
                    System.out.println("Logging out. Returning to the login menu.");
                    break;
                case "6":
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
        menu1(connection);
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
        	return Server.getUserID(connection, email);
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
    
    private static void AdminCreatesAccount(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        boolean flag = true, fFlag = true;
        
        System.out.println("Create an Account");
        System.out.print("Enter a name for the user: ");
        String name = scanner.next();
        System.out.print("Enter a surname for the user: ");
        String surname = scanner.next();
        System.out.print("Enter an email for the user: ");
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
        System.out.print("Enter a password for the user: ");
        String password = "a", confirmPassword = "b";
        while(!password.equals(confirmPassword)) {
	        password = scanner.next();
	        while(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
	            System.out.print("Password isn't secure enough! Try again: ");
	            password = scanner.next();
	    	}
	        System.out.print("Confirm a password for the user: ");
	        confirmPassword = scanner.next();
	        
	        if(!password.equals(confirmPassword)) {
        		System.out.print("Passwords don't match!\nEnter your password: ");
        	}
	    }
	        
        Server.insertUser(connection, email, name, surname, password);
    }

    private static void addBook(Connection connection, int user_id) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Add a Book");
        System.out.print("Enter the name of the book: ");
        String bookName = scanner.nextLine();
        
        System.out.print("Enter the author of the book: ");
        String author = scanner.nextLine();
        
        System.out.print("Enter the genre of the book (1-Fiction, 2-Non-fiction, etc.): ");
        int genre = scanner.nextInt();

        // Call the Server method to add the book to the database
        Server.addBook(connection, user_id, bookName, author, genre);
        
        System.out.println("Book added successfully!");
    }
    
    private static void viewFriendsBooks(Connection connection, int user_id) {
        // Call the Server method to fetch and display all books along with their owners
        Server.viewFriendsBooks(connection, user_id);
    }
    
    
    
    

}





