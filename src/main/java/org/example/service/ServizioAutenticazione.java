package org.example.service;

import org.example.dto.RichiestaLoginDTO;
import org.example.dto.UtenteDTO;
import org.example.exception.EccezioneAutenticazione;

public interface ServizioAutenticazione {
    /**
     * Autentica un utente in base alle credenziali fornite.
     *
     * @param richiestaLogin DTO contenente email e password.
     * @return UtenteDTO con le informazioni dell'utente autenticato (escluso password).
     * @throws EccezioneAutenticazione se l'autenticazione fallisce.
     */
    UtenteDTO login(RichiestaLoginDTO richiestaLogin) throws EccezioneAutenticazione;

    void inizializzaUtenteAdminSeAssente(String email, String password, String ruolo);

}