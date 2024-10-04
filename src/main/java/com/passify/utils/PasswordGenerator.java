package com.passify.utils;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String lowercase = "abcdefghijklmnopqrstuvwxyz";
    private static final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String digits = "0123456789";
    private static final String special = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String all = lowercase + uppercase + digits + special;

    private static final int PASSWORD_LENGTH = 16;
    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        password.append(getRandomCharacter(lowercase));
        password.append(getRandomCharacter(uppercase));
        password.append(getRandomCharacter(digits));
        password.append(getRandomCharacter(special));

        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(getRandomCharacter(all));
        }

        String generatedPassword = shuffleString(password.toString());
        return generatedPassword;
    }

    private static char getRandomCharacter(String charString) {
        int index = random.nextInt(charString.length());
        char randomCharacter = charString.charAt(index);
        return randomCharacter;
    }

    private static String shuffleString(String originalString) {
        char[] charArray = originalString.toCharArray();
        for (int i = charArray.length - 1; i > 0; i--) {

            int j = random.nextInt(i + 1);

            char temp = charArray[i];
            charArray[i] = charArray[j];
            charArray[j] = temp;
        }

        String shuffledString = new String(charArray);

        return shuffledString;
    }

//    public static void main(String[] args) {
//        String generatedPassword = generatePassword();
//        System.out.println("Generated Password: " + generatedPassword);
//        String tempString = "0123456789";
//        System.out.println("Original String : " + tempString);
//        System.out.println("Shuffled String : " + shuffleString(tempString));
//    }

}
