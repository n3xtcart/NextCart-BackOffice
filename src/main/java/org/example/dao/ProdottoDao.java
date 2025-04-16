package org.example.dao;

import org.example.entity.Prodotto;
import java.util.List;
import java.util.Optional;

public interface ProdottoDao {
    Prodotto salva(Prodotto prodotto);
    Optional<Prodotto> trovaPerId(Long id);
    List<Prodotto> trovaTutti();
    List<Prodotto> trovaPerIdCategoria(Long idCategoria);
    Prodotto modifica(Prodotto prodotto);
    void elimina(Long id); 
}
