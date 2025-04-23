package org.example.exception;

/**
 * Classe di eccezione per la gestione degli errori relativi a risorse non trovate.
 * Estende Exception per fornire eccezioni controllate (checked) relative a risorse mancanti.
 */
public class EccezioneRisorsaNonTrovata extends Exception {

    /**
     * Costruisce una nuova EccezioneRisorsaNonTrovata con il messaggio di dettaglio specificato.
     *
     * @param message Il messaggio di dettaglio dell'eccezione.
     */
    public EccezioneRisorsaNonTrovata(String message) {
        super(message);
    }
}
