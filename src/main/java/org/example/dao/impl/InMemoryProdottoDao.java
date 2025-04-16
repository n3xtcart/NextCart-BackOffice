package org.example.dao.impl;

import org.example.dao.ProdottoDao;
import org.example.exception.EccezioneAccessoDati;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementazione di ProdottoDao che utilizza un archivio in memoria per gestire i prodotti.
 * **Questa implementazione NON è thread-safe.** Utilizza un semplice ArrayList.
 */
public class InMemoryProdottoDao implements ProdottoDao {

    // Lista standard per memorizzare i prodotti
    private static final List<Prodotto> archivioProdotti = new ArrayList<>();
    // Contatore standard per generare ID univoci per i prodotti
    private static long contatoreId = 0;

    /**
     * Salva un prodotto nell'archivio in memoria.
     * Se il prodotto non ha un ID, viene considerato nuovo e viene assegnato un nuovo ID.
     * Altrimenti, il prodotto viene aggiornato se già presente.
     * **Attenzione: questo metodo non è thread-safe.**
     *
     * @param prodotto Il prodotto da salvare, non può essere nullo.
     * @return Il prodotto salvato, con l'ID assegnato se nuovo.
     * @throws IllegalArgumentException Se il prodotto è nullo.
     * @throws EccezioneAccessoDati In caso non esista un prodotto da aggiornare.
     */
    @Override
    public Prodotto salva(Prodotto prodotto) {
        if (prodotto == null) throw new IllegalArgumentException("Il prodotto non può essere nullo");

        // Nessun lock necessario
        if (prodotto.getId() == null) { // Nuovo prodotto
            prodotto.setId(++contatoreId); // Incrementa e assegna ID
            archivioProdotti.add(prodotto);
        } else { // Aggiornamento del prodotto esistente
            // Verifica prima se esiste un prodotto con quell'ID
            boolean esiste = trovaPerId(prodotto.getId()).isPresent();
            if (!esiste) {
                throw new EccezioneAccessoDati("Impossibile aggiornare prodotto non esistente con id: " + prodotto.getId());
            }
            // Rimuove il vecchio prodotto e aggiunge il nuovo (aggiornato)
            archivioProdotti.removeIf(p -> p.getId().equals(prodotto.getId()));
            archivioProdotti.add(prodotto);
        }

        return prodotto; // Restituisce l'oggetto salvato/aggiornato
    }

    /**
     * Cerca un prodotto per ID.
     *
     * @param id L'ID del prodotto da cercare.
     * @return Un Optional contenente il prodotto se trovato, in caso contrario un Optional vuoto.
     */
    @Override
    public Optional<Prodotto> trovaPerId(Long id) {
        if (id == null) return Optional.empty();
        // Usa lo stream sulla lista standard
        return archivioProdotti.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
    }

    /**
     * Restituisce una copia di tutti i prodotti memorizzati.
     * Restituire una copia è una buona pratica per evitare modifiche esterne accidentali.
     *
     * @return Una nuova lista contenente tutti i prodotti.
     */
    @Override
    public List<Prodotto> trovaTutti() {
        // Restituisce una copia della lista interna
        return new ArrayList<>(archivioProdotti);
    }

    /**
     * Trova tutti i prodotti appartenenti a una determinata categoria.
     *
     * @param idCategoria L'ID della categoria.
     * @return Una lista di prodotti appartenenti alla categoria specificata, oppure una lista vuota se l'ID è nullo.
     */
    @Override
    public List<Prodotto> trovaPerIdCategoria(Long idCategoria) {
        if (idCategoria == null) return List.of(); // Restituisce una lista vuota immutabile
        // Usa lo stream sulla lista standard
        return archivioProdotti.stream()
                .filter(p -> idCategoria.equals(p.getIdCategoria()))
                .collect(Collectors.toList()); // Raccoglie in una nuova lista
    }

    @Override
    public void elimina(Long id) {
        Optional<Prodotto> prodotto = trovaPerId(id);
        if (prodotto.isPresent()) {
        	archivioProdotti.remove(prodotto.get());
        } else {
            throw new IllegalArgumentException("Categoria non trovata per ID: " + id);
        }
    }

	@Override
	public Prodotto modifica(Prodotto prodotto) {
		Optional<Prodotto> prodottoEsistente = trovaPerId(prodotto.getId());
        if (prodottoEsistente.isEmpty()) {
            throw new IllegalArgumentException("Prodotto non trovato per ID: " + prodotto.getId());
        }
        Prodotto esistente = prodottoEsistente.get();
        esistente.setNome(prodotto.getNome());
        esistente.setDescrizione(prodotto.getDescrizione());
        esistente.setQuantita(prodotto.getQuantita());
        esistente.setPercorsoImmagine(prodotto.getPercorsoImmagine());
        esistente.setIdCategoria(prodotto.getIdCategoria());
        return esistente;
	}
}