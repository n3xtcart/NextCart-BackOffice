package org.example.dao.impl;

import org.example.dao.ProdottoDao;
import org.example.exception.EccezioneAccessoDati;
import org.example.entity.Prodotto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementazione di ProdottoDao che utilizza un archivio in memoria per gestire i prodotti.
 * La classe è thread-safe e utilizza un ReentrantLock per garantire la consistenza durante le operazioni.
 */
public class InMemoryProdottoDao implements ProdottoDao {

    // Lista thread-safe per memorizzare i prodotti
    private static final List<Prodotto> archivioProdotti = new CopyOnWriteArrayList<>();
    // Contatore atomico per generare ID univoci per i prodotti
    private static final AtomicLong contatoreId = new AtomicLong(0);
    // Lock per garantire coerenza nelle operazioni di salvataggio (update)
    private static final ReentrantLock blocco = new ReentrantLock();

    /**
     * Salva un prodotto nell'archivio in memoria.
     * Se il prodotto non ha un ID, viene considerato nuovo e viene assegnato un nuovo ID.
     * Altrimenti, il prodotto viene aggiornato se già presente.
     *
     * @param prodotto Il prodotto da salvare, non può essere nullo.
     * @return Il prodotto salvato, con l'ID assegnato se nuovo.
     * @throws IllegalArgumentException Se il prodotto è nullo.
     * @throws EccezioneAccessoDati In caso non esista un prodotto da aggiornare.
     */
    @Override
    public Prodotto salva(Prodotto prodotto) {
        if (prodotto == null) throw new IllegalArgumentException("Il prodotto non può essere nullo");

        blocco.lock();
        try {
            if (prodotto.getId() == null) { // Nuovo prodotto
                prodotto.setId(contatoreId.incrementAndGet());
                archivioProdotti.add(prodotto);
            } else { // Aggiornamento del prodotto esistente
                boolean esiste = trovaPerId(prodotto.getId()).isPresent();
                if (!esiste) {
                    throw new EccezioneAccessoDati("Impossibile aggiornare prodotto non esistente con id: " + prodotto.getId());
                }
                // Rimuove il vecchio prodotto e aggiunge il nuovo per simulare l'aggiornamento
                archivioProdotti.removeIf(p -> p.getId().equals(prodotto.getId()));
                archivioProdotti.add(prodotto);
            }
        } finally {
            blocco.unlock();
        }
        return prodotto;
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
        return archivioProdotti.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
    }

    /**
     * Restituisce una copia snapshot di tutti i prodotti memorizzati.
     *
     * @return Una lista contenente tutti i prodotti.
     */
    @Override
    public List<Prodotto> trovaTutti() {
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
        if (idCategoria == null) return List.of();
        return archivioProdotti.stream()
                .filter(p -> idCategoria.equals(p.getIdCategoria()))
                .collect(Collectors.toList());
    }
}
