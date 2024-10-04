package com.passify.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class Hashing {
    private static final int KEY_LENGTH = 512;
    private static final int ITERATIONS = 15000;

    public static String generateHash512 (String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException{
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

        byte[] hashBytes = keyFactory.generateSecret(spec).getEncoded();

        String encodedHash = Base64.getEncoder().encodeToString(hashBytes);
        return encodedHash;
    }

    public static boolean verifyHash(String enteredPassword, String storedHash, String storedSalt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        String hashedEnteredPassword = generateHash512(enteredPassword, storedSalt);

        return hashedEnteredPassword.equals(storedHash);
    }

//    public static void main(String[] args) {
//
//        String generatedPassword = PasswordGenerator.generatePassword();
//        System.out.println("Generated Password : " + generatedPassword);
//        byte[] generatedSalt = SaltGenerator.generateSalt();
//        String encodedSalt = Base64.getEncoder().encodeToString(generatedSalt);
//        System.out.println("Generated Salt in bytes: " + Arrays.toString(generatedSalt));
//        System.out.println("Generated Salt after encoding: " + encodedSalt);
//
//        String generatedHash = null;
//        try {
//            generatedHash = generateHash512 (generatedPassword, encodedSalt);
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println("Generated Hash: " + generatedHash);
//    }

}
