package com.passify.model;

import java.time.LocalDateTime;

public class PasswordModel {
    private int passwordId; // password_id
    private int userId; // user_id
    private String encryptedPassword; // encrypted_password
    private String encryptionSalt; // encryption_salt
    private int categoryId; // category_id
    private String appName; // app_name
    private String appUsername; // app_username
    private String appUrl;
    private String appEmail; // app_url
    private String appNotes; // app_notes
    private String passwordState; // password_state
    private boolean isFavourite; // isFavourite
    private LocalDateTime createdAt; // created_at


    // Constructor
    public PasswordModel(int passwordId, int userId, String encryptedPassword, String encryptionSalt,
                         int categoryId, String appName, String appUsername,
                         String appUrl, String appNotes, String passwordState,
                         LocalDateTime createdAt, boolean isFavourite) {
        this.passwordId = passwordId;
        this.userId = userId;
        this.encryptedPassword = encryptedPassword;
        this.encryptionSalt = encryptionSalt;
        this.categoryId = categoryId;
        this.appName = appName;
        this.appUsername = appUsername;
        this.appUrl = appUrl;
        this.appNotes = appNotes;
        this.passwordState = passwordState;
        this.createdAt = createdAt;
        this.isFavourite = isFavourite; // Set the isFavourite value
    }

    // Default constructor
    public PasswordModel() {
    }

    // Getters and Setters
    public int getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(int passwordId) {
        this.passwordId = passwordId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEncryptionSalt() {
        return encryptionSalt;
    }

    public void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppUsername() {
        return appUsername;
    }

    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppEmail () {
        return appEmail;
    }

    public void setAppEmail(String appEmail) {
        this.appEmail = appEmail;
    }

    public String getAppNotes() {
        return appNotes;
    }

    public void setAppNotes(String appNotes) {
        this.appNotes = appNotes;
    }

    public String getPasswordState() {
        return passwordState;
    }

    public void setPasswordState(String passwordState) {
        this.passwordState = passwordState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public String toString() {
        return "PasswordModel{" +
                "passwordId=" + passwordId +
                ", userId=" + userId +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", encryptionSalt='" + encryptionSalt + '\'' +
                ", categoryId=" + categoryId +
                ", appName='" + appName + '\'' +
                ", appUsername='" + appUsername + '\'' +
                ", appUrl='" + appUrl + '\'' +
                ", appEmail='" + appEmail + '\'' +
                ", appNotes='" + appNotes + '\'' +
                ", passwordState='" + passwordState + '\'' +
                ", createdAt=" + createdAt +
                ", isFavourite=" + isFavourite + // Include isFavourite in toString
                '}';
    }
}
