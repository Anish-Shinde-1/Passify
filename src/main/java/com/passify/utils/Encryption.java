package com.passify.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Encryption {

    // Constants for encryption algorithm and configuration
    private static final String ALGORITHM = "AES"; // AES encryption algorithm
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // AES mode with CBC and PKCS5 padding
    private static final int KEY_SIZE = 256; // AES key size (can be 128, 192, or 256 bits)

    /**
     * Generates a new AES secret key with the specified key size.
     * @return A new SecretKey object.
     * @throws Exception if key generation fails.
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE); // Initialize KeyGenerator with AES key size
        return keyGen.generateKey(); // Generate and return the SecretKey
    }

    /**
     * Encrypts a plain text string using AES encryption with the provided secret key and salt.
     * @param plainText The plain text to encrypt.
     * @param salt The salt used to add randomness to the encryption.
     * @param secretKey The secret key used for encryption.
     * @return The base64-encoded encrypted text along with the salt (also base64-encoded).
     * @throws Exception if encryption fails.
     */
    public static String encrypt(String plainText, byte[] salt, SecretKey secretKey) throws Exception {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }
        // Initialize the AES cipher in encryption mode
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // Generate a random IV (Initialization Vector) for CBC mode
        byte[] iv = new byte[cipher.getBlockSize()]; // AES block size is 16 bytes
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv); // Fill the IV with random bytes
        IvParameterSpec ivParams = new IvParameterSpec(iv); // IV parameters for the cipher

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams); // Initialize cipher for encryption

        // Encrypt the plain text into bytes
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted text into one byte array for storage
        byte[] encryptedIvAndText = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedIvAndText, iv.length, encryptedBytes.length);

        // Encode the salt and encrypted data in base64 for easy storage and transmission
        String base64Salt = SaltGenerator.getBase64EncodedSalt(salt); // Convert salt to base64
        String base64Encrypted = Base64.getEncoder().encodeToString(encryptedIvAndText); // Convert encrypted data to base64

        // Return salt and encrypted text together, separated by a colon
        return base64Salt + ":" + base64Encrypted;
    }

    /**
     * Decrypts an encrypted string using AES encryption with the provided secret key and salt.
     * @param encryptedText The base64-encoded encrypted text with the IV.
     * @param salt The salt that was used during encryption.
     * @param secretKey The secret key used for decryption.
     * @return The decrypted plain text string.
     * @throws Exception if decryption fails.
     */
    public static String decrypt(String encryptedText, byte[] salt, SecretKey secretKey) throws Exception {
        // Split the salt and encrypted data, assuming they are separated by a colon
        String[] parts = encryptedText.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted text format");
        }

        // Decode the base64-encoded IV and encrypted text
        String base64Salt = parts[0]; // Extract the salt (unused in decryption process here)
        String base64Encrypted = parts[1]; // Extract the encrypted text

        byte[] ivAndEncryptedText = Base64.getDecoder().decode(base64Encrypted);
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        System.arraycopy(ivAndEncryptedText, 0, iv, 0, iv.length); // Extract IV from the start of the byte array

        // Extract the actual encrypted text from the rest of the byte array
        byte[] encryptedBytes = new byte[ivAndEncryptedText.length - iv.length];
        System.arraycopy(ivAndEncryptedText, iv.length, encryptedBytes, 0, encryptedBytes.length);

        // Initialize the AES cipher in decryption mode
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParams = new IvParameterSpec(iv); // Pass the extracted IV to the cipher
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams); // Initialize cipher for decryption

        // Decrypt the encrypted bytes into plain text
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8); // Convert decrypted bytes to UTF-8 string
    }

    /**
     * Main method to demonstrate the usage of the Encryption class.
     * Generates a key, encrypts a sample text, and then decrypts it back.
     */
//    public static void main(String[] args) {
//        try {
//            // Example usage of encryption and decryption
//            String originalText = "Sensitive Password"; // The plain text to encrypt
//            byte[] salt = SaltGenerator.generateSalt(); // Generate a random salt
//            SecretKey secretKey = generateKey(); // Generate an AES key
//            String Keytext = Base64.getEncoder().encodeToString(secretKey.getEncoded()); // Convert key to base64 for display
//            System.out.println("Encryption Key: " + Keytext);
//
//            // Encrypt the original text
//            String encryptedText = encrypt(originalText, salt, secretKey);
//            System.out.println("Encrypted Text: " + encryptedText);
//
//            // Decrypt the encrypted text
//            String decryptedText = decrypt(encryptedText, salt, secretKey);
//            System.out.println("Decrypted Text: " + decryptedText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
