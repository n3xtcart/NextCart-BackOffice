package org.example.dao;

import org.example.entity.Utente;
import java.util.Optional;

/**
 * Interfaccia per la gestione delle operazioni di accesso ai dati (DAO)
 * relative all'entità Utente.
 */
public interface UtenteDao {

    /**
     * Trova un utente per il suo indirizzo email.
     *
     * @param email L'indirizzo email dell'utente.
     * @return Un oggetto Optional contenente l'utente, se trovato.
     */
    Optional<Utente> trovaPerEmail(String email);

    /**
     * Trova un utente per il suo identificatore univoco.
     *
     * @param id L'identificatore univoco dell'utente.
     * @return Un oggetto Optional contenente l'utente, se trovato.
     */
    Optional<Utente> trovaPerId(Long id);

    /**
     * Salva un nuovo utente nel database.
     *
     * @param utente L'utente da salvare.
     * @return L'utente salvato.
     */
    Utente salva(Utente utente);
}