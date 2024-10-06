package com.passify.model.FutureScope;

import java.time.LocalDateTime;

public class DashboardModel {
    private int dashboardId; // dashboard_id
    private int userId; // user_id
    private String signinLogs; // signin_logs
    private String signoutLogs; // signout_logs
    private boolean backupEnabled; // backup_enabled
    private String backupLocation; // backup_location
    private String passwordSettings; // password_settings
    private LocalDateTime createdAt; // created_at

    // Constructor
    public DashboardModel(int dashboardId, int userId, String signinLogs, String signoutLogs,
                          boolean backupEnabled, String backupLocation, String passwordSettings, LocalDateTime createdAt) {
        this.dashboardId = dashboardId;
        this.userId = userId;
        this.signinLogs = signinLogs;
        this.signoutLogs = signoutLogs;
        this.backupEnabled = backupEnabled;
        this.backupLocation = backupLocation;
        this.passwordSettings = passwordSettings;
        this.createdAt = createdAt;
    }

    // Default constructor
    public DashboardModel() {
    }

    // Getters and Setters
    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSigninLogs() {
        return signinLogs;
    }

    public void setSigninLogs(String signinLogs) {
        this.signinLogs = signinLogs;
    }

    public String getSignoutLogs() {
        return signoutLogs;
    }

    public void setSignoutLogs(String signoutLogs) {
        this.signoutLogs = signoutLogs;
    }

    public boolean isBackupEnabled() {
        return backupEnabled;
    }

    public void setBackupEnabled(boolean backupEnabled) {
        this.backupEnabled = backupEnabled;
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public void setBackupLocation(String backupLocation) {
        this.backupLocation = backupLocation;
    }

    public String getPasswordSettings() {
        return passwordSettings;
    }

    public void setPasswordSettings(String passwordSettings) {
        this.passwordSettings = passwordSettings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DashboardModel{" +
                "dashboardId=" + dashboardId +
                ", userId=" + userId +
                ", signinLogs='" + signinLogs + '\'' +
                ", signoutLogs='" + signoutLogs + '\'' +
                ", backupEnabled=" + backupEnabled +
                ", backupLocation='" + backupLocation + '\'' +
                ", passwordSettings='" + passwordSettings + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
