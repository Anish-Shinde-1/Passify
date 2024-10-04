package com.passify.model;

import com.passify.utils.Encryption;
import com.passify.utils.SaltGenerator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.*;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class EncryptionKeyDAO {

    private final Connection connection;

    public EncryptionKeyDAO(Connection connection) throws SQLException {
        this.connection = connection;

        if (this.connection == null) {
            throw new SQLException("Connection is null!");
        }
    }

    // Generate and store a new DEK (Data Encryption Key) for a user
    public boolean storeEncryptedDEK(String userId) {
        SecretKey dek;
        try {
            dek = Encryption.generateKey(); // Generate DEK
        } catch (Exception e) {
            System.err.println("Error generating DEK: " + e.getMessage());
            return false; // Return false on failure
        }

        byte[] encryptionSalt = SaltGenerator.generateSalt(); // Generate salt for encryption
        String encryptedDEK;
        try {
            // Encrypt DEK using the generated salt
            encryptedDEK = Encryption.encrypt(Base64.getEncoder().encodeToString(dek.getEncoded()), encryptionSalt, dek);
        } catch (Exception e) {
            System.err.println("Error encrypting DEK: " + e.getMessage());
            return false; // Return false on failure
        }

        String insertEncryptionKeySQL = "INSERT INTO EncryptionKey (encryption_key_id, user_id, encrypted_DEK, encryption_salt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement keyStmt = connection.prepareStatement(insertEncryptionKeySQL)) {
            keyStmt.setString(1, UUID.randomUUID().toString()); // Generate EncryptionKey ID
            keyStmt.setString(2, userId); // Link to user
            keyStmt.setString(3, encryptedDEK); // Encrypted DEK
            keyStmt.setString(4, Base64.getEncoder().encodeToString(encryptionSalt)); // Salt for encryption
            return keyStmt.executeUpdate() > 0; // Return true on success
        } catch (SQLException e) {
            System.err.println("Error storing encrypted DEK: " + e.getMessage());
            return false; // Return false on failure
        }
    }

    // Retrieve and decrypt the DEK for a specific user
    public Optional<SecretKey> getDecryptedDEK(String userId) {
        String selectSQL = "SELECT encrypted_DEK, encryption_salt FROM EncryptionKey WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String encryptedDEK = rs.getString("encrypted_DEK");
                String base64Salt = rs.getString("encryption_salt");
                byte[] salt = Base64.getDecoder().decode(base64Salt);

                // Decrypt the DEK
                String decryptedDEK = Encryption.decrypt(encryptedDEK, salt, retrieveKeyForDecryption());
                byte[] decodedKey = Base64.getDecoder().decode(decryptedDEK);

                // Convert the decoded key bytes back to a SecretKey
                return Optional.of(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving or decrypting DEK: " + e.getMessage());
        }
        return Optional.empty(); // Return empty Optional if decryption fails
    }

    // Method to retrieve the DEK for a specific user as a SecretKey
    public Optional<SecretKey> getSecretKeyForUser(String userId) {
        return getDecryptedDEK(userId); // Reuse the existing decryption logic
    }

    // Helper method to simulate retrieving a key for decryption (use your own approach)
    private SecretKey retrieveKeyForDecryption() {
        try {
            return Encryption.generateKey(); // Placeholder: Replace with actual key retrieval mechanism
        } catch (Exception e) {
            System.err.println("Error retrieving decryption key: " + e.getMessage());
            return null;
        }
    }
}
