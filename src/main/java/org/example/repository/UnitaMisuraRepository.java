package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.UnitaMisura;
import java.util.Optional;

@ApplicationScoped
public class UnitaMisuraRepository implements PanacheRepositoryBase<UnitaMisura, Long> {
    public Optional<UnitaMisura> findByNome(String nome) {
        return find("nome", nome).firstResultOptional();
    }
}