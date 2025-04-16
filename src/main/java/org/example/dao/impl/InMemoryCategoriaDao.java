package org.example.dao.impl;

import org.example.dao.CategoriaDao;
import org.example.exception.EccezioneAccessoDati;
import org.example.entity.Categoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementazione di CategoriaDao che utilizza un archivio in memoria per gestire le categorie.
 * La classe è thread-safe e utilizza un ReentrantLock per garantire la consistenza durante le operazioni.
 */
public class InMemoryCategoriaDao implements CategoriaDao {

    // Lista thread-safe per memorizzare le categorie
    private static final List<Categoria> archivioCategorie = new CopyOnWriteArrayList<>();
    // Contatore atomico per generare ID univoci per le categorie
    private static final AtomicLong contatoreId = new AtomicLong(0);
    // Lock per garantire atomicità nelle operazioni composite
    private static final ReentrantLock blocco = new ReentrantLock();

    // Blocco statico per inizializzare alcune categorie di default
    static {
        try {
            salvaInterno(new Categoria(null, "Frutta & Verdura", "images/default_fv.png"));
            salvaInterno(new Categoria(null, "Salumi & Formaggi", "images/default_sf.png"));
            System.out.println("Categorie Inizializzate (lista in memoria)");
        } catch (EccezioneAccessoDati e) {
            System.err.println("Errore durante inizializzazione categorie: " + e.getMessage());
        }
    }

    /**
     * Metodo helper per salvare una categoria durante l'inizializzazione statica.
     * Verifica la presenza di duplicati prima di aggiungere la categoria.
     *
     * @param categoria La categoria da salvare.
     * @throws EccezioneAccessoDati Se il nome della categoria è duplicato.
     */
    private static void salvaInterno(Categoria categoria) throws EccezioneAccessoDati {
        blocco.lock();
        try {
            if (trovaPerNomeInterno(categoria.getNome()).isPresent()) {
                System.err.println("Categoria duplicata durante inizializzazione ignorata: " + categoria.getNome());
                return;
            }
            categoria.setId(contatoreId.incrementAndGet());
            archivioCategorie.add(categoria);
        } finally {
            blocco.unlock();
        }
    }

    /**
     * Versione interna di trovaPerNome senza lock.
     * Utilizzata all'interno di metodi già lockati.
     *
     * @param nome Il nome della categoria da cercare.
     * @return Un Optional contenente la categoria se trovata, altrimenti un Optional vuoto.
     */
    private static Optional<Categoria> trovaPerNomeInterno(String nome) {
        if (nome == null) return Optional.empty();
        return archivioCategorie.stream()
                .filter(c -> nome.equalsIgnoreCase(c.getNome()))
                .findFirst();
    }

    /**
     * Salva una categoria nell'archivio in memoria.
     * Se la categoria non ha un ID, viene considerata nuova e viene assegnato un nuovo ID.
     * Altrimenti, la categoria viene aggiornata se già presente.
     *
     * @param categoria La categoria da salvare, non può essere nulla.
     * @return La categoria salvata, con l'ID assegnato se nuova.
     * @throws IllegalArgumentException Se la categoria è nulla.
     * @throws EccezioneAccessoDati In caso di nome duplicato o categoria non esistente durante l'aggiornamento.
     */
    @Override
    public Categoria salva(Categoria categoria) {
        if (categoria == null) throw new IllegalArgumentException("La categoria non può essere nulla");

        blocco.lock();
        try {
            if (categoria.getId() == null) { // Nuova categoria
                if (trovaPerNomeInterno(categoria.getNome()).isPresent()) {
                    throw new EccezioneAccessoDati("Categoria con nome '" + categoria.getNome() + "' già esistente.");
                }
                categoria.setId(contatoreId.incrementAndGet());
                archivioCategorie.add(categoria);
            } else { // Aggiornamento categoria esistente
                Optional<Categoria> optEsistente = trovaPerId(categoria.getId());
                if (optEsistente.isEmpty()) {
                    throw new EccezioneAccessoDati("Impossibile aggiornare categoria non esistente con id: " + categoria.getId());
                }
                Categoria esistente = optEsistente.get();

                if (!categoria.getNome().equalsIgnoreCase(esistente.getNome())) {
                    if (trovaPerNomeInterno(categoria.getNome()).filter(trovata -> !trovata.getId().equals(categoria.getId())).isPresent()) {
                        throw new EccezioneAccessoDati("Impossibile aggiornare il nome della categoria a '" + categoria.getNome() + "', è già usato da un'altra categoria.");
                    }
                }

                archivioCategorie.removeIf(c -> c.getId().equals(categoria.getId()));
                archivioCategorie.add(categoria);
            }
        } finally {
            blocco.unlock();
        }
        return categoria;
    }

    /**
     * Trova una categoria per ID.
     *<
     * @param id L'ID della categoria da cercare.
     * @return Un Optional contenente la categoria se trovata, altrimenti un Optional vuoto.
     */
    @Override
    public Optional<Categoria> trovaPerId(Long id) {
        if (id == null) return Optional.empty();
        return archivioCategorie.stream()
                .filter(c -> id.equals(c.getId()))
                .findFirst();
    }

    /**
     * Trova una categoria per nome.
     *
     * @param nome Il nome della categoria da cercare.
     * @return Un Optional contenente la categoria se trovata, altrimenti un Optional vuoto.
     */
    @Override
    public Optional<Categoria> trovaPerNome(String nome) {
        return trovaPerNomeInterno(nome);
    }

    /**
     * Restituisce una lista di tutte le categorie memorizzate.
     *
     * @return Una lista contenente tutte le categorie.
     */
    @Override
    public List<Categoria> trovaTutte() {
        return new ArrayList<>(archivioCategorie);
    }
}
