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

    @Transactional
    public UtenteDTO registraAdminDiDefault(String email, String passwordInChiaro, String ruolo) {
        if (utenteRepository.findByEmail(email).isPresent()) {
            System.out.println("Utente " + ruolo + " con email " + email + " già esistente.");
            return utenteRepository.findByEmail(email)
                    .map(u -> new UtenteDTO(u.getId(), u.getEmail(), u.getRuolo()))
                    .orElse(null);
        }
        Utente admin = new Utente();
        admin.setEmail(email);
        admin.setHashPassword(CodificatorePassword.calcolaHashPassword(passwordInChiaro)); // INSICURO
        admin.setRuolo(ruolo);
        utenteRepository.persist(admin);
        System.out.println("Utente " + ruolo + " creato: " + email);
        return new UtenteDTO(admin.getId(), admin.getEmail(), admin.getRuolo());
    }
}