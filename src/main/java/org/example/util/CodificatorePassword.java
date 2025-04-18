package org.example.util;

/**
 * Classe di utilità per la gestione delle password.
 * Fornisce metodi per calcolare l'hash di una password e verificarne la corrispondenza.
 */
public class CodificatorePassword {

    /**
     * Calcola l'hash di una password in chiaro.
     * L'hash è generato invertendo la stringa della password e aggiungendo un "sale".
     *
     * @param passwordInChiaro La password in chiaro da codificare.
     * @return Una stringa rappresentante l'hash della password.
     */
    public static String calcolaHashPassword(String passwordInChiaro) {
        return "hashed_" + new StringBuilder(passwordInChiaro).reverse().toString() + "_sale";
    }

    /**
     * Verifica se una password in chiaro corrisponde a un hash fornito.
     *
     * @param passwordInChiaro La password in chiaro da verificare.
     * @param hashPassword L'hash della password con cui confrontare.
     * @return true se la password in chiaro corrisponde all'hash, false altrimenti.
     */
    public static boolean verificaPassword(String passwordInChiaro, String hashPassword) {
        if (passwordInChiaro == null || hashPassword == null) {
            return false;
        }
        return hashPassword.equals(calcolaHashPassword(passwordInChiaro));
    }
}