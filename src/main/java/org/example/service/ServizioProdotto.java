package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.dto.ProdottoDTO;

/**
 * Interfaccia per la gestione dei servizi relativi all'entità Prodotto.
 */
public interface ServizioProdotto {

    /**
     * Salva un nuovo prodotto.
     *
     * @param prodottoDTO L'oggetto ProdottoDTO da salvare.
     * @return L'oggetto ProdottoDTO salvato.
     */
    ProdottoDTO salva(ProdottoDTO prodottoDTO);

    /**
     * Trova un prodotto per il suo identificatore univoco.
     *
     * @param id L'identificatore univoco del prodotto.
     * @return Un oggetto Optional contenente il ProdottoDTO, se trovato.
     */
    Optional<ProdottoDTO> trovaPerId(Long id);

    /**
     * Recupera tutti i prodotti.
     *
     * @return Una lista di oggetti ProdottoDTO.
     */
    List<ProdottoDTO> trovaTutti();

    /**
     * Trova i prodotti associati a una specifica categoria.
     *
     * @param idCategoria L'identificatore univoco della categoria.
     * @return Una lista di oggetti ProdottoDTO appartenenti alla categoria specificata.
     */
    List<ProdottoDTO> trovaPerIdCategoria(Long idCategoria);

    /**
     * Modifica un prodotto esistente.
     *
     * @param prodottoDTO L'oggetto ProdottoDTO con i dati aggiornati.
     * @return L'oggetto ProdottoDTO modificato.
     */
    ProdottoDTO modifica(ProdottoDTO prodottoDTO);

    /**
     * Elimina un prodotto in base al suo identificatore.
     *
     * @param id L'identificatore univoco del prodotto da eliminare.
     */
    void elimina(Long id);
}
