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

    public PasswordDAO(Connection connection) throws SQLException {
        this.connection = connection;
        if (this.connection == null) {
            throw new SQLException("Connection is null!");
        }
    }

    // Adds a new password entry to the database
    public boolean addPassword(PasswordModel password, UserModel user) throws SQLException {
        // Generate salt for encryption
        byte[] encryptionSalt = SaltGenerator.generateSalt();
        String encryptedPassword;

        try {
            // Get the user's encryption key and encrypt the password
            SecretKey secretKey = user.getEncryptionKey();
            encryptedPassword = Encryption.encrypt(password.getAppPassword(), encryptionSalt, secretKey);
        } catch (Exception e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return false;
        }

        // SQL query to insert the password into the database
        String sql = "INSERT INTO Password (password_id, user_id, encrypted_password, encryption_salt, category, app_name, app_username, app_url, app_email, app_notes, password_state, isFavourite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String generatedId = UUID.randomUUID().toString(); // Generate a unique ID for the password

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the values for the SQL query
            statement.setString(1, generatedId);
            statement.setString(2, password.getUserId());
            statement.setString(3, encryptedPassword);
            statement.setString(4, SaltGenerator.getBase64EncodedSalt(encryptionSalt));
            statement.setString(5, password.getCategory().name());
            statement.setString(6, password.getAppName());
            statement.setString(7, password.getAppUsername());
            statement.setString(8, password.getAppUrl());
            statement.setString(9, password.getAppEmail());
            statement.setString(10, password.getAppNotes());
            statement.setString(11, password.getPasswordState());
            statement.setBoolean(12, password.isFavourite());

            // Execute the SQL query and check if the insertion was successful
            boolean result = statement.executeUpdate() > 0;
            if (result) {
                password.setPasswordId(generatedId); // Set the generated ID back to the PasswordModel
            }
            return result;
        }
    }

    // Retrieves a password by its ID for a specific user
    public Optional<PasswordModel> getPasswordById(String passwordId, UserModel user) throws SQLException {
        String sql = "SELECT * FROM Password WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    PasswordModel passwordModel = mapResultSetToPasswordModel(resultSet);

                    // Decrypt the password using the user's encryption key
                    try {
                        SecretKey secretKey = user.getEncryptionKey();
                        String decryptedPassword = Encryption.decrypt(passwordModel.getEncryptedPassword(),
                                SaltGenerator.getBase64DecodedSalt(passwordModel.getEncryptionSalt()), secretKey);
                        passwordModel.setAppPassword(decryptedPassword); // Set decrypted password
                    } catch (Exception e) {
                        System.err.println("Error decrypting password: " + e.getMessage());
                        return Optional.empty();
                    }
                    return Optional.of(passwordModel);
                }
            }
        }
        return Optional.empty();
    }

    // Retrieves all passwords for a specific user
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

        // Return the list of passwords or an empty Optional if no passwords found
        return passwords.isEmpty() ? Optional.empty() : Optional.of(passwords);
    }

    // Updates an existing password entry in the database
    public boolean updatePassword(PasswordModel password, UserModel user) throws SQLException {
        String encryptedPassword;
        // Retrieve the existing salt from the database
        String existingSalt = getExistingSalt(password.getPasswordId());

        if (existingSalt == null) {
            System.err.println("No existing salt found for password ID: " + password.getPasswordId());
            return false;
        }

        byte[] encryptionSalt = SaltGenerator.getBase64DecodedSalt(existingSalt);

        try {
            // Encrypt the updated password
            SecretKey secretKey = user.getEncryptionKey();
            encryptedPassword = Encryption.encrypt(password.getAppPassword(), encryptionSalt, secretKey);
            password.setEncryptedPassword(encryptedPassword);
            password.setEncryptionSalt(existingSalt); // Use the existing salt
        } catch (Exception e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return false;
        }

        // SQL query to update the password in the database
        String sql = "UPDATE Password SET encrypted_password = ?, encryption_salt = ?, category = ?, app_name = ?, app_username = ?, app_url = ?, app_email = ?, app_notes = ?, password_state = ?, isFavourite = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the values for the update query
            statement.setString(1, password.getEncryptedPassword());
            statement.setString(2, password.getEncryptionSalt());
            statement.setString(3, password.getCategory().name());
            statement.setString(4, password.getAppName());
            statement.setString(5, password.getAppUsername());
            statement.setString(6, password.getAppUrl());
            statement.setString(7, password.getAppEmail());
            statement.setString(8, password.getAppNotes());
            statement.setString(9, password.getPasswordState());
            statement.setBoolean(10, password.isFavourite());
            statement.setString(11, password.getPasswordId());

            // Execute the update and check if it was successful
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            throw e;
        }
    }

    // Retrieves the existing salt for a password entry
    private String getExistingSalt(String passwordId) throws SQLException {
        String sql = "SELECT encryption_salt FROM Password WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("encryption_salt");
                }
            }
        }
        return null;
    }

    // Moves a password entry to the trash (soft delete)
    public boolean trashPassword(String passwordId) throws SQLException {
        String sql = "UPDATE Password SET password_state = 'trashed', updated_at = CURRENT_TIMESTAMP WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);
            return statement.executeUpdate() > 0;
        }
    }

    // Permanently deletes a password entry from the database
    public boolean deletePassword(String passwordId) throws SQLException {
        String sql = "DELETE FROM Password WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);
            return statement.executeUpdate() > 0;
        }
    }

    // Maps a ResultSet row to a PasswordModel object
    private PasswordModel mapResultSetToPasswordModel(ResultSet resultSet) throws SQLException {
        PasswordModel password = new PasswordModel();

        password.setPasswordId(resultSet.getString("password_id"));
        password.setUserId(resultSet.getString("user_id"));
        password.setEncryptedPassword(resultSet.getString("encrypted_password"));
        password.setEncryptionSalt(resultSet.getString("encryption_salt"));

        // Map the category from the result set to the Category enum
        String categoryValue = resultSet.getString("category");
        try {
            password.setCategory(PasswordModel.Category.fromString(categoryValue));
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid category value: " + categoryValue);
            password.setCategory(PasswordModel.Category.MISC); // Default to MISC category
        }

        password.setAppName(resultSet.getString("app_name"));
        password.setAppUsername(resultSet.getString("app_username"));
        password.setAppUrl(resultSet.getString("app_url"));
        password.setAppEmail(resultSet.getString("app_email"));
        password.setAppNotes(resultSet.getString("app_notes"));
        password.setPasswordState(resultSet.getString("password_state"));
        password.setFavourite(resultSet.getBoolean("isFavourite"));

        return password;
    }
}
