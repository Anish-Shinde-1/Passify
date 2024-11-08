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

    // Generates a random password ensuring the inclusion of lowercase, uppercase, digits, and special characters
    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Ensure at least one character from each set is included
        password.append(getRandomCharacter(lowercase));  // Add random lowercase character
        password.append(getRandomCharacter(uppercase));  // Add random uppercase character
        password.append(getRandomCharacter(digits));     // Add random digit
        password.append(getRandomCharacter(special));    // Add random special character

        // Fill the remaining positions with random characters from the full set
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(getRandomCharacter(all));  // Add random character from the complete set
        }

        // Shuffle the generated password to ensure randomness
        String generatedPassword = shuffleString(password.toString());  // Shuffle to randomize character order
        return generatedPassword;
    }

    // Returns a random character from the given string
    private static char getRandomCharacter(String charString) {
        int index = random.nextInt(charString.length());  // Pick a random index
        return charString.charAt(index);  // Return the character at the random index
    }

    // Shuffles the characters in the given string
    private static String shuffleString(String originalString) {
        char[] charArray = originalString.toCharArray();  // Convert the string to a character array

        // Fisher-Yates shuffle algorithm to randomize the array
        for (int i = charArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);  // Pick a random index to swap

            // Swap the current character with a randomly chosen one
            char temp = charArray[i];
            charArray[i] = charArray[j];
            charArray[j] = temp;
        }

        // Convert the shuffled character array back to a string and return
        return new String(charArray);
    }

    // Main method for testing password generation and shuffling functionality
    /*
    public static void main(String[] args) {
        // Generate and display a random password
        String generatedPassword = generatePassword();
        System.out.println("Generated Password: " + generatedPassword);

        // Example of shuffling a fixed string
        String tempString = "0123456789";
        System.out.println("Original String : " + tempString);
        System.out.println("Shuffled String : " + shuffleString(tempString));
    }
    */
}
