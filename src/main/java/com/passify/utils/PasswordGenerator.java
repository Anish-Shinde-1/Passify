package com.passify.utils;

import java.security.SecureRandom;

public class PasswordGenerator {

    // Character sets for password generation
    private static final String lowercase = "abcdefghijklmnopqrstuvwxyz";  // Lowercase letters
    private static final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";  // Uppercase letters
    private static final String digits = "0123456789";  // Digits (0-9)
    private static final String special = "!@#$%^&*()-_=+[]{}|;:,.<>?";  // Special characters
    private static final String all = lowercase + uppercase + digits + special;  // All characters combined

    // Desired password length
    private static final int PASSWORD_LENGTH = 16;

    // SecureRandom instance to generate cryptographically strong random values
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random password consisting of at least one lowercase, one uppercase,
     * one digit, and one special character, with a total length of 16 characters.
     *
     * @return A randomly generated password as a String
     */
    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Ensure at least one character from each set is included
        password.append(getRandomCharacter(lowercase));  // Add random lowercase character
        password.append(getRandomCharacter(uppercase));  // Add random uppercase character
        password.append(getRandomCharacter(digits));     // Add random digit
        password.append(getRandomCharacter(special));    // Add random special character

        // Fill the remaining positions with random characters from the full set
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(getRandomCharacter(all));
        }

        // Shuffle the generated password to ensure randomness
        String generatedPassword = shuffleString(password.toString());
        return generatedPassword;
    }

    /**
     * Returns a random character from the given string.
     *
     * @param charString The string containing the characters to choose from
     * @return A randomly selected character from the string
     */
    private static char getRandomCharacter(String charString) {
        int index = random.nextInt(charString.length());  // Pick a random index
        return charString.charAt(index);  // Return the character at the random index
    }

    /**
     * Shuffles the characters in the given string.
     *
     * @param originalString The string to be shuffled
     * @return A shuffled version of the input string
     */
    private static String shuffleString(String originalString) {
        char[] charArray = originalString.toCharArray();  // Convert the string to a character array

        // Fisher-Yates shuffle algorithm to randomize the array
        for (int i = charArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);  // Pick a random index

            // Swap the current character with a random character
            char temp = charArray[i];
            charArray[i] = charArray[j];
            charArray[j] = temp;
        }

        // Convert the shuffled character array back to a string
        return new String(charArray);
    }

    /**
     * Main method for testing password generation and shuffling functionality.
     *
     * @param args Command-line arguments
     */
//    public static void main(String[] args) {
//        // Generate a password and display it
//        String generatedPassword = generatePassword();
//        System.out.println("Generated Password: " + generatedPassword);
//
//        // Demonstrate string shuffling with a fixed string
//        String tempString = "0123456789";
//        System.out.println("Original String : " + tempString);
//        System.out.println("Shuffled String : " + shuffleString(tempString));
//    }
}
