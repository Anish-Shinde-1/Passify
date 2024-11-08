package com.passify.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Hashing {

    // Length of the generated hash (in bits)
    private static final int KEY_LENGTH = 512;

    // Number of iterations for PBKDF2 hashing function
    private static final int ITERATIONS = 15000;

    // Generates a PBKDF2 hash using HmacSHA512 for the given password and salt
    public static String generateHash512(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Convert the password into a character array and decode the salt from Base64
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = Base64.getDecoder().decode(salt);

        // Prepare the key specification for the hashing algorithm with password, salt, iterations, and key length
        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);

        // Create a SecretKeyFactory for PBKDF2WithHmacSHA512 algorithm
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

        // Generate the hash byte array using the key specification
        byte[] hashBytes = keyFactory.generateSecret(spec).getEncoded();

        // Encode the hash in Base64 and return it
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    // Verifies if the entered password matches the stored hash by rehashing the entered password
    public static boolean verifyHash(String enteredPassword, String storedHash, String storedSalt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Generate the hash of the entered password using the stored salt
        String hashedEnteredPassword = generateHash512(enteredPassword, storedSalt);

        // Compare the generated hash with the stored hash to verify match
        return hashedEnteredPassword.equals(storedHash);
    }

    /*
    // Uncomment the main method to test password generation, hashing, and salt encoding.
    public static void main(String[] args) {

        // Generate a random password using PasswordGenerator
        String generatedPassword = PasswordGenerator.generatePassword();
        System.out.println("Generated Password : " + generatedPassword);

        // Generate a salt and encode it as Base64
        byte[] generatedSalt = SaltGenerator.generateSalt();
        String encodedSalt = Base64.getEncoder().encodeToString(generatedSalt);
        System.out.println("Generated Salt in bytes: " + Arrays.toString(generatedSalt));
        System.out.println("Generated Salt after encoding: " + encodedSalt);

        // Generate a hash for the password using the salt
        String generatedHash = null;
        try {
            generatedHash = generateHash512(generatedPassword, encodedSalt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Generated Hash: " + generatedHash);
    }
    */
}
