package com.passify.utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TempPasswordGen {

    private static final String USER_ID = "1eb55be4-eaec-4af7-80db-dd058bcada1";
    private static final int PASSWORD_COUNT = 9;
    private static final String ENCRYPTION_KEY_BASE64 = "cuMwjhoT0n0SMLZfXpZqRwsho6Wd2UPU9erRsgfB/v4=";

    public static void main(String[] args) {
        try {
            List<PasswordEntry> passwords = generatePasswords(PASSWORD_COUNT);
            for (PasswordEntry passwordEntry : passwords) {
                System.out.println(passwordEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<PasswordEntry> generatePasswords(int count) throws Exception {
        List<PasswordEntry> passwordEntries = new ArrayList<>();

        // Decode the hardcoded encryption key from Base64
        byte[] decodedKey = Base64.getDecoder().decode(ENCRYPTION_KEY_BASE64);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        for (int i = 0; i < count; i++) {
            // Generate a secure password using the PasswordGenerator class
            String password = PasswordGenerator.generatePassword(); // Use the PasswordGenerator class

            byte[] salt = SaltGenerator.generateSalt();

            // Encrypt the password using the hardcoded secret key
            String encryptedPassword = Encryption.encrypt(password, salt, secretKey);

            // Create a PasswordEntry object to hold the data
            PasswordEntry entry = new PasswordEntry(
                    generateUUID(), // Generate a new UUID for the password_id
                    USER_ID,
                    encryptedPassword,
                    SaltGenerator.getBase64EncodedSalt(salt), // Base64 encoded salt
                    determineCategory(i), // Determine category based on the index
                    "AppName_" + (i + 1), // Sample application name
                    "user" + (i + 1) + "@example.com", // Sample application username
                    "http://app" + (i + 1) + ".com", // Sample application URL
                    "app" + (i + 1) + "@example.com", // Sample application email
                    "This is a note for AppName_" + (i + 1) // Sample app notes
            );

            passwordEntries.add(entry);
        }

        return passwordEntries;
    }

    private static String determineCategory(int index) {
        if (index < 3) {
            return "Work";
        } else if (index < 6) {
            return "Social";
        } else {
            return "Misc";
        }
    }

    private static String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    // Inner class to represent a password entry
    private static class PasswordEntry {
        String passwordId;
        String userId;
        String encryptedPassword;
        String encryptionSalt;
        String category;
        String appName;
        String appUsername;
        String appUrl;
        String appEmail;
        String appNotes;

        public PasswordEntry(String passwordId, String userId, String encryptedPassword,
                             String encryptionSalt, String category, String appName,
                             String appUsername, String appUrl, String appEmail,
                             String appNotes) {
            this.passwordId = passwordId;
            this.userId = userId;
            this.encryptedPassword = encryptedPassword;
            this.encryptionSalt = encryptionSalt;
            this.category = category;
            this.appName = appName;
            this.appUsername = appUsername;
            this.appUrl = appUrl;
            this.appEmail = appEmail;
            this.appNotes = appNotes;
        }

        @Override
        public String toString() {
            return String.format("PasswordEntry{passwordId='%s', userId='%s', encryptedPassword='%s', encryptionSalt='%s', category='%s', appName='%s', appUsername='%s', appUrl='%s', appEmail='%s', appNotes='%s'}",
                    passwordId, userId, encryptedPassword, encryptionSalt, category, appName, appUsername, appUrl, appEmail, appNotes);
        }
    }
}
