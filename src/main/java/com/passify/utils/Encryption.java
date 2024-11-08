package com.passify.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryption {

    // Constants for AES algorithm configuration
    private static final String ALGORITHM = "AES"; // AES encryption algorithm
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // AES with CBC and PKCS5 padding
    private static final int KEY_SIZE = 256; // AES key size (256 bits)

    // Generates a new AES secret key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM); // Get AES key generator
        keyGen.init(KEY_SIZE); // Initialize with the desired key size
        return keyGen.generateKey(); // Generate and return the AES secret key
    }

    // Encrypts the plain text using AES encryption, with salt and secret key
    public static String encrypt(String plainText, byte[] salt, SecretKey secretKey) throws Exception {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null"); // Check for null input
        }
        Cipher cipher = Cipher.getInstance(TRANSFORMATION); // Initialize AES cipher with CBC mode

        // Generate a random Initialization Vector (IV) for AES CBC mode
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv); // Generate random bytes for IV
        IvParameterSpec ivParams = new IvParameterSpec(iv); // Set IV for cipher

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams); // Initialize cipher for encryption

        // Encrypt the plain text into bytes
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        // Combine IV and encrypted text into a single byte array
        byte[] encryptedIvAndText = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedIvAndText, iv.length, encryptedBytes.length);

        // Encode the salt and encrypted data in base64 for storage and transmission
        String base64Salt = SaltGenerator.getBase64EncodedSalt(salt); // Convert salt to base64
        String base64Encrypted = Base64.getEncoder().encodeToString(encryptedIvAndText); // Convert encrypted data to base64

        // Return salt and encrypted text together, separated by a colon
        return base64Salt + ":" + base64Encrypted;
    }

    // Decrypts the encrypted text using AES decryption, salt, and secret key
    public static String decrypt(String encryptedText, byte[] salt, SecretKey secretKey) throws Exception {
        String[] parts = encryptedText.split(":"); // Split salt and encrypted data
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted text format"); // Ensure correct format
        }

        String base64Salt = parts[0]; // Extract salt (unused in decryption here)
        String base64Encrypted = parts[1]; // Extract encrypted text

        byte[] ivAndEncryptedText = Base64.getDecoder().decode(base64Encrypted); // Decode encrypted text
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        System.arraycopy(ivAndEncryptedText, 0, iv, 0, iv.length); // Extract IV from the byte array

        // Extract the encrypted data from the rest of the byte array
        byte[] encryptedBytes = new byte[ivAndEncryptedText.length - iv.length];
        System.arraycopy(ivAndEncryptedText, iv.length, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION); // Initialize AES cipher
        IvParameterSpec ivParams = new IvParameterSpec(iv); // Set IV for cipher
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams); // Initialize cipher for decryption

        // Decrypt the encrypted data and return as a string
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes); // Convert decrypted bytes to a plain text string
    }

}
