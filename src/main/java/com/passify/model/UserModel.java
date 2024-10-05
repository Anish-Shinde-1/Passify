package com.passify.model;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class UserModel {
    private String userId; // user_id
    private String userName; // user_name
    private String hashedPassword; // hashed_password
    private String hashSalt; // hash_salt
    private String userEmail; // user_email
    private String encryptionKey; // New encryption_key attribute
    private Timestamp createdAt; // created_at
    private Timestamp updatedAt; // updated_at

    // Constructor
    public UserModel(String userId, String userName, String hashedPassword, String hashSalt, String userEmail, String encryptionKey, Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.hashSalt = hashSalt;
        this.userEmail = userEmail;
        this.encryptionKey = encryptionKey; // Initialize encryptionKey
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Default constructor
    public UserModel() {
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getHashSalt() {
        return hashSalt;
    }

    public void setHashSalt(String hashSalt) {
        this.hashSalt = hashSalt;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEncryptionKey() {
        return encryptionKey; // Getter for encryptionKey
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey; // Setter for encryptionKey
    }

    public SecretKey getSecretKey() {
        // Convert the string to a byte array using UTF-8 encoding
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        // Create a SecretKeySpec from the byte array
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES"); // Change "AES" to your encryption algorithm if needed
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", hashSalt='" + hashSalt + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", encryptionKey='" + encryptionKey + '\'' + // Include encryptionKey in toString
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Example main method for testing (if needed)
    // public static void main(String[] args) {
    //     UserModel user = new UserModel(
    //             "user123",
    //             "JohnDoe",
    //             "5f4dcc3b5aa765d61d8327deb882cf99",
    //             "randomSaltValue123",
    //             "john.doe@example.com",
    //             "encryptionKeyValue123", // Sample encryption key
    //             new Timestamp(System.currentTimeMillis()), // Current time for createdAt
    //             new Timestamp(System.currentTimeMillis())  // Current time for updatedAt
    //     );
    //     System.out.println(user);
    // }
}
