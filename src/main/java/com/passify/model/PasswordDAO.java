package com.passify.model;

import com.passify.utils.Encryption;
import com.passify.utils.SaltGenerator;

import javax.crypto.SecretKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PasswordDAO {

    private final Connection connection;
    private final EncryptionKeyDAO encryptionKeyDAO; // EncryptionKeyDAO for key management

    public PasswordDAO(Connection connection, EncryptionKeyDAO encryptionKeyDAO) throws SQLException {
        this.connection = connection;
        this.encryptionKeyDAO = encryptionKeyDAO;

        if (this.connection == null) {
            throw new SQLException("Connection is null!");
        }
    }

    // Create a new password entry
    public boolean addPassword(PasswordModel password, String userId) throws SQLException {
        // Generate salt for encryption
        byte[] encryptionSalt = SaltGenerator.generateSalt();
        String encryptedPassword;

        try {
            // Retrieve the DEK (Data Encryption Key) for the user from EncryptionKeyDAO
            Optional<SecretKey> secretKeyOpt = encryptionKeyDAO.getSecretKeyForUser(userId);
            if (!secretKeyOpt.isPresent()) {
                throw new SQLException("Failed to retrieve encryption key for the user.");
            }
            SecretKey secretKey = secretKeyOpt.get();

            // Encrypt the password using the user's DEK and generated salt
            encryptedPassword = Encryption.encrypt(password.getAppUsername(), encryptionSalt, secretKey);
        } catch (Exception e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return false; // Return false on failure
        }

        String sql = "INSERT INTO Password (password_id, user_id, encrypted_password, encryption_salt, category_id, app_name, app_username, app_url, app_email, app_notes, password_state, isFavourite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Generate a new UUID for the password_id
        String generatedId = UUID.randomUUID().toString();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, generatedId);  // Use the generated ID here
            statement.setString(2, password.getUserId());
            statement.setString(3, encryptedPassword);
            statement.setString(4, SaltGenerator.getBase64EncodedSalt(encryptionSalt));
            statement.setString(5, password.getCategoryId());
            statement.setString(6, password.getAppName());
            statement.setString(7, password.getAppUsername());
            statement.setString(8, password.getAppUrl());
            statement.setString(9, password.getAppEmail());
            statement.setString(10, password.getAppNotes());
            statement.setString(11, password.getPasswordState());
            statement.setBoolean(12, password.isFavourite());

            // Execute the update and return true if successful
            boolean result = statement.executeUpdate() > 0;

            if (result) {
                password.setPasswordId(generatedId); // Set the generated ID back to the PasswordModel
            }
            return result;
        }
    }

    // Retrieve a password by its ID (returns Optional)
    public Optional<PasswordModel> getPasswordById(String passwordId, String userId) throws SQLException {
        String sql = "SELECT * FROM Password WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    PasswordModel passwordModel = mapResultSetToPasswordModel(resultSet);

                    // Decrypt the password
                    try {
                        // Retrieve the DEK (Data Encryption Key) for the user from EncryptionKeyDAO
                        Optional<SecretKey> secretKeyOpt = encryptionKeyDAO.getSecretKeyForUser(userId);
                        if (!secretKeyOpt.isPresent()) {
                            throw new SQLException("Failed to retrieve encryption key for the user.");
                        }
                        SecretKey secretKey = secretKeyOpt.get();

                        String decryptedPassword = Encryption.decrypt(passwordModel.getEncryptedPassword(), SaltGenerator.getBase64DecodedSalt(passwordModel.getEncryptionSalt()), secretKey);
                        passwordModel.setAppUsername(decryptedPassword); // Assuming appUsername is used to store the password
                    } catch (Exception e) {
                        System.err.println("Error decrypting password: " + e.getMessage());
                        return Optional.empty(); // Return empty if decryption fails
                    }
                    return Optional.of(passwordModel);
                }
            }
        }

        // Return an empty Optional if no password is found
        return Optional.empty();
    }

    // Retrieve all passwords for a user (returns Optional<List<PasswordModel>>)
    public Optional<List<PasswordModel>> getAllPasswordsForUser(String userId) throws SQLException {
        String sql = "SELECT * FROM Password WHERE user_id = ? AND password_state = 'saved'";
        List<PasswordModel> passwords = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PasswordModel password = mapResultSetToPasswordModel(resultSet);
                    passwords.add(password);
                }
            }
        }

        // Return an Optional, either containing the list or empty if no passwords found
        return passwords.isEmpty() ? Optional.empty() : Optional.of(passwords);
    }

    // Update a password entry
    public boolean updatePassword(PasswordModel password, String userId) throws SQLException {
        String encryptedPassword;
        byte[] encryptionSalt = SaltGenerator.generateSalt();

        try {
            // Retrieve the DEK (Data Encryption Key) for the user from EncryptionKeyDAO
            Optional<SecretKey> secretKeyOpt = encryptionKeyDAO.getSecretKeyForUser(userId);
            if (!secretKeyOpt.isPresent()) {
                throw new SQLException("Failed to retrieve encryption key for the user.");
            }
            SecretKey secretKey = secretKeyOpt.get();

            // Encrypt the password before updating
            encryptedPassword = Encryption.encrypt(password.getAppUsername(), encryptionSalt, secretKey);
            password.setEncryptedPassword(encryptedPassword);
            password.setEncryptionSalt(SaltGenerator.getBase64EncodedSalt(encryptionSalt));
        } catch (Exception e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return false; // Return false on failure
        }

        String sql = "UPDATE Password SET encrypted_password = ?, encryption_salt = ?, category_id = ?, app_name = ?, app_username = ?, app_url = ?, app_email = ?, app_notes = ?, password_state = ?, isFavourite = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, password.getEncryptedPassword());
            statement.setString(2, password.getEncryptionSalt());
            statement.setString(3, password.getCategoryId());
            statement.setString(4, password.getAppName());
            statement.setString(5, password.getAppUsername());
            statement.setString(6, password.getAppUrl());
            statement.setString(7, password.getAppEmail());  // Include appEmail here
            statement.setString(8, password.getAppNotes());
            statement.setString(9, password.getPasswordState());
            statement.setBoolean(10, password.isFavourite());
            statement.setString(11, password.getPasswordId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No password found with the given ID: " + password.getPasswordId());
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            throw e; // Rethrow the exception for further handling
        }
    }

    // Move password to trash (soft delete)
    public boolean trashPassword(String passwordId) throws SQLException {
        String sql = "UPDATE Password SET password_state = 'trashed', updated_at = CURRENT_TIMESTAMP WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);
            return statement.executeUpdate() > 0;
        }
    }

    // Delete a password entry permanently
    public boolean deletePassword(String passwordId) throws SQLException {
        String sql = "DELETE FROM Password WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);
            return statement.executeUpdate() > 0;
        }
    }

    // Helper function to map a result set row to a PasswordModel object
    private PasswordModel mapResultSetToPasswordModel(ResultSet resultSet) throws SQLException {
        PasswordModel password = new PasswordModel();
        password.setPasswordId(resultSet.getString("password_id"));
        password.setUserId(resultSet.getString("user_id"));
        password.setEncryptedPassword(resultSet.getString("encrypted_password"));
        password.setEncryptionSalt(resultSet.getString("encryption_salt"));
        password.setCategoryId(resultSet.getString("category_id"));
        password.setAppName(resultSet.getString("app_name"));
        password.setAppUsername(resultSet.getString("app_username"));
        password.setAppUrl(resultSet.getString("app_url"));
        password.setAppEmail(resultSet.getString("app_email")); // Retrieve appEmail from ResultSet
        password.setAppNotes(resultSet.getString("app_notes"));
        password.setPasswordState(resultSet.getString("password_state"));
        password.setFavourite(resultSet.getBoolean("isFavourite"));
        password.setCreatedAt(resultSet.getTimestamp("created_at"));
        password.setUpdatedAt(resultSet.getTimestamp("updated_at"));

        return password;
    }

    // Method to retrieve the current database connection
    public Connection getConnection() {
        return connection;
    }

    public static PasswordModel getMockPassword() {
        PasswordModel password = new PasswordModel();
        password.setPasswordId("mock_password_id");
        password.setUserId("mock_user_id");
        password.setEncryptedPassword("mock_encrypted_password");
        password.setEncryptionSalt("mock_encryption_salt");
        password.setCategoryId("mock_category_id");
        password.setAppName("mock_app_name");
        password.setAppUsername("mock_app_username");
        password.setAppUrl("http://mockapp.com");
        password.setAppEmail("mockemail@example.com");  // Set mock email
        password.setAppNotes("Some notes about the app");
        password.setPasswordState("active");
        password.setFavourite(true);  // or false depending on the mock user
        password.setCreatedAt(new Timestamp(System.currentTimeMillis()));  // Sets to current timestamp
        password.setUpdatedAt(new Timestamp(System.currentTimeMillis()));  // Sets to current timestamp

        return password;  // Don't forget to return the password object
    }
}
