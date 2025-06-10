package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Categoria;
import java.util.Optional;

@ApplicationScoped
public class CategoriaRepository implements PanacheRepositoryBase<Categoria, Long> {
    public Optional<Categoria> findByNome(String nome) {
        return find("LOWER(nome)", nome.toLowerCase()).firstResultOptional();
    }
}