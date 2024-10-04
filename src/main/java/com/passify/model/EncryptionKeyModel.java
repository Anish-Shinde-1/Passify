package com.passify.model;

import java.time.LocalDateTime;

public class EncryptionKeyModel {
    private int encryptionKeyId; // encryption_key_id
    private int userId; // user_id
    private String encryptedDEK; // encrypted_DEK
    private String KEKSalt; // encryption_salt
    private LocalDateTime createdAt; // created_at

    // Constructor
    public EncryptionKeyModel(int encryptionKeyId, int userId, String encryptedDEK,
                              String encryptionSalt, LocalDateTime createdAt) {
        this.encryptionKeyId = encryptionKeyId;
        this.userId = userId;
        this.encryptedDEK = encryptedDEK;
        this.KEKSalt = encryptionSalt;
        this.createdAt = createdAt;
    }

    // Default constructor
    public EncryptionKeyModel() {
    }

    // Getters and Setters
    public int getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(int encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEncryptedDEK() {
        return encryptedDEK;
    }

    public void setEncryptedDEK(String encryptedDEK) {
        this.encryptedDEK = encryptedDEK;
    }

    public String getEncryptionSalt() {
        return KEKSalt;
    }

    public void setEncryptionSalt(String encryptionSalt) {
        this.KEKSalt = encryptionSalt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "EncryptionKeyModel{" +
                "encryptionKeyId=" + encryptionKeyId +
                ", userId=" + userId +
                ", encryptedDEK='" + encryptedDEK + '\'' +
                ", encryptionSalt='" + KEKSalt + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
