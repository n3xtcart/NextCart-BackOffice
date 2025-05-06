//package org.example.dao.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.example.dao.CategoriaDao;
//import org.example.entity.Categoria;
//import org.example.exception.EccezioneAccessoDati;
//
///**
// * Implementazione di CategoriaDao che utilizza un archivio in memoria per gestire le categorie.
// * Utilizza un semplice ArrayList e ora è basata su istanza (NON più statica) per una migliore testabilità.
// */
//public class InMemoryCategoriaDao implements CategoriaDao {
//
//    // --- Instance Variables (Non-Static) ---
//    private final List<Categoria> archivioCategorie = new ArrayList<>();
//    private long contatoreId = 0;
//
//    // --- Static Initializer Block REMOVED ---
//    // The initial data ("Frutta & Verdura", "Salumi & Formaggi")
//    // that was here is no longer automatically added.
//    // Tests (or application setup) should add necessary initial data.
//
//    /**
//     * Versione interna di trovaPerNome (ora non statica).
//     *
//     * @param nome Il nome della categoria da cercare.
//     * @return Un Optional contenente la categoria se trovata, altrimenti un Optional vuoto.
//     */
//    private Optional<Categoria> trovaPerNomeInterno(String nome) {
//        if (nome == null) return Optional.empty();
//        // Usa la lista dell'istanza
//        return this.archivioCategorie.stream()
//                .filter(c -> nome.equalsIgnoreCase(c.getNome()))
//                .findFirst();
//    }
//
//    /**
//     * Salva una categoria nell'archivio in memoria dell'istanza.
//     * Se la categoria non ha un ID, viene considerata nuova e viene assegnato un nuovo ID.
//     * Altrimenti, la categoria viene aggiornata se già presente (rimpiazzando l'elemento esistente).
//     * **Attenzione: questo metodo non è thread-safe.**
//     *
//     * @param categoria La categoria da salvare, non può essere nulla.
//     * @return La categoria salvata, con l'ID assegnato se nuova o aggiornata.
//     * @throws IllegalArgumentException Se la categoria è nulla.
//     * @throws EccezioneAccessoDati In caso di nome duplicato (per nuove categorie o durante aggiornamenti con cambio nome)
//     *                              o categoria non esistente durante l'aggiornamento.
//     */
//    @Override
//    public Categoria salva(Categoria categoria) {
//        if (categoria == null) throw new IllegalArgumentException("La categoria non può essere nulla");
//
//        if (categoria.getId() == null) { // Nuova categoria
//            // Chiama il metodo interno non statico
//            if (trovaPerNomeInterno(categoria.getNome()).isPresent()) {
//                throw new EccezioneAccessoDati("Categoria con nome '" + categoria.getNome() + "' già esistente.");
//            }
//            // Usa contatoreId e archivioCategorie dell'istanza
//            categoria.setId(++this.contatoreId);
//            this.archivioCategorie.add(categoria);
//        } else { // Aggiornamento categoria esistente
//            // Chiama trovaPerId dell'istanza
//            Optional<Categoria> optEsistente = trovaPerId(categoria.getId());
//            if (optEsistente.isEmpty()) {
//                throw new EccezioneAccessoDati("Impossibile aggiornare categoria non esistente con id: " + categoria.getId());
//            }
//            Categoria esistente = optEsistente.get();
//
//            // Controlla duplicati nome *solo* se il nome è cambiato
//            if (!categoria.getNome().equalsIgnoreCase(esistente.getNome())) {
//                // Cerca se esiste un'altra categoria (diversa da quella che stiamo aggiornando) con il nuovo nome
//                // Chiama il metodo interno non statico
//                if (trovaPerNomeInterno(categoria.getNome()).filter(trovata -> !trovata.getId().equals(categoria.getId())).isPresent()) {
//                    throw new EccezioneAccessoDati("Impossibile aggiornare il nome della categoria a '" + categoria.getNome() + "', è già usato da un'altra categoria.");
//                }
//            }
//
//            // Rimuovi il vecchio elemento e aggiungi quello aggiornato per garantire che l'oggetto nella lista sia quello passato
//            // Usa archivioCategorie dell'istanza
//            this.archivioCategorie.removeIf(c -> c.getId().equals(categoria.getId()));
//            this.archivioCategorie.add(categoria); // Aggiunge la versione aggiornata
//        }
//
//        return categoria; // Restituisce l'oggetto salvato/aggiornato
//    }
//
//    /**
//     * Trova una categoria per ID nell'archivio dell'istanza.
//     *
//     * @param id L'ID della categoria da cercare.
//     * @return Un Optional contenente la categoria se trovata, altrimenti un Optional vuoto.
//     */
//    @Override
//    public Optional<Categoria> trovaPerId(Long id) {
//        if (id == null) return Optional.empty();
//        // Usa archivioCategorie dell'istanza
//        return this.archivioCategorie.stream()
//                .filter(c -> id.equals(c.getId()))
//                .findFirst();
//    }
//
//    /**
//     * Trova una categoria per nome nell'archivio dell'istanza.
//     *
//     * @param nome Il nome della categoria da cercare.
//     * @return Un Optional contenente la categoria se trovata, altrimenti un Optional vuoto.
//     */
//    @Override
//    public Optional<Categoria> trovaPerNome(String nome) {
//        // Chiama direttamente la versione interna (ora non statica)
//        return trovaPerNomeInterno(nome);
//    }
//
//    /**
//     * Restituisce una *copia* della lista di tutte le categorie memorizzate nell'istanza.
//     *
//     * @return Una nuova lista contenente tutte le categorie dell'istanza.
//     */
//    @Override
//    public List<Categoria> trovaTutte() {
//        // Restituisce una copia della lista interna dell'istanza
//        return new ArrayList<>(this.archivioCategorie);
//    }
//
//    /**
//     * Elimina una categoria dall'archivio dell'istanza dato il suo ID.
//     *
//     * @param id L'ID della categoria da eliminare.
//     * @throws IllegalArgumentException Se la categoria con l'ID specificato non viene trovata.
//     */
//    @Override
//    public void elimina(Long id) {
//        // Chiama trovaPerId dell'istanza
//        Optional<Categoria> categoriaOpt = trovaPerId(id);
//        if (categoriaOpt.isPresent()) {
//            // Usa archivioCategorie dell'istanza
//            this.archivioCategorie.remove(categoriaOpt.get());
//        } else {
//            throw new IllegalArgumentException("Categoria non trovata per ID: " + id + " per l'eliminazione.");
//        }
//    }
//
//    /**
//     * Modifica i dati di una categoria esistente nell'archivio dell'istanza.
//     * Questo metodo modifica direttamente l'oggetto esistente nella lista.
//     * ATTENZIONE: A differenza di `salva`, questo non rimpiazza l'oggetto, ma aggiorna i campi
//     * dell'oggetto già presente in lista. Assicurarsi che sia il comportamento desiderato.
//     * Controlla anche la duplicazione del nome se viene modificato.
//     *
//     * @param categoria L'oggetto Categoria con l'ID esistente e i nuovi dati.
//     * @return La categoria modificata (l'oggetto esistente aggiornato).
//     * @throws IllegalArgumentException Se la categoria con l'ID specificato non viene trovata.
//     * @throws EccezioneAccessoDati Se il nuovo nome è già utilizzato da un'altra categoria.
//     */
//    @Override
//    public Categoria modifica(Categoria categoria) {
//        if (categoria == null || categoria.getId() == null) {
//            throw new IllegalArgumentException("Categoria o ID della categoria non possono essere nulli per la modifica.");
//        }
//        // Chiama trovaPerId dell'istanza
//        Optional<Categoria> categoriaEsistenteOpt = trovaPerId(categoria.getId());
//        if (categoriaEsistenteOpt.isEmpty()) {
//            throw new IllegalArgumentException("Categoria non trovata per ID: " + categoria.getId() + " per la modifica.");
//        }
//
//        Categoria esistente = categoriaEsistenteOpt.get();
//
//        // Controlla duplicati nome *solo* se il nome è cambiato
//        if (!categoria.getNome().equalsIgnoreCase(esistente.getNome())) {
//            // Chiama il metodo interno non statico
//            if (trovaPerNomeInterno(categoria.getNome()).filter(trovata -> !trovata.getId().equals(categoria.getId())).isPresent()) {
//                throw new EccezioneAccessoDati("Impossibile modificare il nome della categoria a '" + categoria.getNome() + "', è già usato da un'altra categoria.");
//            }
//        }
//
//        // Aggiorna i campi dell'oggetto esistente
//        esistente.setNome(categoria.getNome());
//        esistente.setPercorsoImmagine(categoria.getPercorsoImmagine());
//        return esistente; // Restituisce l'oggetto modificato che è ancora nella lista
//    }
//}