package LibraryProject.LibraryProject;
import java.sql.Statement;
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

    public static int getUserID(Connection connection, String email) {
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
        return -1;
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
    
    public static void addBook(Connection connection, int user_id, String bookName, String author, int genre) {
        try {
            String query = "INSERT INTO books (nazov, autor, zaner, id_user) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, bookName);
                preparedStatement.setString(2, author);
                preparedStatement.setInt(3, genre);
                preparedStatement.setInt(4, user_id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void viewFriendsBooks(Connection connection, int user_id) {
        try {
        	 String query = "SELECT b.nazov AS book_name, b.autor AS book_author, " +
                     "CONCAT(u.meno, ' ', u.priezvisko) AS owner_name " +
                     "FROM books b " +
                     "JOIN users u ON b.id_user = u.id_user " +
                     "ORDER BY b.nazov";

      try (Statement statement = connection.createStatement()) {
          ResultSet resultSet = statement.executeQuery(query);

          if (resultSet.next()) {
              System.out.println("Library - Books and Owners:");
              System.out.println("\n************************************************************\n|                 Welcome to the Library!                  |");
              do {
                  String bookName = resultSet.getString("book_name");
                  String bookAuthor = resultSet.getString("book_author");
                  String ownerName = resultSet.getString("owner_name");
                  System.out.println("|                                                          |");
                  System.out.println("|    Book: " + bookName + " by " + bookAuthor + ", Owner: " + ownerName + "    |");
                  System.out.println("|                                                          |");
              } while (resultSet.next());
              System.out.println("************************************************************");

          } else {
              System.out.println("The library is empty.");
          }
      }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    public static void deleteUser(Connection connection, String userEmailToRemove) {
    	int userIdToRemove = getUserID(connection, userEmailToRemove);

        if (userIdToRemove != -1) {
            // Delete associated books
            deleteBooksForUser(connection, userIdToRemove);

            String query = "DELETE FROM users WHERE id_user = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userIdToRemove);

                // Execute the update query
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User removed successfully.");
                } else {
                    System.out.println("User removal failed.");
                }
            } catch (SQLException e) {
                System.err.println("Error executing the query: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("User not found.");
        }
    }
    
    private static void deleteBooksForUser(Connection connection, int userId) {
        String query = "DELETE FROM books WHERE id_user = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            // Execute the update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting books for the user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    
    }
    
    
    
