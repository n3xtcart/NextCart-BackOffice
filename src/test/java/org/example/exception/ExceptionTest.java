package org.example.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testEccezioneAccessoDati() {
        Throwable cause = new RuntimeException("DB down");
        EccezioneAccessoDati ex1 = new EccezioneAccessoDati("Errore DB");
        EccezioneAccessoDati ex2 = new EccezioneAccessoDati("Errore DB con causa", cause);

        assertEquals("Errore DB", ex1.getMessage());
        assertNull(ex1.getCause());
        assertEquals("Errore DB con causa", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testEccezioneAutenticazione() {
        Throwable cause = new RuntimeException("JWT error");
        EccezioneAutenticazione ex1 = new EccezioneAutenticazione("Credenziali errate");
        EccezioneAutenticazione ex2 = new EccezioneAutenticazione("Token fallito", cause);

        assertEquals("Credenziali errate", ex1.getMessage());
        assertNull(ex1.getCause());
        assertEquals("Token fallito", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testEccezioneRisorsaNonTrovata() {
        Throwable cause = new RuntimeException("Not found in DB");
        EccezioneRisorsaNonTrovata ex1 = new EccezioneRisorsaNonTrovata("User 1 non trovato");
        EccezioneRisorsaNonTrovata ex2 = new EccezioneRisorsaNonTrovata("Prodotto 2 non trovato", cause);

        assertEquals("User 1 non trovato", ex1.getMessage());
        assertNull(ex1.getCause());
        assertEquals("Prodotto 2 non trovato", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
}