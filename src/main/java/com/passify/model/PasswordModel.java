package com.passify.model;

import java.sql.Timestamp;

public class PasswordModel {

    public enum Category {
        WORK,
        SOCIAL,
        MISC;

        public static Category fromString(String category) {
            if (category == null) {
                return null;
            }
            return switch (category.toUpperCase()) {
                case "WORK" -> WORK;
                case "SOCIAL" -> SOCIAL;
                case "MISC" -> MISC;
                default -> throw new IllegalArgumentException("No enum constant for category: " + category);
            };
        }
    }

    private String passwordId;
    private String userId;
    private String encryptedPassword;
    private String appPassword;
    private String encryptionSalt;
    private Category category;
    private String appName;
    private String appUsername;
    private String appUrl;
    private String appEmail;
    private String appNotes;
    private String passwordState;
    private boolean isFavourite;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Custom constructor
    public PasswordModel(String userId, String encryptedPassword, String encryptionSalt,
                         Category category, String appName, String appUsername,
                         String appUrl, String appEmail, String appNotes,
                         String passwordState, boolean isFavourite) {
        this.userId = userId;
        this.encryptedPassword = encryptedPassword;
        this.encryptionSalt = encryptionSalt;
        this.category = category;
        this.appName = appName;
        this.appUsername = appUsername;
        this.appUrl = appUrl;
        this.appEmail = appEmail;
        this.appNotes = appNotes;
        this.passwordState = passwordState;
        this.isFavourite = isFavourite;
        this.createdAt = new Timestamp(System.currentTimeMillis()); // Initialize createdAt
        this.updatedAt = new Timestamp(System.currentTimeMillis()); // Initialize updatedAt
    }

    public PasswordModel(String userId, String encryptedPassword, String appPassword, String encryptionSalt,
                         Category category, String appName, String appUsername,
                         String appUrl, String appEmail, String appNotes,
                         String passwordState, boolean isFavourite) {
        this.userId = userId;
        this.encryptedPassword = encryptedPassword;
        this.encryptionSalt = encryptionSalt;
        this.appPassword = appPassword;
        this.category = category;
        this.appName = appName;
        this.appUsername = appUsername;
        this.appUrl = appUrl;
        this.appEmail = appEmail;
        this.appNotes = appNotes;
        this.passwordState = passwordState;
        this.isFavourite = isFavourite;
        this.createdAt = new Timestamp(System.currentTimeMillis()); // Initialize createdAt
        this.updatedAt = new Timestamp(System.currentTimeMillis()); // Initialize updatedAt
    }

    // Default Constructor
    public PasswordModel() {
    }

    public String getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(String passwordId) {
        this.passwordId = passwordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getAppPassword() {
        return appPassword;
    }

    public void setAppPassword(String appPassword) {
        this.appPassword = appPassword;
    }

    public String getEncryptionSalt() {
        return encryptionSalt;
    }

    public void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }

    public Category getCategory() {
        return category; // Getter for category
    }

    public void setCategory(Category category) {
        this.category = category; // Setter for category
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

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
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

    public String getAppEmail() {
        return appEmail;
    }

    public void setAppEmail(String appEmail) {
        this.appEmail = appEmail;
    }

    @Override
    public String toString() {
        return "PasswordModel{" +
                "passwordId='" + passwordId + '\'' +
                ", userId='" + userId + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", encryptionSalt='" + encryptionSalt + '\'' +
                ", category=" + category + // Use category in toString
                ", appName='" + appName + '\'' +
                ", appUsername='" + appUsername + '\'' +
                ", appUrl='" + appUrl + '\'' +
                ", appEmail='" + appEmail + '\'' +
                ", appNotes='" + appNotes + '\'' +
                ", passwordState='" + passwordState + '\'' +
                ", isFavourite=" + isFavourite +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
