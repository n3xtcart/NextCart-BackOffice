package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.dto.CategoriaDTO;

/**
 * Interfaccia per la gestione dei servizi relativi all'entità Categoria.
 */
public interface ServizioCategoria {

    /**
     * Salva una nuova categoria.
     *
     * @param categoriaDTO L'oggetto CategoriaDTO da salvare.
     * @return L'oggetto CategoriaDTO salvato.
     */
    CategoriaDTO salva(CategoriaDTO categoriaDTO);

    /**
     * Trova una categoria per il suo identificatore univoco.
     *
     * @param id L'identificatore univoco della categoria.
     * @return Un oggetto Optional contenente la CategoriaDTO, se trovata.
     */
    Optional<CategoriaDTO> trovaPerId(Long id);

    /**
     * Trova una categoria per il suo nome.
     *
     * @param nome Il nome della categoria.
     * @return Un oggetto Optional contenente la CategoriaDTO, se trovata.
     */
    Optional<CategoriaDTO> trovaPerNome(String nome);

    /**
     * Recupera tutte le categorie.
     *
     * @return Una lista di oggetti CategoriaDTO.
     */
    List<CategoriaDTO> trovaTutte();

    /**
     * Modifica una categoria esistente.
     *
     * @param categoriaDTO L'oggetto CategoriaDTO con i dati aggiornati.
     * @return L'oggetto CategoriaDTO modificato.
     */
    CategoriaDTO modifica(CategoriaDTO categoriaDTO);

    /**
     * Elimina una categoria in base al suo identificatore.
     *
     * @param id L'identificatore univoco della categoria da eliminare.
     */
    void elimina(Long id);
}
