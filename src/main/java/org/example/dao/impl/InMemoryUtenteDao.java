package org.example.dao.impl;

import org.example.dao.UtenteDao;
import org.example.entity.Utente;
import org.example.exception.EccezioneAccessoDati;
import org.example.util.CodificatorePassword; // Assumiamo che questa classe sia ok

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione di UtenteDao che utilizza un archivio in memoria per gestire gli utenti.
 * **Questa implementazione NON è thread-safe.** Utilizza un semplice ArrayList.
 */
public class InMemoryUtenteDao implements UtenteDao {
    // Lista standard per memorizzare gli utenti
    private static final List<Utente> archivioUtenti = new ArrayList<>();
    // Contatore standard per generare ID univoci per gli utenti
    private static long contatoreId = 0;

    // Blocco statico per inizializzare un utente admin di default
    static {
        // L'inizializzazione statica è eseguita una sola volta
        String email = "admin@example.com";
        String password = "password123"; // Password in chiaro solo per esempio
        String hashPassword = CodificatorePassword.calcolaHashPassword(password);

        Utente admin = new Utente(++contatoreId, email, hashPassword, "ADMIN"); // Incrementa e assegna ID
        archivioUtenti.add(admin);
        System.out.println("Utente Admin Inizializzato (lista in memoria): " + admin.getEmail());
    }

    /**
     * Trova un utente per email. Ricerca case-insensitive.
     *
     * @param email L'email dell'utente da cercare.
     * @return Un Optional contenente l'utente se trovato, altrimenti un Optional vuoto.
     */
    @Override
    public Optional<Utente> trovaPerEmail(String email) {
        if (email == null) return Optional.empty();
        return archivioUtenti.stream()
                .filter(utente -> email.equalsIgnoreCase(utente.getEmail()))
                .findFirst();
    }

    /**
     * Trova un utente per ID.
     *
     * @param id L'ID dell'utente da cercare.
     * @return Un Optional contenente l'utente se trovato, altrimenti un Optional vuoto.
     */
    @Override
    public Optional<Utente> trovaPerId(Long id) {
        if (id == null) return Optional.empty();
        return archivioUtenti.stream()
                .filter(utente -> id.equals(utente.getId()))
                .findFirst();
    }

    /**
     * Salva un utente nell'archivio in memoria.
     * Se l'utente non ha un ID, viene considerato nuovo e viene assegnato un nuovo ID.
     * Altrimenti, l'utente viene aggiornato se già presente.
     *
     *
     * @param utente L'utente da salvare, non può essere nullo.
     * @return L'utente salvato, con l'ID assegnato se nuovo.
     * @throws IllegalArgumentException Se l'utente è nullo.
     * @throws EccezioneAccessoDati In caso di email duplicata o utente non esistente durante l'aggiornamento.
     */
    @Override
    public Utente salva(Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("L'utente non può essere nullo");
        }

        if (utente.getId() == null) { // Nuovo utente
            // Verifica univocità email PRIMA di generare ID e aggiungere
            if (trovaPerEmail(utente.getEmail()).isPresent()) {
                throw new EccezioneAccessoDati("Email già esistente: " + utente.getEmail());
            }
            utente.setId(++contatoreId); // Incrementa e assegna ID
            archivioUtenti.add(utente);
        } else { // Aggiornamento utente esistente
            // Trova l'utente esistente
            Optional<Utente> optUtenteEsistente = trovaPerId(utente.getId());
            if (optUtenteEsistente.isEmpty()) {
                throw new EccezioneAccessoDati("Impossibile aggiornare utente non esistente con id: " + utente.getId());
            }
            Utente utenteEsistente = optUtenteEsistente.get();

            // Se l'email sta cambiando, verifica che la NUOVA email non sia già usata da ALTRI
            if (!utente.getEmail().equalsIgnoreCase(utenteEsistente.getEmail())) {
                // Cerca se esiste un altro utente (diverso da quello che stiamo aggiornando) con la nuova email
                if (trovaPerEmail(utente.getEmail()).filter(trovato -> !trovato.getId().equals(utente.getId())).isPresent()) {
                    throw new EccezioneAccessoDati("Impossibile aggiornare email a " + utente.getEmail() + ", è già usata da un altro utente.");
                }
            }

            // Rimuovi il vecchio e aggiungi il nuovo (versione aggiornata)
            archivioUtenti.removeIf(u -> u.getId().equals(utente.getId()));
            archivioUtenti.add(utente);
        }

        return utente; // Restituisce l'utente salvato/aggiornato
    }

}