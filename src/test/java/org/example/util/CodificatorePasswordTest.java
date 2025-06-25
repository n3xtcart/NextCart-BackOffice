package org.example.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CodificatorePasswordTest {

    @Test
    void calcolaHashPassword_conStringaValida() {
        String password = "password123";
        String expectedHash = "hashed_example_321drowssap";
        assertEquals(expectedHash, CodificatorePassword.calcolaHashPassword(password));
    }

    @Test
    void calcolaHashPassword_conStringaVuota() {
        String password = "";
        String expectedHash = "hashed_example_";
        assertEquals(expectedHash, CodificatorePassword.calcolaHashPassword(password));
    }

    @Test
    void calcolaHashPassword_conNull() {
        assertNull(CodificatorePassword.calcolaHashPassword(null));
    }

    @Test
    void verificaPassword_corrispondenzaCorretta() {
        String password = "test";
        String hash = "hashed_example_tset";
        assertTrue(CodificatorePassword.verificaPassword(password, hash));
    }

    @Test
    void verificaPassword_nonCorrispondente() {
        String password = "test";
        String hashErrato = "hashed_example_wrong";
        assertFalse(CodificatorePassword.verificaPassword(password, hashErrato));
    }

    @Test
    void verificaPassword_conInputNull() {
        assertFalse(CodificatorePassword.verificaPassword(null, "some_hash"), "Password nulla deve restituire false");
        assertFalse(CodificatorePassword.verificaPassword("some_pass", null), "Hash nullo deve restituire false");
        assertFalse(CodificatorePassword.verificaPassword(null, null), "Entrambi nulli devono restituire false");
    }
}