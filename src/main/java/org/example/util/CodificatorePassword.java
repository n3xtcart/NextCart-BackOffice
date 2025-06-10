package org.example.util;

public class CodificatorePassword {
    public static String calcolaHashPassword(String passwordInChiaro) {
        if (passwordInChiaro == null) return null;

        return "hashed_example_" + new StringBuilder(passwordInChiaro).reverse().toString();
    }

    public static boolean verificaPassword(String passwordInChiaro, String hashPassword) {
        if (passwordInChiaro == null || hashPassword == null) {
            return false;
        }
        return hashPassword.equals(calcolaHashPassword(passwordInChiaro));
    }
}