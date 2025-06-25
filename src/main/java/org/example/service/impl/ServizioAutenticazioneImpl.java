package org.example.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.example.dto.RichiestaLoginDTO;
import org.example.dto.UtenteDTO;
import org.example.entity.Utente;

import org.example.exception.EccezioneAutenticazione;
import org.example.repository.UtenteRepository;
import org.example.service.ServizioAutenticazione;
import org.example.util.CodificatorePassword;

@ApplicationScoped
public class ServizioAutenticazioneImpl implements ServizioAutenticazione {

    @Inject
    UtenteRepository utenteRepository;

    @Override
    public UtenteDTO login(RichiestaLoginDTO richiestaLogin) throws EccezioneAutenticazione {
        Utente utente = utenteRepository.findByEmail(richiestaLogin.getEmail())
                .orElseThrow(() -> new EccezioneAutenticazione("Credenziali non valide. (Utente non trovato)"));


        if (!CodificatorePassword.verificaPassword(richiestaLogin.getPassword(), utente.getHashPassword())) {
            throw new EccezioneAutenticazione("Credenziali non valide. (Password errata)");
        }
        return new UtenteDTO(utente.getId(), utente.getEmail(), utente.getRuolo());
    }

    @Override
    @Transactional
    public void inizializzaUtenteAdminSeAssente(String email, String passwordInChiaro, String ruolo) {
        if (utenteRepository.findByEmail(email).isEmpty()) {
            Utente admin = new Utente();
            admin.setEmail(email);
            admin.setHashPassword(CodificatorePassword.calcolaHashPassword(passwordInChiaro));
            admin.setRuolo(ruolo);
            utenteRepository.persist(admin);
            System.out.println("Utente " + ruolo + " creato: " + email);
        } else {
            System.out.println("Utente " + ruolo + " con email " + email + " già esistente. Nessuna azione richiesta.");
        }
    }
}