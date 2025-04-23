package org.example.dao;

import org.example.entity.Prodotto;
import java.util.List;
import java.util.Optional;

/**
 * Interfaccia per la gestione delle operazioni di accesso ai dati (DAO)
 * relative all'entità Prodotto.
 */
public interface ProdottoDao {

    /**
     * Salva un nuovo prodotto nel database.
     *
     * @param prodotto Il prodotto da salvare.
     * @return Il prodotto salvato.
     */
    Prodotto salva(Prodotto prodotto);

    /**
     * Trova un prodotto per il suo identificatore univoco.
     *
     * @param id L'identificatore univoco del prodotto.
     * @return Un oggetto Optional contenente il prodotto, se trovato.
     */
    Optional<Prodotto> trovaPerId(Long id);

    /**
     * Recupera tutti i prodotti presenti nel database.
     *
     * @return Una lista di tutti i prodotti.
     */
    List<Prodotto> trovaTutti();

    /**
     * Trova i prodotti associati a una specifica categoria.
     *
     * @param idCategoria L'identificatore univoco della categoria.
     * @return Una lista di prodotti appartenenti alla categoria specificata.
     */
    List<Prodotto> trovaPerIdCategoria(Long idCategoria);

    /**
     * Modifica un prodotto esistente nel database.
     *
     * @param prodotto Il prodotto con i dati aggiornati.
     * @return Il prodotto modificato.
     */
    Prodotto modifica(Prodotto prodotto);

    /**
     * Elimina un prodotto dal database in base al suo identificatore.
     *
     * @param id L'identificatore univoco del prodotto da eliminare.
     */
    void elimina(Long id);
}
