package org.example.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.dao.CategoriaDao;
import org.example.entity.Categoria;
import org.example.exception.EccezioneAccessoDati;

/**
 * Implementazione di CategoriaDao che utilizza un archivio in memoria per gestire le categorie.
 * **Questa implementazione NON è thread-safe.** Utilizza un semplice ArrayList.
 */
public class InMemoryCategoriaDao implements CategoriaDao {

    private static final List<Categoria> archivioCategorie = new ArrayList<>();
    private static long contatoreId = 0;


    static {
        try {
            // Inizializzazione Categoria 1
            Categoria fruttaVerdura = new Categoria(null, "Frutta & Verdura", "images/default_fv.png");
            // Verifica duplicato direttamente qui
            if (trovaPerNomeInterno(fruttaVerdura.getNome()).isEmpty()) {
                fruttaVerdura.setId(++contatoreId); // Assegna ID
                archivioCategorie.add(fruttaVerdura); // Aggiungi alla lista
            } else {
                // Gestione duplicato come prima (ignorando)
                System.err.println("Categoria duplicata durante inizializzazione ignorata: " + fruttaVerdura.getNome());
            }

            // Inizializzazione Categoria 2
            Categoria salumiFormaggi = new Categoria(null, "Salumi & Formaggi", "images/default_sf.png");
            // Verifica duplicato direttamente qui
            if (trovaPerNomeInterno(salumiFormaggi.getNome()).isEmpty()) {
                salumiFormaggi.setId(++contatoreId); // Assegna ID
                archivioCategorie.add(salumiFormaggi); // Aggiungi alla lista
            } else {
                // Gestione duplicato come prima (ignorando)
                System.err.println("Categoria duplicata durante inizializzazione ignorata: " + salumiFormaggi.getNome());
            }

            System.out.println("Categorie Inizializzate (lista in memoria - NON thread-safe)");

        } catch (Exception e) { // Cattura generica per sicurezza durante init statico
            System.err.println("Errore imprevisto durante inizializzazione categorie: " + e.getMessage());
            // Potrebbe essere necessario un logging più robusto qui
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
        if (trovaPerNomeInterno(categoria.getNome()).isPresent()) {
            System.err.println("Categoria duplicata durante inizializzazione ignorata: " + categoria.getNome());
            return; // Ignora duplicati durante l'init
        }
        categoria.setId(++contatoreId); // Incrementa e assegna
        archivioCategorie.add(categoria);
    }

    /**
     * Versione interna di trovaPerNome.
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
     * **Attenzione: questo metodo non è thread-safe.**
     *
     * @param categoria La categoria da salvare, non può essere nulla.
     * @return La categoria salvata, con l'ID assegnato se nuova.
     * @throws IllegalArgumentException Se la categoria è nulla.
     * @throws EccezioneAccessoDati In caso di nome duplicato o categoria non esistente durante l'aggiornamento.
     */
    @Override
    public Categoria salva(Categoria categoria) {
        if (categoria == null) throw new IllegalArgumentException("La categoria non può essere nulla");

        // Nessun lock necessario
        if (categoria.getId() == null) { // Nuova categoria
            if (trovaPerNomeInterno(categoria.getNome()).isPresent()) {
                throw new EccezioneAccessoDati("Categoria con nome '" + categoria.getNome() + "' già esistente.");
            }
            categoria.setId(++contatoreId); // Incrementa e assegna
            archivioCategorie.add(categoria);
        } else { // Aggiornamento categoria esistente
            Optional<Categoria> optEsistente = trovaPerId(categoria.getId());
            if (optEsistente.isEmpty()) {
                throw new EccezioneAccessoDati("Impossibile aggiornare categoria non esistente con id: " + categoria.getId());
            }
            Categoria esistente = optEsistente.get();

            // Controlla duplicati nome *solo* se il nome è cambiato
            if (!categoria.getNome().equalsIgnoreCase(esistente.getNome())) {
                // Cerca se esiste un'altra categoria (diversa da quella che stiamo aggiornando) con il nuovo nome
                if (trovaPerNomeInterno(categoria.getNome()).filter(trovata -> !trovata.getId().equals(categoria.getId())).isPresent()) {
                    throw new EccezioneAccessoDati("Impossibile aggiornare il nome della categoria a '" + categoria.getNome() + "', è già usato da un'altra categoria.");
                }
            }

            // Rimuovi il vecchio elemento e aggiungi quello aggiornato
            // ArrayList non è ottimizzato per questo come CopyOnWriteArrayList, ma funziona
            archivioCategorie.removeIf(c -> c.getId().equals(categoria.getId()));
            archivioCategorie.add(categoria); // Aggiunge la versione aggiornata
        }

        return categoria; // Restituisce l'oggetto salvato/aggiornato
    }

    /**
     * Trova una categoria per ID.
     *
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
        // Chiama direttamente la versione interna
        return trovaPerNomeInterno(nome);
    }

    /**
     * Restituisce una lista di tutte le categorie memorizzate.
     * Restituisce una *copia* della lista interna per evitare modifiche esterne accidentali.
     *
     * @return Una nuova lista contenente tutte le categorie.
     */
    @Override
    public List<Categoria> trovaTutte() {
        // Restituire una copia è una buona pratica anche in single-thread
        return new ArrayList<>(archivioCategorie);
    }

    @Override
    public void elimina(Long id) {
        Optional<Categoria> categoria = trovaPerId(id);
        if (categoria.isPresent()) {
            archivioCategorie.remove(categoria.get());
        } else {
            throw new IllegalArgumentException("Categoria non trovata per ID: " + id);
        }
    }

	@Override
	public Categoria modifica(Categoria categoria) {
        Optional<Categoria> categoriaEsistente = trovaPerId(categoria.getId());
        if (categoriaEsistente.isEmpty()) {
            throw new IllegalArgumentException("Categoria non trovata per ID: " + categoria.getId());
        }
        Categoria esistente = categoriaEsistente.get();
        esistente.setNome(categoria.getNome());
        esistente.setPercorsoImmagine(categoria.getPercorsoImmagine());
        return esistente;
	}

}