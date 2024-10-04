package com.passify.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256; // AES key size (128, 192, or 256 bits)

    // Generate a new Secret Key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    // Encrypts the given plain text using the provided salt and secret key
    public static String encrypt(String plainText, byte[] salt, SecretKey secretKey) throws Exception {
        // Initialize the cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // Generate a random IV (Initialization Vector) for encryption
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);

        // Encrypt the plain text
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        // Combine IV and encrypted bytes for storage (base64-encoded)
        byte[] encryptedIvAndText = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedIvAndText, iv.length, encryptedBytes.length);

        // Store the salt alongside the encrypted data (for demonstration)
        String base64Salt = SaltGenerator.getBase64EncodedSalt(salt);
        String base64Encrypted = Base64.getEncoder().encodeToString(encryptedIvAndText);

        return base64Salt + ":" + base64Encrypted; // Return salt and encrypted text together
    }

    // Decrypts the given encrypted text using the provided salt and secret key
    public static String decrypt(String encryptedText, byte[] salt, SecretKey secretKey) throws Exception {
        // Split the salt and encrypted text
        String[] parts = encryptedText.split(":");
        String base64Salt = parts[0];
        String base64Encrypted = parts[1];

        byte[] ivAndEncryptedText = Base64.getDecoder().decode(base64Encrypted);
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        System.arraycopy(ivAndEncryptedText, 0, iv, 0, iv.length);

        // Extract the actual encrypted text
        byte[] encryptedBytes = new byte[ivAndEncryptedText.length - iv.length];
        System.arraycopy(ivAndEncryptedText, iv.length, encryptedBytes, 0, encryptedBytes.length);

        // Initialize the cipher for decryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);

        // Decrypt the text
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    // Main method for testing
//    public static void main(String[] args) {
//        try {
//            // Example usage
//            String originalText = "Sensitive Password";
//            byte[] salt = SaltGenerator.generateSalt();
//            SecretKey secretKey = generateKey(); // Generate a key for encryption
//
//            String encryptedText = encrypt(originalText, salt, secretKey);
//            System.out.println("Encrypted Text: " + encryptedText);
//
//            String decryptedText = decrypt(encryptedText, salt, secretKey);
//            System.out.println("Decrypted Text: " + decryptedText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
