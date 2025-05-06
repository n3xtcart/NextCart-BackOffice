//package org.example.dao.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.example.dao.ProdottoDao;
//import org.example.entity.Prodotto;
//import org.example.exception.EccezioneAccessoDati;
//
///**
// * Implementazione di ProdottoDao che utilizza un archivio in memoria per gestire i prodotti.
// * Utilizza un semplice ArrayList e ora è basata su istanza (NON più statica) per una migliore testabilità.
// */
//public class InMemoryProdottoDao implements ProdottoDao {
//
//    private final List<Prodotto> archivioProdotti = new ArrayList<>();
//    private long contatoreId = 0;
//
//    /**
//     * Salva un prodotto nell'archivio in memoria dell'istanza.
//     * Se il prodotto non ha un ID, viene considerato nuovo e viene assegnato un nuovo ID.
//     * Altrimenti, il prodotto viene aggiornato se già presente (rimpiazzando l'elemento esistente).
//     *
//     * @param prodotto Il prodotto da salvare, non può essere nullo.
//     * @return Il prodotto salvato, con l'ID assegnato se nuovo o aggiornato.
//     * @throws IllegalArgumentException Se il prodotto è nullo.
//     * @throws EccezioneAccessoDati In caso di prodotto non esistente durante un tentativo di aggiornamento.
//     */
//    @Override
//    public Prodotto salva(Prodotto prodotto) {
//        if (prodotto == null) throw new IllegalArgumentException("Il prodotto non può essere nullo");
//
//        if (prodotto.getId() == null) { // Nuovo prodotto
//            // Usa contatoreId e archivioProdotti dell'istanza
//            prodotto.setId(++this.contatoreId);
//            this.archivioProdotti.add(prodotto);
//        } else { // Aggiornamento prodotto esistente
//            // Chiama trovaPerId dell'istanza
//            boolean esiste = trovaPerId(prodotto.getId()).isPresent();
//            if (!esiste) {
//                throw new EccezioneAccessoDati("Impossibile aggiornare prodotto non esistente con id: " + prodotto.getId());
//            }
//            // Rimuovi il vecchio e aggiungi il nuovo per garantire che l'oggetto nella lista sia quello passato
//            // Usa archivioProdotti dell'istanza
//            this.archivioProdotti.removeIf(p -> p.getId().equals(prodotto.getId()));
//            this.archivioProdotti.add(prodotto);
//        }
//
//        return prodotto; // Restituisce l'oggetto salvato/aggiornato
//    }
//
//    /**
//     * Trova un prodotto per ID nell'archivio dell'istanza.
//     *
//     * @param id L'ID del prodotto da cercare.
//     * @return Un Optional contenente il prodotto se trovato, altrimenti un Optional vuoto.
//     */
//    @Override
//    public Optional<Prodotto> trovaPerId(Long id) {
//        if (id == null) return Optional.empty();
//        // Usa archivioProdotti dell'istanza
//        return this.archivioProdotti.stream()
//                .filter(p -> id.equals(p.getId()))
//                .findFirst();
//    }
//
//    /**
//     * Restituisce una *copia* della lista di tutti i prodotti memorizzati nell'istanza.
//     *
//     * @return Una nuova lista contenente tutti i prodotti dell'istanza.
//     */
//    @Override
//    public List<Prodotto> trovaTutti() {
//        // Restituisce una copia della lista interna dell'istanza
//        return new ArrayList<>(this.archivioProdotti);
//    }
//
//    /**
//     * Trova tutti i prodotti appartenenti a una specifica categoria nell'archivio dell'istanza.
//     *
//     * @param idCategoria L'ID della categoria per cui cercare i prodotti. Se null, restituisce lista vuota.
//     * @return Una nuova lista contenente i prodotti della categoria specificata.
//     */
//    @Override
//    public List<Prodotto> trovaPerIdCategoria(Long idCategoria) {
//        if (idCategoria == null) return List.of(); // Ritorna lista vuota immutabile se idCategoria è nullo
//        // Usa archivioProdotti dell'istanza
//        return this.archivioProdotti.stream()
//                // Aggiunto controllo null per p.getCategoria() per sicurezza
//                .filter(p -> p.getCategoria() != null && idCategoria.equals(p.getCategoria().getId()))
//                .collect(Collectors.toList()); // Ritorna una nuova lista modificabile
//    }
//
//    /**
//     * Elimina un prodotto dall'archivio dell'istanza dato il suo ID.
//     *
//     * @param id L'ID del prodotto da eliminare.
//     * @throws IllegalArgumentException Se il prodotto con l'ID specificato non viene trovato.
//     */
//    @Override
//    public void elimina(Long id) {
//        // Chiama trovaPerId dell'istanza
//        Optional<Prodotto> prodottoOpt = trovaPerId(id);
//        if (prodottoOpt.isPresent()) {
//            // Usa archivioProdotti dell'istanza
//            this.archivioProdotti.remove(prodottoOpt.get());
//        } else {
//            throw new IllegalArgumentException("Prodotto non trovato per ID: " + id + " per l'eliminazione.");
//        }
//    }
//
//    /**
//     * Modifica i dati di un prodotto esistente nell'archivio dell'istanza.
//     * Questo metodo modifica direttamente l'oggetto esistente nella lista.
//     * ATTENZIONE: A differenza di `salva`, questo non rimpiazza l'oggetto, ma aggiorna i campi
//     * dell'oggetto già presente in lista. Assicurarsi che sia il comportamento desiderato.
//     *
//     * @param prodotto L'oggetto Prodotto con l'ID esistente e i nuovi dati. La Categoria associata deve essere valida.
//     * @return Il prodotto modificato (l'oggetto esistente aggiornato).
//     * @throws IllegalArgumentException Se il prodotto (o il suo ID) è nullo, o se il prodotto con l'ID specificato non viene trovato.
//     */
//    @Override
//    public Prodotto modifica(Prodotto prodotto) {
//        if (prodotto == null || prodotto.getId() == null) {
//            throw new IllegalArgumentException("Prodotto o ID del prodotto non possono essere nulli per la modifica.");
//        }
//        // Chiama trovaPerId dell'istanza
//        Optional<Prodotto> prodottoEsistenteOpt = trovaPerId(prodotto.getId());
//        if (prodottoEsistenteOpt.isEmpty()) {
//            throw new IllegalArgumentException("Prodotto non trovato per ID: " + prodotto.getId() + " per la modifica.");
//        }
//
//        Prodotto esistente = prodottoEsistenteOpt.get();
//
//        // Aggiorna i campi dell'oggetto esistente
//        esistente.setNome(prodotto.getNome());
//        esistente.setDescrizione(prodotto.getDescrizione());
//        esistente.setQuantita(prodotto.getQuantita());
//        esistente.setPercorsoImmagine(prodotto.getPercorsoImmagine());
//        esistente.setCategoria(prodotto.getCategoria()); // Assicurarsi che la categoria passata sia valida e gestita correttamente a livello di servizio
//
//        return esistente; // Restituisce l'oggetto modificato che è ancora nella lista
//    }
//}