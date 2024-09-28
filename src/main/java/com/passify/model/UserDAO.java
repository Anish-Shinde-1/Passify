package com.passify.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public UserModel createUser(String userName, String hashedPassword, String hashSalt, String userEmail) {
        String insertSQL = "INSERT INTO User (user_id, user_name, hashed_password, hash_salt, user_email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            String userId = UUID.randomUUID().toString();
            pstmt.setString(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, hashSalt);
            pstmt.setString(5, userEmail);
            pstmt.executeUpdate();
            return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, LocalDateTime.now(), null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Return null on failure
        }
    }

    public UserModel getUserById(String userId) {
        String selectSQL = "SELECT * FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultToUserModel(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found
    }

    public UserModel getUserByEmail(String userEmail) {
        String selectSQL = "SELECT * FROM User WHERE user_email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultToUserModel(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found
    }

    public boolean updateUser(String userId, String userName, String hashedPassword, String hashSalt, String userEmail) {
        String updateSQL = "UPDATE User SET user_name = ?, hashed_password = ?, hash_salt = ?, user_email = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, hashSalt);
            pstmt.setString(4, userEmail);
            pstmt.setString(5, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Return true if update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on failure
        }
    }

    public boolean deleteUser(String userId) {
        String deleteSQL = "DELETE FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Return true if deletion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on failure
        }
    }

    private UserModel mapResultToUserModel(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String userName = rs.getString("user_name");
        String hashedPassword = rs.getString("hashed_password");
        String hashSalt = rs.getString("hash_salt");
        String userEmail = rs.getString("user_email");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, createdAt, updatedAt);
    }

//    private static final String DB_URL = "jdbc:mysql://localhost:3306/demopasswordmanager"; // Replace with your DB URL
//    private static final String DB_USER = "root"; // Replace with your DB username
//    private static final String DB_PASSWORD = "anish2004"; // Replace with your DB password
//
//    public static void main(String[] args) {
//        // Database connection
//        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
//            UserDAO userDAO = new UserDAO(connection);
//
//            // Create a new user
//            UserModel newUser = userDAO.createUser("JohnDoe", "hashedPassword123", "salt123", "john.doe@example.com");
//            System.out.println("User Created: " + newUser);
//            Thread.sleep(30000);
//
//            // Retrieve the user
//            UserModel retrievedUser = userDAO.getUserById(newUser.getUserId());
//            System.out.println("User Retrieved: " + retrievedUser);
//            Thread.sleep(30000);
//
//            // Update the user
//            boolean updateSuccess = userDAO.updateUser(retrievedUser.getUserId(), "JaneDoe", "newHashedPassword456", "newSalt456", "jane.doe@example.com");
//            if (updateSuccess) {
//                UserModel updatedUser = userDAO.getUserById(retrievedUser.getUserId());
//                System.out.println("User Updated: " + updatedUser);
//            } else {
//                System.out.println("Failed to update user.");
//            }
//
//            // Delete the user
//            boolean deleteSuccess = userDAO.deleteUser(retrievedUser.getUserId());
//            System.out.println("User Deleted: " + deleteSuccess);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
