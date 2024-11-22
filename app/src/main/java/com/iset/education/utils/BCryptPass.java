package com.iset.education.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPass {

    // Method to hash a password
    public static String hashPassword(String plainPassword) {
        // Generate a salt and hash the password
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Method to verify a password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // Compare the plain password with the hashed password
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
