//package org.example.dao.impl;
//
//import org.example.dao.UtenteDao;
//import org.example.entity.Utente;
//import org.example.exception.EccezioneAccessoDati;
//import org.example.util.CodificatorePassword;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Implementazione di UtenteDao che utilizza un archivio in memoria per gestire gli utenti.
// * Ora è basata su istanza (NON più statica) per una migliore testabilità.
// * **Questa implementazione NON è thread-safe.** Utilizza un semplice ArrayList.
// */
//public class InMemoryUtenteDao implements UtenteDao {
//
//    private final List<Utente> archivioUtenti = new ArrayList<>();
//    private long contatoreId = 0;
//
//    /**
//     * Trova un utente per email nell'archivio dell'istanza. Ricerca case-insensitive.
//     *
//     * @param email L'email dell'utente da cercare.
//     * @return Un Optional contenente l'utente se trovato, altrimenti un Optional vuoto.
//     */
//    @Override
//    public Optional<Utente> trovaPerEmail(String email) {
//        if (email == null) return Optional.empty();
//        // Usa la lista dell'istanza
//        return this.archivioUtenti.stream()
//                .filter(utente -> email.equalsIgnoreCase(utente.getEmail()))
//                .findFirst();
//    }
//
//    /**
//     * Trova un utente per ID nell'archivio dell'istanza.
//     *
//     * @param id L'ID dell'utente da cercare.
//     * @return Un Optional contenente l'utente se trovato, altrimenti un Optional vuoto.
//     */
//    @Override
//    public Optional<Utente> trovaPerId(Long id) {
//        if (id == null) return Optional.empty();
//        // Usa la lista dell'istanza
//        return this.archivioUtenti.stream()
//                .filter(utente -> id.equals(utente.getId()))
//                .findFirst();
//    }
//
//    /**
//     * Salva un utente nell'archivio in memoria dell'istanza.
//     * Se l'utente non ha un ID, viene considerato nuovo e viene assegnato un nuovo ID.
//     * Altrimenti, l'utente viene aggiornato se già presente (rimpiazzando l'elemento esistente).
//     * Controlla l'univocità dell'email.
//     *
//     * @param utente L'utente da salvare, non può essere nullo.
//     * @return L'utente salvato, con l'ID assegnato se nuovo o aggiornato.
//     * @throws IllegalArgumentException Se l'utente è nullo.
//     * @throws EccezioneAccessoDati In caso di email duplicata (per nuovi utenti o durante aggiornamenti con cambio email)
//     *                              o utente non esistente durante un tentativo di aggiornamento.
//     */
//    @Override
//    public Utente salva(Utente utente) {
//        if (utente == null) {
//            throw new IllegalArgumentException("L'utente non può essere nullo");
//        }
//
//        if (utente.getId() == null) { // Nuovo utente
//            // Verifica univocità email PRIMA di generare ID e aggiungere
//            if (trovaPerEmail(utente.getEmail()).isPresent()) {
//                throw new EccezioneAccessoDati("Email già esistente: " + utente.getEmail());
//            }
//            // Usa contatoreId e archivioUtenti dell'istanza
//            utente.setId(++this.contatoreId); // Incrementa e assegna ID
//            //Aggiunge l'utente SOLO UNA VOLTA
//            this.archivioUtenti.add(utente);
//
//        } else { // Aggiornamento utente esistente
//            // Trova l'utente esistente usando trovaPerId dell'istanza
//            Optional<Utente> optUtenteEsistente = trovaPerId(utente.getId());
//            if (optUtenteEsistente.isEmpty()) {
//                throw new EccezioneAccessoDati("Impossibile aggiornare utente non esistente con id: " + utente.getId());
//            }
//            Utente utenteEsistente = optUtenteEsistente.get();
//
//            // Se l'email sta cambiando, verifica che la NUOVA email non sia già usata da ALTRI
//            if (!utente.getEmail().equalsIgnoreCase(utenteEsistente.getEmail())) {
//                String targetEmail = utente.getEmail();
//                Long idBeingUpdated = utente.getId();
//
//                // Cerca se esiste un altro utente (diverso da quello che stiamo aggiornando) con la nuova email
//                Optional<Utente> duplicateOpt = this.archivioUtenti.stream()
//                        .filter(u -> targetEmail.equalsIgnoreCase(u.getEmail()) && !u.getId().equals(idBeingUpdated))
//                        .findFirst();
//
//                if (duplicateOpt.isPresent()) {
//                    // Se troviamo un duplicato (che non sia l'utente stesso), lanciamo l'eccezione
//                    throw new EccezioneAccessoDati("Impossibile aggiornare email a " + targetEmail + ", è già usata da un altro utente.");
//                }
//            }
//
//            // Se i controlli passano, procedi con l'aggiornamento:
//            // Rimuovi il vecchio e aggiungi il nuovo (versione aggiornata)
//            this.archivioUtenti.removeIf(u -> u.getId().equals(utente.getId()));
//            this.archivioUtenti.add(utente); // Aggiunge la versione aggiornata passata come argomento
//        }
//
//        return utente; // Restituisce l'utente salvato/aggiornato
//    }
//}