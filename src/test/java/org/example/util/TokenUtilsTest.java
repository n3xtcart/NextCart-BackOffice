package org.example.util;

import org.example.dto.UtenteDTO;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TokenUtilsTest {

    private final String ISSUER = "http://test.issuer";
    private final long DURATION_SECONDS = 3600L;

    @Test
    void generateToken_conParametriBase() {
        String token = TokenUtils.generateToken("test@example.com", "1", Set.of("admin"), ISSUER, Duration.ofSeconds(DURATION_SECONDS));
        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateToken_daUtenteDTO_successo() {
        UtenteDTO utente = new UtenteDTO(1L, "admin@example.com", "Admin");
        String token = TokenUtils.generateToken(utente, ISSUER, DURATION_SECONDS);

        assertNotNull(token);

    }

    @Test
    void generateToken_daUtenteDTO_conRuoloNullo() {
        UtenteDTO utente = new UtenteDTO(2L, "user@example.com", null);
        String token = TokenUtils.generateToken(utente, ISSUER, DURATION_SECONDS);

        assertNotNull(token);

    }

    @Test
    void generateToken_daUtenteDTO_conRuoloVuoto() {
        UtenteDTO utente = new UtenteDTO(3L, "guest@example.com", "  ");
        String token = TokenUtils.generateToken(utente, ISSUER, DURATION_SECONDS);

        assertNotNull(token);

    }

    @Test
    void generateToken_daUtenteDTO_conRuoloAdminSpeciale() {

        UtenteDTO utente = new UtenteDTO(4L, "special@admin.com", "admin");
        String token = TokenUtils.generateToken(utente, ISSUER, DURATION_SECONDS);
        assertNotNull(token);
    }

    @Test
    void generateToken_lanciaEccezione_seUtenteNullo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            TokenUtils.generateToken(null, ISSUER, DURATION_SECONDS);
        });
        assertEquals("Dati utente incompleti per la generazione del token.", ex.getMessage());
    }

    @Test
    void generateToken_lanciaEccezione_seEmailUtenteNullo() {
        UtenteDTO utente = new UtenteDTO(1L, null, "admin");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            TokenUtils.generateToken(utente, ISSUER, DURATION_SECONDS);
        });
        assertEquals("Dati utente incompleti per la generazione del token.", ex.getMessage());
    }

    @Test
    void generateToken_lanciaEccezione_seIdUtenteNullo() {
        UtenteDTO utente = new UtenteDTO(null, "test@example.com", "admin");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            TokenUtils.generateToken(utente, ISSUER, DURATION_SECONDS);
        });
        assertEquals("Dati utente incompleti per la generazione del token.", ex.getMessage());
    }
}