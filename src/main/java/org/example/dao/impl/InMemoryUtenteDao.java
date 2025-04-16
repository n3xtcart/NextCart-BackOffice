package org.example.dao.impl;

import org.example.dao.UtenteDao;
import org.example.entity.Utente;
import org.example.exception.EccezioneAccessoDati;
import org.example.util.CodificatorePassword;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementazione di UtenteDao che utilizza un archivio in memoria per gestire gli utenti.
 * La classe è thread-safe e utilizza un ReentrantLock per garantire la consistenza durante le operazioni.
 */
public class InMemoryUtenteDao implements UtenteDao {
    // Lista thread-safe per memorizzare gli utenti
    private static final List<Utente> archivioUtenti = new CopyOnWriteArrayList<>();
    // Contatore atomico per generare ID univoci per gli utenti
    private static final AtomicLong contatoreId = new AtomicLong(0);
    // Lock per garantire atomicità nelle operazioni composite
    private static final ReentrantLock blocco = new ReentrantLock();

    // Blocco statico per inizializzare un utente admin di default
    static {
        String email = "admin@example.com";
        String password = "password123"; // Password in chiaro solo per esempio
        String hashPassword = CodificatorePassword.calcolaHashPassword(password);

        Utente admin = new Utente(contatoreId.incrementAndGet(), email, hashPassword, "ADMIN");
        archivioUtenti.add(admin);
        System.out.println("Utente Admin Inizializzato (lista in memoria): " + admin.getEmail());
    }

    /**
     * Trova un utente per email.
     *
     * @param email L'email dell'utente da cercare.
     * @return Un Optional contenente l'utente se trovato, altrimenti un Optional vuoto.
     */
    @Override
    public Optional<Utente> trovaPerEmail(String email) {
        if (email == null) return Optional.empty();
        // Itera sulla lista per trovare l'utente con email corrispondente (case-insensitive)
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
        // Itera sulla lista per trovare l'utente con ID corrispondente
        return archivioUtenti.stream()
                .filter(utente -> id.equals(utente.getId()))
                .findFirst();
    }

    /**
     * Salva un utente nell'archivio in memoria.
     * Se l'utente non ha un ID, viene considerato nuovo e viene assegnato un nuovo ID.
     * Altrimenti, l'utente viene aggiornato se già presente.
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

        blocco.lock(); // Blocca per operazioni composite
        try {
            if (utente.getId() == null) { // Nuovo utente
                // Verifica univocità email PRIMA di generare ID e aggiungere
                if (trovaPerEmail(utente.getEmail()).isPresent()) {
                    throw new EccezioneAccessoDati("Email già esistente: " + utente.getEmail());
                }
                utente.setId(contatoreId.incrementAndGet());
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
                    if (trovaPerEmail(utente.getEmail()).filter(trovato -> !trovato.getId().equals(utente.getId())).isPresent()) {
                        throw new EccezioneAccessoDati("Impossibile aggiornare email a " + utente.getEmail() + ", è già usata da un altro utente.");
                    }
                }

                // Rimuovi il vecchio e aggiungi il nuovo (CopyOnWriteArrayList non permette modifiche in-place efficienti)
                archivioUtenti.removeIf(u -> u.getId().equals(utente.getId()));
                archivioUtenti.add(utente); // Aggiunge la versione aggiornata
            }
        } finally {
            blocco.unlock(); // Rilascia il lock SEMPRE
        }
        return utente; // Restituisce l'utente salvato/aggiornato
    }
}