package org.example.util;

import io.smallrye.jwt.build.Jwt;
import org.example.dto.UtenteDTO;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class TokenUtils {

    /**
     * Genera un token JWT.
     * La chiave privata per la firma è letta automaticamente da Quarkus
     * tramite la property smallrye.jwt.sign.key.location in application.properties.
     *
     * @param email Email dell'utente (usata come User Principal Name - upn).
     * @param userId ID dell'utente (usato come subject - sub).
     * @param roles Set di ruoli/gruppi dell'utente.
     * @param issuer L'issuer del token (deve corrispondere a mp.jwt.verify.issuer).
     * @param expiresIn Durata di validità del token.
     * @return Il token JWT firmato come stringa.
     */
    public static String generateToken(String email, String userId, Set<String> roles, String issuer, Duration expiresIn) {
        return Jwt.issuer(issuer)
                .upn(email)
                .subject(userId)
                .groups(roles)
                .expiresIn(expiresIn)
                .sign();
    }

    /**
     * Metodo di convenienza per generare un token JWT da un UtenteDTO.
     *
     * @param utente DTO dell'utente contenente ID, email e ruolo.
     * @param issuer L'issuer del token.
     * @param durationInSeconds Durata di validità del token in secondi.
     * @return Il token JWT firmato.
     */
    public static String generateToken(UtenteDTO utente, String issuer, long durationInSeconds) {
        if (utente == null || utente.getEmail() == null || utente.getId() == null) {
            throw new IllegalArgumentException("Dati utente incompleti per la generazione del token.");
        }
        Set<String> roles = new HashSet<>();
        if (utente.getRuolo() != null && !utente.getRuolo().trim().isEmpty()) {
            roles.add(utente.getRuolo().toLowerCase());
        } else {
            System.err.println("Avviso: Utente " + utente.getEmail() + " non ha un ruolo specificato per il token JWT.");
        }
        if (roles.isEmpty() && "admin".equalsIgnoreCase(utente.getRuolo())) {
            roles.add("admin");
        }


        return generateToken(utente.getEmail(), utente.getId().toString(), roles, issuer, Duration.ofSeconds(durationInSeconds));
    }
}