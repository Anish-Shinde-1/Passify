package com.passify.model;

import com.passify.utils.Encryption;
import com.passify.utils.SaltGenerator;

import javax.crypto.SecretKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data Access Object (DAO) for managing password entries in the database.
 * This class provides methods to add, retrieve, update, trash, and delete passwords securely.
 */
public class PasswordDAO {
    private final Connection connection;

    /**
     * Constructs a PasswordDAO with the specified database connection.
     *
     * @param connection the database connection to be used for password operations.
     * @throws SQLException if the connection is null or an SQL error occurs.
     */
    public PasswordDAO(Connection connection) throws SQLException {
        this.connection = connection;

        if (this.connection == null) {
            throw new SQLException("Connection is null!");
        }
    }

    /**
     * Creates a new password entry in the database.
     *
     * @param password the PasswordModel containing the password details.
     * @param user     the UserModel containing the user's encryption key.
     * @return true if the password entry was added successfully; false otherwise.
     * @throws SQLException if an SQL error occurs during the operation.
     */
    public boolean addPassword(PasswordModel password, UserModel user) throws SQLException {
        // Generate salt for encryption
        byte[] encryptionSalt = SaltGenerator.generateSalt();
        String encryptedPassword;

        try {
            // Get the SecretKey from the UserModel
            SecretKey secretKey = user.getEncryptionKey(); // Update to use getEncryptionKey()

            // Encrypt the password using the user's encryption key and generated salt
            encryptedPassword = Encryption.encrypt(password.getAppPassword(), encryptionSalt, secretKey);
        } catch (Exception e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return false; // Return false on failure
        }

        String sql = "INSERT INTO Password (password_id, user_id, encrypted_password, encryption_salt, category, app_name, app_username, app_url, app_email, app_notes, password_state, isFavourite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Generate a new UUID for the password_id
        String generatedId = UUID.randomUUID().toString();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, generatedId);
            statement.setString(2, password.getUserId());
            statement.setString(3, encryptedPassword);
            statement.setString(4, SaltGenerator.getBase64EncodedSalt(encryptionSalt));
            statement.setString(5, password.getCategory().name()); // Use enum name for category
            statement.setString(6, password.getAppName());
            statement.setString(7, password.getAppUsername());
            statement.setString(8, password.getAppUrl());
            statement.setString(9, password.getAppEmail());
            statement.setString(10, password.getAppNotes());
            statement.setString(11, password.getPasswordState());
            statement.setBoolean(12, password.isFavourite());

            boolean result = statement.executeUpdate() > 0;

            if (result) {
                password.setPasswordId(generatedId); // Set the generated ID back to the PasswordModel
            }
            return result;
        }
    }

    /**
     * Retrieves a password by its ID for a specific user.
     *
     * @param passwordId the unique identifier of the password.
     * @param user       the UserModel containing the user's encryption key.
     * @return an Optional containing the PasswordModel if found; empty otherwise.
     * @throws SQLException if an SQL error occurs during the operation.
     */
    public Optional<PasswordModel> getPasswordById(String passwordId, UserModel user) throws SQLException {
        String sql = "SELECT * FROM Password WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    PasswordModel passwordModel = mapResultSetToPasswordModel(resultSet);

                    // Decrypt the password
                    try {
                        SecretKey secretKey = user.getEncryptionKey(); // Update to use getEncryptionKey()
                        String decryptedPassword = Encryption.decrypt(passwordModel.getEncryptedPassword(),
                                SaltGenerator.getBase64DecodedSalt(passwordModel.getEncryptionSalt()), secretKey);
                        passwordModel.setAppPassword(decryptedPassword); // Set decrypted password
                    } catch (Exception e) {
                        System.err.println("Error decrypting password: " + e.getMessage());
                        return Optional.empty(); // Return empty if decryption fails
                    }
                    return Optional.of(passwordModel);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves all passwords for a specific user.
     *
     * @param userId the unique identifier of the user.
     * @return an Optional containing a list of PasswordModel if found; empty otherwise.
     * @throws SQLException if an SQL error occurs during the operation.
     */
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

    /**
     * Updates an existing password entry in the database.
     *
     * @param password the PasswordModel containing the updated password details.
     * @param user     the UserModel containing the user's encryption key.
     * @return true if the password entry was updated successfully; false otherwise.
     * @throws SQLException if an SQL error occurs during the operation.
     */
    public boolean updatePassword(PasswordModel password, UserModel user) throws SQLException {
        String encryptedPassword;
        // Retrieve the existing salt from the database first
        String existingSalt = getExistingSalt(password.getPasswordId());

        if (existingSalt == null) {
            System.err.println("No existing salt found for password ID: " + password.getPasswordId());
            return false; // Handle the case where no salt is found
        }

        byte[] encryptionSalt = SaltGenerator.getBase64DecodedSalt(existingSalt); // Decode the existing salt

        try {
            // Get the SecretKey from the UserModel
            SecretKey secretKey = user.getEncryptionKey(); // Update to use getEncryptionKey()

            // Encrypt the password before updating
            encryptedPassword = Encryption.encrypt(password.getAppPassword(), encryptionSalt, secretKey);
            password.setEncryptedPassword(encryptedPassword);
            password.setEncryptionSalt(existingSalt); // Use the existing salt
        } catch (Exception e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return false; // Return false on failure
        }

        String sql = "UPDATE Password SET encrypted_password = ?, encryption_salt = ?, category = ?, app_name = ?, app_username = ?, app_url = ?, app_email = ?, app_notes = ?, password_state = ?, isFavourite = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, password.getEncryptedPassword());
            statement.setString(2, password.getEncryptionSalt());
            statement.setString(3, password.getCategory().name()); // Use enum name for category
            statement.setString(4, password.getAppName());
            statement.setString(5, password.getAppUsername());
            statement.setString(6, password.getAppUrl());
            statement.setString(7, password.getAppEmail());
            statement.setString(8, password.getAppNotes());
            statement.setString(9, password.getPasswordState());
            statement.setBoolean(10, password.isFavourite());
            statement.setString(11, password.getPasswordId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            throw e; // Rethrow the exception for further handling
        }
    }

    /**
     * Retrieves the existing salt for a password entry.
     *
     * @param passwordId the unique identifier of the password.
     * @return the encryption salt if found; null otherwise.
     * @throws SQLException if an SQL error occurs during the operation.
     */
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
        return null; // Return null if no salt is found
    }

    /**
     * Moves a password entry to the trash (soft delete).
     *
     * @param passwordId the unique identifier of the password to be trashed.
     * @return true if the password entry was trashed successfully; false otherwise.
     * @throws SQLException if an SQL error occurs during the operation.
     */
    public boolean trashPassword(String passwordId) throws SQLException {
        String sql = "UPDATE Password SET password_state = 'trashed', updated_at = CURRENT_TIMESTAMP WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a password entry permanently from the database.
     *
     * @param passwordId the unique identifier of the password to be deleted.
     * @return true if the password entry was deleted successfully; false otherwise.
     * @throws SQLException if an SQL error occurs during the operation.
     */
    public boolean deletePassword(String passwordId) throws SQLException {
        String sql = "DELETE FROM Password WHERE password_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Helper function to map a result set row to a PasswordModel object.
     *
     * @param resultSet the ResultSet containing password data from the database.
     * @return the mapped PasswordModel object.
     * @throws SQLException if an SQL error occurs while retrieving data from the result set.
     */
    private PasswordModel mapResultSetToPasswordModel(ResultSet resultSet) throws SQLException {
        PasswordModel password = new PasswordModel();

        password.setPasswordId(resultSet.getString("password_id"));
        password.setUserId(resultSet.getString("user_id"));
        password.setEncryptedPassword(resultSet.getString("encrypted_password"));
        password.setEncryptionSalt(resultSet.getString("encryption_salt"));

        // Map the category from the result set to the Category enum
        String categoryValue = resultSet.getString("category");
        try {
            password.setCategory(PasswordModel.Category.fromString(categoryValue)); // Use fromString method
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid category value: " + categoryValue);
            password.setCategory(PasswordModel.Category.MISC); // Default to MISC or handle as needed
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
