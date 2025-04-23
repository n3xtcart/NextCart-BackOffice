package org.example.dao;

import org.example.entity.Categoria;
import java.util.List;
import java.util.Optional;

/**
 * Interfaccia per la gestione delle operazioni di accesso ai dati (DAO)
 * relative all'entità Categoria.
 */
public interface CategoriaDao {

    /**
     * Salva una nuova categoria nel database.
     *
     * @param categoria La categoria da salvare.
     * @return La categoria salvata.
     */
    Categoria salva(Categoria categoria);

    /**
     * Trova una categoria per il suo identificatore univoco.
     *
     * @param id L'identificatore univoco della categoria.
     * @return Un oggetto Optional contenente la categoria, se trovata.
     */
    Optional<Categoria> trovaPerId(Long id);

    /**
     * Trova una categoria per il suo nome.
     *
     * @param nome Il nome della categoria.
     * @return Un oggetto Optional contenente la categoria, se trovata.
     */
    Optional<Categoria> trovaPerNome(String nome);

    /**
     * Recupera tutte le categorie presenti nel database.
     *
     * @return Una lista di tutte le categorie.
     */
    List<Categoria> trovaTutte();

    /**
     * Modifica una categoria esistente nel database.
     *
     * @param categoria La categoria con i dati aggiornati.
     * @return La categoria modificata.
     */
    Categoria modifica(Categoria categoria);

    /**
     * Elimina una categoria dal database in base al suo identificatore.
     *
     * @param id L'identificatore univoco della categoria da eliminare.
     */
    void elimina(Long id);
}

