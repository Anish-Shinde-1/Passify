package com.passify.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SaltGenerator {

    // The desired length for the salt (in bytes)
    private static final int SALT_LENGTH = 16;

    // Generates a random salt using a secure random number generator
    public static byte[] generateSalt() {
        try {
            // SecureRandom instance using a strong cryptographic algorithm
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] salt = new byte[SALT_LENGTH];  // Create an array to store the salt
            secureRandom.nextBytes(salt);  // Fill the array with random bytes
            return salt;
        } catch (NoSuchAlgorithmException e) {
            // If SecureRandom instance creation fails, log the error
            System.err.println("SecureRandom algorithm is not available: " + e.getMessage());
            return null;
        }
    }

    // Converts the salt byte array into a Base64 encoded string
    public static String getBase64EncodedSalt(byte[] salt) {
        if (salt == null) {
            return null;  // Return null if the salt array is null
        }
        // Base64 encode the byte array and return the encoded string
        return Base64.getEncoder().encodeToString(salt);
    }

    // Decodes a Base64 encoded salt string back into a byte array
    public static byte[] getBase64DecodedSalt(String salt) {
        if (salt == null) {
            return null;  // Return null if the input string is null
        }
        // Decode the Base64 string back to a byte array and return it
        return Base64.getDecoder().decode(salt);
    }

    // Main method for testing the salt generation and encoding/decoding
    /*
    public static void main(String[] args) {
        System.out.println("Generating random salt:");
        byte[] salt = generateSalt();  // Generate a random salt
        System.out.println("Generated salt: " + Arrays.toString(salt));  // Print the generated salt

        String encodedSalt = getBase64EncodedSalt(salt);  // Encode the salt to Base64
        System.out.println("Encoded salt: " + encodedSalt);  // Print the Base64 encoded salt

        byte[] decodedSalt = getBase64DecodedSalt(encodedSalt);  // Decode the salt from Base64 back to byte array
        System.out.println("Decoded salt: " + Arrays.toString(decodedSalt));  // Print the decoded salt
    }
    */
}
