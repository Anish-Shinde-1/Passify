package com.passify.model;

import java.sql.Timestamp;

public class PasswordModel {

    public enum Category {
        WORK,
        SOCIAL,
        MISC;

        public static Category fromString(String category) {
            if (category == null) {
                return null; // or throw an exception
            }
            return switch (category.toUpperCase()) {
                case "WORK" -> WORK;
                case "SOCIAL" -> SOCIAL;
                case "MISC" -> MISC;
                default -> throw new IllegalArgumentException("No enum constant for category: " + category);
            };
        }
    }

    private String passwordId; // password_id
    private String userId; // user_id
    private String encryptedPassword; // encrypted_password
    private String appPassword = null;
    private String encryptionSalt; // encryption_salt
    private Category category; // Changed to use the Category enum
    private String appName; // app_name
    private String appUsername; // app_username
    private String appUrl; // app_url
    private String appEmail; // app_email
    private String appNotes; // app_notes
    private String passwordState; // password_state
    private boolean isFavourite; // isFavourite
    private Timestamp createdAt; // created_at
    private Timestamp updatedAt; // updated_at

    // Getters and Setters for each field
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

    // Example main method for testing (if needed)
//    public static void main(String[] args) {
//        PasswordModel password = new PasswordModel();
//        password.setPasswordId("pass123");
//        password.setUserId("user123");
//        password.setEncryptedPassword("encryptedPass");
//        password.setEncryptionSalt("saltValue");
//        password.setCategory(Category.WORK); // Example category
//        password.setAppName("ExampleApp");
//        password.setAppUsername("exampleUser");
//        password.setAppUrl("http://example.com");
//        password.setAppEmail("user@example.com");
//        password.setAppNotes("Sample notes");
//        password.setPasswordState("saved");
//        password.setFavourite(true);
//        password.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//        password.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//        System.out.println(password);
//    }
}
