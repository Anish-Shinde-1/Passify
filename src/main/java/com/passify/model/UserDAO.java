package com.passify.model;

import com.passify.utils.Hashing;
import com.passify.utils.SaltGenerator;
import com.passify.utils.Encryption;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class UserDAO {

    private final Connection connection;

    // Constructor to initialize the database connection
    public UserDAO(Connection connection) throws SQLException {
        this.connection = connection;
    }

    // Create a new user and store in the database
    public UserModel createUser(String userName, String password, String userEmail) {
        byte[] salt = SaltGenerator.generateSalt(); // Generate salt for password
        String hashSalt = SaltGenerator.getBase64EncodedSalt(salt); // Base64 encode salt
        String hashedPassword;
        try {
            hashedPassword = Hashing.generateHash512(password, hashSalt); // Hash the password
        } catch (Exception e) {
            return null; // Return null if password hashing fails
        }

        SecretKey secretKey;
        try {
            secretKey = Encryption.generateKey(); // Generate AES encryption key
        } catch (Exception e) {
            return null; // Return null if encryption key generation fails
        }

        String insertUserSQL = "INSERT INTO User (user_id, user_name, hashed_password, hash_salt, user_email, encryption_key, created_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            String userId = UUID.randomUUID().toString(); // Generate unique user ID
            pstmt.setString(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, hashedPassword); // Store hashed password
            pstmt.setString(4, hashSalt); // Store salt
            pstmt.setString(5, userEmail);
            pstmt.setString(6, Base64.getEncoder().encodeToString(secretKey.getEncoded())); // Encode encryption key
            pstmt.executeUpdate(); // Execute the insert query
            return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, secretKey, new Timestamp(System.currentTimeMillis()), null); // Return created user
        } catch (SQLException e) {
            return null; // Return null if insertion fails
        }
    }

    // Verify if the entered password matches the stored password hash
    public boolean verifyUserPassword(String userId, String enteredPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<UserModel> user = getUserById(userId); // Retrieve user by ID
        if (user.isPresent()) {
            return Hashing.verifyHash(enteredPassword, user.get().getHashedPassword(), user.get().getHashSalt()); // Verify entered password
        }
        return false; // Return false if user is not found
    }

    // Retrieve a user by their unique user ID
    public Optional<UserModel> getUserById(String userId) {
        String selectSQL = "SELECT * FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userId); // Set user ID parameter
            ResultSet rs = pstmt.executeQuery(); // Execute query
            if (rs.next()) {
                return Optional.of(mapResultToUserModel(rs)); // Map result to UserModel
            }
        } catch (SQLException e) {
            return Optional.empty(); // Return empty if user not found
        }
        return Optional.empty();
    }

    // Retrieve a user by their email address
    public Optional<UserModel> getUserByEmail(String userEmail) {
        String selectSQL = "SELECT * FROM User WHERE user_email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userEmail); // Set email parameter
            ResultSet rs = pstmt.executeQuery(); // Execute query
            if (rs.next()) {
                return Optional.of(mapResultToUserModel(rs)); // Map result to UserModel
            }
        } catch (SQLException e) {
            return Optional.empty(); // Return empty if user not found
        }
        return Optional.empty();
    }

    // Update an existing user's details in the database
    public boolean updateUser(String userId, String userName, String hashedPassword, String hashSalt, String userEmail) {
        String updateSQL = "UPDATE User SET user_name = ?, hashed_password = ?, hash_salt = ?, user_email = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, hashedPassword); // Update password hash
            pstmt.setString(3, hashSalt); // Update salt
            pstmt.setString(4, userEmail); // Update email
            pstmt.setString(5, userId); // Specify user ID to update
            return pstmt.executeUpdate() > 0; // Return true if update succeeds
        } catch (SQLException e) {
            return false; // Return false if update fails
        }
    }

    // Delete a user from the database by their user ID
    public boolean deleteUser(String userId) {
        String deleteSQL = "DELETE FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, userId); // Set user ID to delete
            return pstmt.executeUpdate() > 0; // Return true if deletion succeeds
        } catch (SQLException e) {
            return false; // Return false if deletion fails
        }
    }

    // Map a ResultSet row to a UserModel object
    private UserModel mapResultToUserModel(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String userName = rs.getString("user_name");
        String hashedPassword = rs.getString("hashed_password");
        String hashSalt = rs.getString("hash_salt");
        String userEmail = rs.getString("user_email");
        String encryptionKeyString = rs.getString("encryption_key");
        SecretKey encryptionKey = null;

        if (encryptionKeyString != null) {
            byte[] decodedKey = Base64.getDecoder().decode(encryptionKeyString); // Decode encryption key
            encryptionKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); // Convert to SecretKey
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, encryptionKey, createdAt, updatedAt); // Return mapped UserModel
    }
}
