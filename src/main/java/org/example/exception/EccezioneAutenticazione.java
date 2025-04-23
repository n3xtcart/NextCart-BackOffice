package org.example.exception;

/**
 * Classe di eccezione per la gestione degli errori di autenticazione.
 * Estende Exception per fornire eccezioni controllate (checked) relative ai problemi di autenticazione.
 */
public class EccezioneAutenticazione extends Exception {

    /**
     * Costruisce una nuova EccezioneAutenticazione con il messaggio di dettaglio specificato.
     *
     * @param message Il messaggio di dettaglio dell'eccezione.
     */
    public EccezioneAutenticazione(String message) {
        super(message);
    }
}