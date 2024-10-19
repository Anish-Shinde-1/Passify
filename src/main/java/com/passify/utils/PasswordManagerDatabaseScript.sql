-- Create Database
CREATE DATABASE IF NOT EXISTS PasswordManager;

-- Use the new database
USE PasswordManager;

-- Create User Table
CREATE TABLE IF NOT EXISTS User (
    user_id CHAR(36) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    hash_salt VARCHAR(255) NOT NULL,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    encryption_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Password Table
CREATE TABLE IF NOT EXISTS Password (
    password_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    encryption_salt VARCHAR(255) NOT NULL,
    category ENUM('Work', 'Social', 'Misc') NOT NULL,
    app_name VARCHAR(100) NOT NULL,
    app_username VARCHAR(100) NOT NULL,
    app_url VARCHAR(255),
    app_email VARCHAR(255),
    app_notes TEXT,
    password_state ENUM('saved', 'trashed') DEFAULT 'saved',
    isFavourite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

-- Create Dashboard Table
CREATE TABLE IF NOT EXISTS Dashboard (
    dashboard_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    signin_logs TEXT,
    signout_logs TEXT,
    backup_enabled BOOLEAN DEFAULT FALSE,
    backup_location VARCHAR(255),
    password_settings TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);
