package org.example.service;

import org.example.dto.ProdottoDTO;
import org.example.exception.EccezioneRisorsaNonTrovata;

import java.util.List;
import java.util.Optional;

public interface ServizioProdotto {
    /**
     * Salva un nuovo prodotto.
     * Le entità referenziate (Categoria, Tipologia, UnitaMisura) devono esistere.
     *
     * @param prodottoDTO DTO del prodotto da salvare.
     * @return Il DTO del prodotto salvato con l'ID assegnato.
     * @throws EccezioneRisorsaNonTrovata se una delle entità referenziate (Categoria, Tipologia, UnitaMisura) non viene trovata.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    ProdottoDTO salva(ProdottoDTO prodottoDTO) throws EccezioneRisorsaNonTrovata;

    /**
     * Trova un prodotto per il suo ID.
     *
     * @param id L'ID del prodotto.
     * @return Un Optional contenente il DTO del prodotto se trovato, altrimenti Optional.empty().
     */
    Optional<ProdottoDTO> trovaPerId(Long id);

    /**
     * Recupera tutti i prodotti.
     *
     * @return Una lista di tutti i DTO dei prodotti.
     */
    List<ProdottoDTO> trovaTutti();

    /**
     * Trova prodotti appartenenti a una specifica categoria.
     *
     * @param idCategoria L'ID della categoria.
     * @return Lista di DTO dei prodotti per la categoria data.
     */
    List<ProdottoDTO> trovaPerIdCategoria(Long idCategoria);


    /**
     * Modifica un prodotto esistente.
     *
     * @param prodottoDTO DTO con i dati aggiornati del prodotto (l'ID deve essere presente).
     * @return Il DTO del prodotto modificato.
     * @throws EccezioneRisorsaNonTrovata se il prodotto o una delle sue entità referenziate non vengono trovate.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    ProdottoDTO modifica(ProdottoDTO prodottoDTO) throws EccezioneRisorsaNonTrovata;

    /**
     * Elimina un prodotto per ID.
     *
     * @param id L'ID del prodotto da eliminare.
     * @throws EccezioneRisorsaNonTrovata se il prodotto con l'ID specificato non esiste.
     */
    void elimina(Long id) throws EccezioneRisorsaNonTrovata;
}