package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.service.impl.ServizioAutenticazioneImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe ServizioAutenticazioneImpl.
 * Verifica il corretto comportamento del costruttore e l'istanziazione del servizio.
 */
@DisplayName("Test ServizioAutenticazioneImpl (Vuoto)")
class TestServizioAutenticazioneImpl {

    /**
     * Test per verificare l'istanziazione della classe ServizioAutenticazioneImpl.
     * Controlla che l'oggetto venga creato correttamente e non sia null.
     */
    @Test
    @DisplayName("Istanziazione per Copertura Costruttore")
    void testIstanziazione() {
        ServizioAutenticazioneImpl servizio = new ServizioAutenticazioneImpl();
        assertNotNull(servizio, "L'oggetto ServizioAutenticazioneImpl dovrebbe essere istanziato correttamente");
    }
}