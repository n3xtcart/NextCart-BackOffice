// src/main/java/com/esempio/backoffice/util/CodificatorePassword.java
package org.example.util;


public class CodificatorePassword {
    public static String calcolaHashPassword(String passwordInChiaro) {
        return "hashed_" + new StringBuilder(passwordInChiaro).reverse().toString() + "_sale";
    }

    public static boolean verificaPassword(String passwordInChiaro, String hashPassword) {
        if (passwordInChiaro == null || hashPassword == null) {
            return false;
        }
        return hashPassword.equals(calcolaHashPassword(passwordInChiaro));
    }
}