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

    private final Connection connection; // The connection object for interacting with the database

    // Constructor to initialize the UserDAO with a database connection
    public UserDAO(Connection connection) throws SQLException {
        this.connection = connection;
    }

    /**
     * Create a new user and insert the details into the database.
     * @param userName  The user's name
     * @param password  The user's password (plaintext)
     * @param userEmail The user's email address
     * @return UserModel instance representing the newly created user or null if creation fails
     */
    public UserModel createUser(String userName, String password, String userEmail) {
        // Generate salt and hash the user's password
        byte[] salt = SaltGenerator.generateSalt();  // Generate a random salt
        String hashSalt = SaltGenerator.getBase64EncodedSalt(salt);  // Convert salt to Base64 string
        String hashedPassword;
        try {
            // Hash the password using PBKDF2WithHmacSHA512 algorithm
            hashedPassword = Hashing.generateHash512(password, hashSalt);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null; // Return null if hashing fails
        }

        // Generate a new AES encryption key for the user
        SecretKey secretKey;
        try {
            secretKey = Encryption.generateKey();  // Generate a 256-bit AES key
        } catch (Exception e) {
            System.err.println("Error generating encryption key: " + e.getMessage());
            return null; // Return null if encryption key generation fails
        }

        // SQL statement to insert the new user into the User table
        String insertUserSQL = "INSERT INTO User (user_id, user_name, hashed_password, hash_salt, user_email, encryption_key, created_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            String userId = UUID.randomUUID().toString();  // Generate a unique user ID
            pstmt.setString(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, hashedPassword);  // Store the hashed password
            pstmt.setString(4, hashSalt);        // Store the Base64 encoded salt
            pstmt.setString(5, userEmail);
            // Store the Base64-encoded encryption key
            pstmt.setString(6, Base64.getEncoder().encodeToString(secretKey.getEncoded()));
            pstmt.executeUpdate();  // Execute the SQL statement

            // Return a UserModel object representing the new user
            return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, secretKey, new Timestamp(System.currentTimeMillis()), null);
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return null; // Return null if the user creation fails
        }
    }

    /**
     * Verify if the entered password matches the stored hashed password for the user.
     * @param userId          The ID of the user
     * @param enteredPassword The password entered by the user (plaintext)
     * @return true if the password is correct, false otherwise
     */
    public boolean verifyUserPassword(String userId, String enteredPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Retrieve the user by their ID
        Optional<UserModel> user = getUserById(userId);
        if (user.isPresent()) {
            // If user exists, verify the entered password against the stored hash and salt
            UserModel userModel = user.get();
            return Hashing.verifyHash(enteredPassword, userModel.getHashedPassword(), userModel.getHashSalt());
        }
        return false; // Return false if user is not found
    }

    /**
     * Retrieve a user by their unique user ID.
     * @param userId The ID of the user to retrieve
     * @return An Optional containing the UserModel if found, or empty if not
     */
    public Optional<UserModel> getUserById(String userId) {
        // SQL query to select the user by ID
        String selectSQL = "SELECT * FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userId);  // Set the userId in the SQL query
            ResultSet rs = pstmt.executeQuery();  // Execute the query and get the result set
            if (rs.next()) {
                // If a user is found, map the result set to a UserModel and return it
                return Optional.of(mapResultToUserModel(rs));
            } else {
                System.err.println("User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
        }
        return Optional.empty();  // Return an empty Optional if the user is not found
    }

    /**
     * Retrieve a user by their email address.
     * @param userEmail The email address of the user to retrieve
     * @return An Optional containing the UserModel if found, or empty if not
     */
    public Optional<UserModel> getUserByEmail(String userEmail) {
        // SQL query to select the user by email
        String selectSQL = "SELECT * FROM User WHERE user_email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userEmail);  // Set the userEmail in the SQL query
            ResultSet rs = pstmt.executeQuery();  // Execute the query
            if (rs.next()) {
                // If a user is found, map the result set to a UserModel and return it
                return Optional.of(mapResultToUserModel(rs));
            } else {
                System.err.println("User not found with email: " + userEmail);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by email: " + e.getMessage());
        }
        return Optional.empty();  // Return an empty Optional if the user is not found
    }

    /**
     * Update the details of an existing user in the database.
     * @param userId        The ID of the user to update
     * @param userName      The updated user name
     * @param hashedPassword The updated hashed password
     * @param hashSalt      The updated salt used for hashing
     * @param userEmail     The updated user email
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUser(String userId, String userName, String hashedPassword, String hashSalt, String userEmail) {
        // SQL query to update the user record
        String updateSQL = "UPDATE User SET user_name = ?, hashed_password = ?, hash_salt = ?, user_email = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, hashedPassword);  // Update the password hash
            pstmt.setString(3, hashSalt);        // Update the salt
            pstmt.setString(4, userEmail);       // Update the email
            pstmt.setString(5, userId);          // Specify the user ID to update
            int affectedRows = pstmt.executeUpdate();  // Execute the update
            return affectedRows > 0;  // Return true if the update affected at least one row
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;  // Return false if the update fails
        }
    }

    /**
     * Delete a user from the database by their ID.
     * @param userId The ID of the user to delete
     * @return true if the user was deleted, false otherwise
     */
    public boolean deleteUser(String userId) {
        // SQL query to delete a user by ID
        String deleteSQL = "DELETE FROM User WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, userId);  // Set the userId to delete
            int affectedRows = pstmt.executeUpdate();  // Execute the delete query
            return affectedRows > 0;  // Return true if at least one row was affected
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;  // Return false if the deletion fails
        }
    }

    /**
     * Helper method to map a ResultSet row to a UserModel object.
     * @param rs The ResultSet to map
     * @return A UserModel object with values from the result set
     * @throws SQLException if any SQL error occurs during mapping
     */
    private UserModel mapResultToUserModel(ResultSet rs) throws SQLException {
        // Extract fields from the result set
        String userId = rs.getString("user_id");
        String userName = rs.getString("user_name");
        String hashedPassword = rs.getString("hashed_password");
        String hashSalt = rs.getString("hash_salt");
        String userEmail = rs.getString("user_email");
        String encryptionKeyString = rs.getString("encryption_key");  // Get the encryption key
        SecretKey encryptionKey = null;

        // Convert the Base64-encoded encryption key back to a SecretKey object
        if (encryptionKeyString != null) {
            byte[] decodedKey = Base64.getDecoder().decode(encryptionKeyString);
            encryptionKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        }

        // Retrieve timestamps for user creation and update
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        // Return a new UserModel object with the mapped values
        return new UserModel(userId, userName, hashedPassword, hashSalt, userEmail, encryptionKey, createdAt, updatedAt);
    }
}
