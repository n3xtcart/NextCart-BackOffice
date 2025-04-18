// java
package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.util.CodificatorePassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe CodificatorePassword.
 * Contiene test per verificare il calcolo dell'hash e la verifica della password.
 */
@DisplayName("Test CodificatorePassword (Implementazione Semplice)")
class TestCodificatorePassword {

    /**
     * Verifica che il metodo calcolaHashPassword restituisca una stringa hash non nulla e prevedibile.
     */
    @Test
    @DisplayName("Calcola Hash restituisce stringa non nulla e prevedibile")
    void calcolaHashPassword_successo() {
        String password = "password123";
        String expectedHash = "hashed_321drowssap_sale";
        String actualHash = CodificatorePassword.calcolaHashPassword(password);

        assertNotNull(actualHash, "L'hash non dovrebbe essere null");
        assertEquals(expectedHash, actualHash, "L'hash calcolato non corrisponde a quello atteso");
    }

    /**
     * Verifica che il metodo calcolaHashPassword gestisca correttamente la stringa vuota.
     */
    @Test
    @DisplayName("Calcola Hash per stringa vuota")
    void calcolaHashPassword_stringaVuota() {
        String password = "";
        String expectedHash = "hashed__sale";
        String actualHash = CodificatorePassword.calcolaHashPassword(password);
        assertEquals(expectedHash, actualHash);
    }

    /**
     * Verifica che il metodo verificaPassword restituisca true quando la password è corretta.
     */
    @Test
    @DisplayName("Verifica Password Corretta restituisce true")
    void verificaPassword_corretta_true() {
        String password = "testPassword";
        String hash = CodificatorePassword.calcolaHashPassword(password);
        assertTrue(CodificatorePassword.verificaPassword(password, hash),
                "La verifica con password corretta dovrebbe restituire true");
    }

    /**
     * Verifica che il metodo verificaPassword restituisca false quando viene fornita una password errata.
     */
    @Test
    @DisplayName("Verifica Password Errata restituisce false")
    void verificaPassword_errata_false() {
        String password = "testPassword";
        String hash = CodificatorePassword.calcolaHashPassword(password);
        assertFalse(CodificatorePassword.verificaPassword("passwordErrata", hash),
                "La verifica con password errata dovrebbe restituire false");
        assertFalse(CodificatorePassword.verificaPassword("testpassword", hash),
                "La verifica è case-sensitive, dovrebbe restituire false");
    }

    /**
     * Verifica che il metodo verificaPassword restituisca false quando viene fornito un hash errato.
     */
    @Test
    @DisplayName("Verifica Password con Hash Errato restituisce false")
    void verificaPassword_hashErrato_false() {
        String password = "testPassword";
        String hashErrato = "hashed_drowssaptset_sale_extra";
        assertFalse(CodificatorePassword.verificaPassword(password, hashErrato),
                "La verifica con hash errato dovrebbe restituire false");
    }

    /**
     * Verifica che il metodo verificaPassword restituisca false quando uno o entrambi gli input sono null.
     */
    @Test
    @DisplayName("Verifica Password con Input Null restituisce false")
    void verificaPassword_nullInput_false() {
        String hashValido = CodificatorePassword.calcolaHashPassword("qualsiasi");
        String passwordValida = "qualsiasi";

        assertFalse(CodificatorePassword.verificaPassword(null, hashValido),
                "Verifica con password null dovrebbe restituire false");
        assertFalse(CodificatorePassword.verificaPassword(passwordValida, null),
                "Verifica con hash null dovrebbe restituire false");
        assertFalse(CodificatorePassword.verificaPassword(null, null),
                "Verifica con entrambi null dovrebbe restituire false");
    }
}