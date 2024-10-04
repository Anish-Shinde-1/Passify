package com.passify.model;

import java.sql.Timestamp;

public class PasswordModel {
    private String passwordId;
    private String userId;
    private String encryptedPassword;
    private String encryptionSalt;
    private String categoryId;
    private String appName;
    private String appUsername;
    private String appUrl;
    private String appEmail;
    private String appNotes;
    private String passwordState;
    private boolean isFavourite;
    private Timestamp createdAt;
    private Timestamp updatedAt;

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

    public String getEncryptionSalt() {
        return encryptionSalt;
    }

    public void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
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
}
