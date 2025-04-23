package org.example.exception;

/**
 * Classe di eccezione personalizzata per la gestione degli errori di accesso ai dati.
 * Estende RuntimeException per fornire eccezioni unchecked relative alle operazioni di accesso ai dati.
 */
public class EccezioneAccessoDati extends RuntimeException {

    /**
     * Costruisce una nuova EccezioneAccessoDati con il messaggio di dettaglio specificato.
     *
     * @param message Il messaggio di dettaglio.
     */
    public EccezioneAccessoDati(String message) {
        super(message);
    }

    /**
     * Costruisce una nuova EccezioneAccessoDati con il messaggio di dettaglio e la causa specificati.
     *
     * @param message Il messaggio di dettaglio.
     * @param cause La causa che ha originato l'eccezione.
     */
    public EccezioneAccessoDati(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Costruisce una nuova EccezioneAccessoDati con la causa specificata.
     *
     * @param cause La causa che ha originato l'eccezione.
     */
    public EccezioneAccessoDati(Throwable cause) {
        super(cause);
    }
}
