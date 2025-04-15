package org.example.dao;

import org.example.entity.Utente;
import java.util.Optional;

public interface UtenteDao {
    Optional<Utente> trovaPerEmail(String email);
    Optional<Utente> trovaPerId(Long id);
    Utente salva(Utente utente);
}