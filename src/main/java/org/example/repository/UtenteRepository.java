package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Utente;
import java.util.Optional;

@ApplicationScoped
public class UtenteRepository implements PanacheRepositoryBase<Utente, Long> {
    public Optional<Utente> findByEmail(String email) {
        return find("LOWER(email)", email.toLowerCase()).firstResultOptional();
    }
}