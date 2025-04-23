package org.example.dto;

/**
 * Data Transfer Object (DTO) per le richieste di login.
 * Questa classe viene utilizzata per trasferire le credenziali di login (email e password)
 * dal client al server.
 */
public class RichiestaLoginDTO {
    // Indirizzo email dell'utente.
    private String email;
    // Password dell'utente.
    private String password;

    /**
     * Costruttore per inizializzare un oggetto RichiestaLoginDTO con email e password.
     *
     * @param email    Indirizzo email dell'utente.
     * @param password Password dell'utente.
     */
    public RichiestaLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Restituisce l'indirizzo email dell'utente.
     *
     * @return l'email dell'utente.
     */
    public String getEmail() { return email; }

    /**
     * Restituisce la password dell'utente.
     *
     * @return la password dell'utente.
     */
    public String getPassword() { return password; }
}