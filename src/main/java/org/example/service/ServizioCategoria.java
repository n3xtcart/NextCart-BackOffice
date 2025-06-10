package org.example.service;

import org.example.dto.CategoriaDTO;
import org.example.exception.EccezioneAccessoDati; // Per coerenza con implementazione
import org.example.exception.EccezioneRisorsaNonTrovata;

import java.util.List;
import java.util.Optional;

public interface ServizioCategoria {
    /**
     * Salva una nuova categoria.
     * Se il nome della categoria esiste già, lancia EccezioneAccessoDati.
     *
     * @param categoriaDTO DTO della categoria da salvare.
     * @return Il DTO della categoria salvata con l'ID assegnato.
     * @throws EccezioneAccessoDati se il nome è duplicato o altri errori DB.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    CategoriaDTO salva(CategoriaDTO categoriaDTO);

    /**
     * Trova una categoria per il suo ID.
     *
     * @param id L'ID della categoria.
     * @return Un Optional contenente il DTO della categoria se trovata, altrimenti Optional.empty().
     */
    Optional<CategoriaDTO> trovaPerId(Long id);

    /**
     * Trova una categoria per il suo nome (case-insensitive).
     *
     * @param nome Il nome della categoria.
     * @return Un Optional contenente il DTO della categoria se trovata, altrimenti Optional.empty().
     */
    Optional<CategoriaDTO> trovaPerNome(String nome);

    /**
     * Recupera tutte le categorie.
     *
     * @return Una lista di tutti i DTO delle categorie.
     */
    List<CategoriaDTO> trovaTutte();

    /**
     * Modifica una categoria esistente.
     *
     * @param categoriaDTO DTO con i dati aggiornati della categoria (l'ID deve essere presente).
     * @return Il DTO della categoria modificata.
     * @throws EccezioneRisorsaNonTrovata se la categoria con l'ID specificato non esiste.
     * @throws EccezioneAccessoDati se il nuovo nome è duplicato o altri errori DB.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    CategoriaDTO modifica(CategoriaDTO categoriaDTO) throws EccezioneRisorsaNonTrovata;

    /**
     * Elimina una categoria per ID.
     *
     * @param id L'ID della categoria da eliminare.
     * @throws EccezioneRisorsaNonTrovata se la categoria con l'ID specificato non esiste.
     * @throws EccezioneAccessoDati se la categoria è referenziata da altre entità (es. Prodotti)
     *                                e non può essere eliminata a causa di vincoli di integrità referenziale.
     */
    void elimina(Long id) throws EccezioneRisorsaNonTrovata, EccezioneAccessoDati;
}