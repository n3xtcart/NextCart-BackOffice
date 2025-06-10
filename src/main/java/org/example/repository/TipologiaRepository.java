package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Tipologia;
import java.util.Optional;

@ApplicationScoped
public class TipologiaRepository implements PanacheRepositoryBase<Tipologia, Long> {
    public Optional<Tipologia> findByNome(String nome) {
        return find("nome", nome).firstResultOptional();
    }
}