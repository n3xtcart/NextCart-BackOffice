package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Prodotto;
import java.util.List;

@ApplicationScoped
public class ProdottoRepository implements PanacheRepositoryBase<Prodotto, Long> {

    public List<Prodotto> findByCategoriaId(Long categoriaId) {
        return list("categoria.id", categoriaId);
    }

    public List<Prodotto> findByTipologiaId(Long tipologiaId) {
        return list("tipologia.id", tipologiaId);
    }

    public List<Prodotto> findByUnitaMisuraId(Long unitaMisuraId) {
        return list("unitaMisura.id", unitaMisuraId);
    }

    public long countByCategoriaId(Long categoriaId) {
        return count("categoria.id", categoriaId);
    }

    public long countByTipologiaId(Long tipologiaId) {
        return count("tipologia.id", tipologiaId);
    }

    public long countByUnitaMisuraId(Long unitaMisuraId) {
        return count("unitaMisura.id", unitaMisuraId);
    }
}