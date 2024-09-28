package com.passify.model;

import java.time.LocalDateTime;

public class UserModel {
    private String userId; // user_id
    private String userName; // user_name
    private String hashedPassword; // hashed_password
    private String hashSalt; // hash_salt
    private String userEmail; // user_email
    private LocalDateTime createdAt; // created_at
    private LocalDateTime updatedAt; // updated_at

    // Constructor
    public UserModel(String userId, String userName, String hashedPassword, String hashSalt, String userEmail, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.hashSalt = hashSalt;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Default constructor
    public UserModel () {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", hashSalt='" + hashSalt + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

//    public static void main(String[] args) {
//        UserModel user = new UserModel("user123", "JohnDoe", "5f4dcc3b5aa765d61d8327deb882cf99", "randomSaltValue123", "john.doe@example.com", LocalDateTime.now().minusDays(10), LocalDateTime.now());
//        System.out.println(user);
//    }
}
