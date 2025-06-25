// org/example/repository/ProdottoRepository.java
package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Prodotto;

import java.util.List;

@ApplicationScoped
public class ProdottoRepository implements PanacheRepositoryBase<Prodotto, Long> {

    public List<Prodotto> findByCategoriaId(Long categoriaId) {
        return list("categoria.id", categoriaId);
    }

    public long countByCategoriaId(Long categoriaId) {
        return count("categoria.id", categoriaId);
    }
}