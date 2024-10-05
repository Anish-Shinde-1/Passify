package com.passify.model;

import com.passify.utils.Hashing;
import com.passify.utils.SaltGenerator;
import com.passify.utils.Encryption;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) throws SQLException {
        this.connection = connection;
    }

    // Generate a new encryption key
    private SecretKey generateEncryptionKey() throws Exception {
        return Encryption.generateKey(); // Generate a new secret key
    }

    // Create a new user
    public UserModel createUser(String userName, String password, String userEmail) {
        // Generate salt and hash the password
        byte[] salt = SaltGenerator.generateSalt();
        String hashSalt = SaltGenerator.getBase64EncodedSalt(salt);
        String hashedPassword;
        try {
            hashedPassword = Hashing.generateHash512(password, hashSalt);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null; // Return null on failure
        }

        // Generate a new encryption key
        SecretKey secretKey;
        try {
            secretKey = generateEncryptionKey();
        } catch (Exception e) {
            System.err.println("Error generating encryption key: " + e.getMessage());
            return null; // Return null on failure
        }

        // Insert the new user into the User table
        String insertUserSQL = "INSERT INTO User (user_id, user_name, hashed_password, hash_salt, user_email, encryption_key, created_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            String userId = UUID.randomUUID().toString();
            pstmt.setString(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, hashedPassword); // Store the hashed password
            pstmt.setString(4, hashSalt);
            pstmt.setString(5, userEmail);
            pstmt.setString(6, Base64.getEncoder().encodeToString(secretKey.getEncoded())); // Store the base64-encoded encryption key
            pstmt.executeUpdate();

            return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, Base64.getEncoder().encodeToString(secretKey.getEncoded()), new Timestamp(System.currentTimeMillis()), null);
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return null; // Return null on failure
        }
    }

    // Verify user password
    public boolean verifyUserPassword(String userId, String enteredPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<UserModel> user = getUserById(userId);
        if (user.isPresent()) {
            UserModel userModel = user.get();
            return Hashing.verifyHash(enteredPassword, userModel.getHashedPassword(), userModel.getHashSalt());
        }
        return false; // User not found
    }

    // Retrieve a user by ID (returns Optional)
    public Optional<UserModel> getUserById(String userId) {
        String selectSQL = "SELECT * FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultToUserModel(rs));
            } else {
                System.err.println("User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
        }
        return Optional.empty(); // Return empty Optional if user not found
    }

    // Retrieve a user by email (returns Optional)
    public Optional<UserModel> getUserByEmail(String userEmail) {
        String selectSQL = "SELECT * FROM User WHERE user_email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultToUserModel(rs));
            } else {
                System.err.println("User not found with email: " + userEmail);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by email: " + e.getMessage());
        }
        return Optional.empty(); // Return empty Optional if user not found
    }

    // Update a user entry
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
            System.err.println("Error updating user: " + e.getMessage());
            return false; // Return false on failure
        }
    }

    // Delete a user entry
    public boolean deleteUser(String userId) {
        String deleteSQL = "DELETE FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Return true if deletion was successful
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false; // Return false on failure
        }
    }

    // Helper function to map a result set row to a UserModel object
    private UserModel mapResultToUserModel(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String userName = rs.getString("user_name");
        String hashedPassword = rs.getString("hashed_password");
        String hashSalt = rs.getString("hash_salt");
        String userEmail = rs.getString("user_email");
        String encryptionKey = Base64.getEncoder().encodeToString(rs.getBytes("encryption_key")); // Fetch encryption_key and encode it
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at"); // Get updated_at directly as Timestamp
        return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, encryptionKey, createdAt, updatedAt);
    }
}
