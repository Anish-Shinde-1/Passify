package com.passify.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;


public class SaltGenerator {
    private static final int SALT_LENGTH = 16;

    public static byte[] generateSalt () {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] salt = new byte[SALT_LENGTH];
            secureRandom.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SecureRandom algorithm is not available : " + e.getMessage());
            return null;
        }
    }

    public static String getBase64EncodedSalt(byte[] salt) {
        if(salt == null) {
            return null;
        }
        String encodedString = Base64.getEncoder().encodeToString(salt);
        return encodedString;
    }

    public static byte[] getBase64DecodedSalt(String salt) {
        if(salt == null) {
            return null;
        }
         byte[] decodedString = Base64.getDecoder().decode(salt);
        return decodedString;
    }

//    public static void main(String[] args) {
//
//        System.out.println("Generating random salt :  ");
//        byte[] salt = generateSalt();
//        System.out.println("Generated salt : " + Arrays.toString(salt));
//        String encodedSalt = getBase64EncodedSalt(salt);
//        System.out.println("Encoded salt : " + encodedSalt);
//        byte[] decodedSalt = getBase64DecodedSalt(encodedSalt);
//        System.out.println("Decoded salt : " + Arrays.toString(decodedSalt));
//    }

}
